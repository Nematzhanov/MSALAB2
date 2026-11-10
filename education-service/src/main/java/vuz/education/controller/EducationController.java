package vuz.education.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vuz.common.ApiException;
import vuz.common.EventPublisher;
import vuz.education.entity.Education;
import vuz.education.entity.GroupRef;
import vuz.education.repository.EducationRepository;
import vuz.education.repository.GroupRefRepository;
import vuz.education.repository.PersonRefRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/educations")
@Tag(name = "Обучение")
public class EducationController {

    public static final Map<String, Set<String>> TRANSITIONS = Map.of(
            "активен", Set.of("академический отпуск", "отчислен", "завершил обучение"),
            "академический отпуск", Set.of("активен"),
            "отчислен", Set.of(),
            "завершил обучение", Set.of());

    private final EducationRepository educations;
    private final PersonRefRepository persons;
    private final GroupRefRepository groups;
    private final EventPublisher events;

    public EducationController(EducationRepository educations, PersonRefRepository persons,
                               GroupRefRepository groups, EventPublisher events) {
        this.educations = educations;
        this.persons = persons;
        this.groups = groups;
        this.events = events;
    }

    @Operation(summary = "Список записей обучения")
    @GetMapping
    public List<Education> list() {
        return educations.findAll(Sort.by("id"));
    }

    @Operation(summary = "Запись обучения по id")
    @GetMapping("/{id}")
    public Education get(@PathVariable int id) {
        return find(id);
    }

    @Operation(summary = "Добавить студента в группу", description = "Статус всегда «активен». В закрытую группу добавить нельзя")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Education create(@Valid @RequestBody Education education) {
        checkDates(education);
        checkPerson(education.getPersonId());
        GroupRef group = checkGroup(education.getGroupId());
        if (!"актуальна".equals(group.getStatus())) {
            throw ApiException.conflict("Группа с id = " + group.getId() + " закрыта, добавить в неё студента нельзя");
        }

        education.setId(null);
        education.setStatus("активен");
        Education saved = educations.saveAndFlush(education);
        events.publish("education.created", saved.toEvent());
        return saved;
    }

    @Operation(summary = "Изменить запись обучения", description = "Статус меняется только по правилам (активен <-> академический отпуск, активен -> отчислен / завершил обучение)")
    @PutMapping("/{id}")
    public Education update(@PathVariable int id, @Valid @RequestBody Education education) {
        Education old = find(id);
        String oldStatus = old.getStatus();
        String newStatus = education.getStatus();

        if (!oldStatus.equals(newStatus)) {
            if (!TRANSITIONS.containsKey(newStatus)) {
                throw ApiException.badRequest("Неизвестный статус: " + newStatus);
            }
            if (!TRANSITIONS.get(oldStatus).contains(newStatus)) {
                throw ApiException.conflict("Недопустимый переход статуса: «" + oldStatus + "» -> «" + newStatus + "»");
            }
        }
        checkDates(education);
        checkPerson(education.getPersonId());
        GroupRef group = checkGroup(education.getGroupId());
        if (!group.getId().equals(old.getGroupId()) && !"актуальна".equals(group.getStatus())) {
            throw ApiException.conflict("Группа с id = " + group.getId() + " закрыта, перевести в неё студента нельзя");
        }

        education.setId(id);
        Education saved = educations.saveAndFlush(education);
        events.publish("education.updated", saved.toEvent());
        return saved;
    }

    @Operation(summary = "Удалить запись обучения")
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable int id) {
        Education education = find(id);
        educations.delete(education);
        educations.flush();
        events.publish("education.deleted", education.toEvent());
        return Map.of("message", "Обучение удалено");
    }

    private void checkDates(Education education) {
        if (education.getEndDate() != null && education.getEndDate().isBefore(education.getStartDate())) {
            throw ApiException.badRequest("Ошибка в данных: дата окончания раньше даты начала");
        }
    }

    // персона и группа в других сервисах, проверяем по своим копиям
    private void checkPerson(Integer personId) {
        if (!persons.existsById(personId)) {
            throw ApiException.conflict("Нарушена связь между таблицами: персона с id = " + personId + " не найдена");
        }
    }

    private GroupRef checkGroup(Integer groupId) {
        return groups.findById(groupId).orElseThrow(() ->
                ApiException.conflict("Нарушена связь между таблицами: группа с id = " + groupId + " не найдена"));
    }

    private Education find(int id) {
        return educations.findById(id).orElseThrow(() -> ApiException.notFound("Обучение"));
    }
}
