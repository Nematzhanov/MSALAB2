package vuz.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vuz.common.ApiException;
import vuz.common.EventPublisher;
import vuz.group.entity.PersonRef;
import vuz.group.entity.Specialty;
import vuz.group.entity.StudyGroup;
import vuz.group.repository.EducationRefRepository;
import vuz.group.repository.PersonRefRepository;
import vuz.group.repository.SpecialtyRepository;
import vuz.group.repository.StudyGroupRepository;
import vuz.group.service.GroupEvents;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups")
@Tag(name = "Группы")
public class StudyGroupController {

    private final StudyGroupRepository groups;
    private final SpecialtyRepository specialties;
    private final EducationRefRepository educations;
    private final PersonRefRepository persons;
    private final GroupEvents groupEvents;
    private final EventPublisher events;

    public StudyGroupController(StudyGroupRepository groups, SpecialtyRepository specialties,
                                EducationRefRepository educations, PersonRefRepository persons,
                                GroupEvents groupEvents, EventPublisher events) {
        this.groups = groups;
        this.specialties = specialties;
        this.educations = educations;
        this.persons = persons;
        this.groupEvents = groupEvents;
        this.events = events;
    }

    @Operation(summary = "Список групп")
    @GetMapping
    public List<StudyGroup> list() {
        return groups.findAll(Sort.by("id"));
    }

    @Operation(summary = "Группа по id")
    @GetMapping("/{id}")
    public StudyGroup get(@PathVariable int id) {
        return find(id);
    }

    @Operation(summary = "Добавить группу", description = "Специальность должна быть актуальной, курс не больше срока обучения")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudyGroup create(@Valid @RequestBody StudyGroup group) {
        Specialty specialty = checkSpecialty(group);
        if (!"актуально".equals(specialty.getStatus())) {
            throw ApiException.conflict("Специальность «" + specialty.getName() + "» неактуальна, новые группы по ней создавать нельзя");
        }
        group.setId(null);
        group.setStatus("актуальна");
        StudyGroup saved = groups.saveAndFlush(group);
        groupEvents.send("group.created", saved);
        return saved;
    }

    @Operation(summary = "Изменить группу")
    @PutMapping("/{id}")
    public StudyGroup update(@PathVariable int id, @Valid @RequestBody StudyGroup group) {
        find(id);
        checkSpecialty(group);
        group.setId(id);
        StudyGroup saved = groups.saveAndFlush(group);
        groupEvents.send("group.updated", saved);
        return saved;
    }

    @Operation(summary = "Удалить группу", description = "Если в группе кто-то учился - не удаляется, а закрывается")
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable int id) {
        StudyGroup group = find(id);

        long count = educations.countByGroupId(id);
        if (count > 0) {
            group.setStatus("закрыта");
            StudyGroup saved = groups.saveAndFlush(group);
            groupEvents.send("group.updated", saved);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "В группе есть записи «Обучение» (" + count + "), поэтому она не удалена, а закрыта");
            result.put("group", saved);
            return result;
        }

        groups.delete(group);
        groups.flush();
        events.publish("group.deleted", Map.of("id", id));
        return Map.of("message", "Группа удалена");
    }

    @Operation(summary = "Студенты группы", description = "Персоны, у которых обучение в этой группе со статусом «активен»")
    @GetMapping("/{id}/students")
    public List<PersonRef> students(@PathVariable int id) {
        find(id);
        return persons.findStudentsOfGroup(id);
    }

    // специальность должна существовать, а курс не может быть больше срока обучения
    private Specialty checkSpecialty(StudyGroup group) {
        Specialty specialty = specialties.findById(group.getSpecialtyId())
                .orElseThrow(() -> ApiException.conflict("Нарушена связь между таблицами: специальность с id = "
                        + group.getSpecialtyId() + " не найдена"));
        if (group.getCourse() > specialty.getStudyYears()) {
            throw ApiException.badRequest("Ошибка в данных: курс " + group.getCourse()
                    + " больше срока обучения специальности (" + specialty.getStudyYears() + ")");
        }
        return specialty;
    }

    private StudyGroup find(int id) {
        return groups.findById(id).orElseThrow(() -> ApiException.notFound("Группа"));
    }
}
