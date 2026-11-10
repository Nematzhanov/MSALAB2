package vuz.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vuz.common.ApiException;
import vuz.group.entity.PersonRef;
import vuz.group.entity.Specialty;
import vuz.group.entity.StudyGroup;
import vuz.group.repository.PersonRefRepository;
import vuz.group.repository.SpecialtyRepository;
import vuz.group.repository.StudyGroupRepository;
import vuz.group.service.GroupEvents;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specialties")
@Tag(name = "Специальности")
public class SpecialtyController {

    private final SpecialtyRepository specialties;
    private final StudyGroupRepository groups;
    private final PersonRefRepository persons;
    private final GroupEvents groupEvents;

    public SpecialtyController(SpecialtyRepository specialties, StudyGroupRepository groups,
                               PersonRefRepository persons, GroupEvents groupEvents) {
        this.specialties = specialties;
        this.groups = groups;
        this.persons = persons;
        this.groupEvents = groupEvents;
    }

    @Operation(summary = "Список специальностей")
    @GetMapping
    public List<Specialty> list() {
        return specialties.findAll(Sort.by("id"));
    }

    @Operation(summary = "Специальность по id")
    @GetMapping("/{id}")
    public Specialty get(@PathVariable int id) {
        return find(id);
    }

    @Operation(summary = "Добавить специальность", description = "Статус всегда «актуально»")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Specialty create(@Valid @RequestBody Specialty specialty) {
        specialty.setId(null);
        specialty.setStatus("актуально");
        return specialties.saveAndFlush(specialty);
    }

    @Operation(summary = "Изменить специальность")
    @PutMapping("/{id}")
    public Specialty update(@PathVariable int id, @Valid @RequestBody Specialty specialty) {
        find(id);
        specialty.setId(id);
        Specialty saved = specialties.saveAndFlush(specialty);

        // название специальности хранится в копиях у сервиса персон, отправляем обновлённые группы
        for (StudyGroup group : groups.findBySpecialtyId(id)) {
            groupEvents.send("group.updated", group);
        }
        return saved;
    }

    @Operation(summary = "Удалить специальность", description = "Если по ней есть группы - не удаляется, а становится «неактуально»")
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable int id) {
        Specialty specialty = find(id);

        long count = groups.countBySpecialtyId(id);
        if (count > 0) {
            specialty.setStatus("неактуально");
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "Специальность используется группами (" + count + "), поэтому не удалена, а переведена в статус «неактуально»");
            result.put("specialty", specialties.saveAndFlush(specialty));
            return result;
        }

        specialties.delete(specialty);
        specialties.flush();
        return Map.of("message", "Специальность удалена");
    }

    @Operation(summary = "Студенты специальности", description = "Активные студенты всех групп этой специальности")
    @GetMapping("/{id}/students")
    public List<PersonRef> students(@PathVariable int id) {
        find(id);
        return persons.findStudentsOfSpecialty(id);
    }

    private Specialty find(int id) {
        return specialties.findById(id).orElseThrow(() -> ApiException.notFound("Специальность"));
    }
}
