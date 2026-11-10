package vuz.person.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vuz.common.ApiException;
import vuz.common.EventPublisher;
import vuz.person.entity.Person;
import vuz.person.repository.EducationRefRepository;
import vuz.person.repository.HistoryRow;
import vuz.person.repository.PersonRepository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/persons")
@Tag(name = "Персоны")
public class PersonController {

    private final PersonRepository persons;
    private final EducationRefRepository educations;
    private final EventPublisher events;

    public PersonController(PersonRepository persons, EducationRefRepository educations, EventPublisher events) {
        this.persons = persons;
        this.educations = educations;
        this.events = events;
    }

    @Operation(summary = "Список персон")
    @GetMapping
    public List<Person> list() {
        return persons.findAll(Sort.by("id"));
    }

    @Operation(summary = "Персона по id")
    @GetMapping("/{id}")
    public Person get(@PathVariable int id) {
        return find(id);
    }

    @Operation(summary = "Добавить персону")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Person create(@Valid @RequestBody Person person) {
        person.setId(null);
        Person saved = persons.saveAndFlush(person);
        events.publish("person.created", saved.toEvent());
        return saved;
    }

    @Operation(summary = "Изменить персону")
    @PutMapping("/{id}")
    public Person update(@PathVariable int id, @Valid @RequestBody Person person) {
        find(id);
        person.setId(id);
        Person saved = persons.saveAndFlush(person);
        events.publish("person.updated", saved.toEvent());
        return saved;
    }

    @Operation(summary = "Удалить персону", description = "Её обучения удалит сервис обучения, когда получит событие person.deleted")
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable int id) {
        find(id);
        long count = educations.countByPersonId(id);
        persons.deleteById(id);
        persons.flush();
        educations.deleteByPersonId(id);
        events.publish("person.deleted", Map.of("id", id));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Персона удалена");
        result.put("deleted_educations", count);
        return result;
    }

    @Operation(summary = "Поиск отчисленных", description = "Можно указать дату рождения и/или пол")
    @GetMapping("/expelled")
    public List<Person> expelled(
            @RequestParam(name = "birth_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(required = false) String gender) {
        return persons.findExpelled(birthDate, gender);
    }

    @Operation(summary = "Информация о студенте", description = "Персона и вся её история обучения: группы, специальности, курсы, даты, статусы")
    @GetMapping("/{id}/history")
    public Map<String, Object> history(@PathVariable int id) {
        Person person = find(id);
        List<HistoryRow> rows = educations.findHistory(id);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("person", person);
        result.put("educations", rows);
        return result;
    }

    private Person find(int id) {
        return persons.findById(id).orElseThrow(() -> ApiException.notFound("Персона"));
    }
}
