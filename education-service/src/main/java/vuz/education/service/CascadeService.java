package vuz.education.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuz.common.Events;
import vuz.education.entity.Education;
import vuz.education.entity.GroupRef;
import vuz.education.entity.PersonRef;
import vuz.education.repository.EducationRepository;
import vuz.education.repository.GroupRefRepository;
import vuz.education.repository.PersonRefRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// реакция на события других сервисов.
@Service
public class CascadeService {

    public record Change(String event, Education education) { }

    private final EducationRepository educations;
    private final PersonRefRepository persons;
    private final GroupRefRepository groups;

    public CascadeService(EducationRepository educations, PersonRefRepository persons, GroupRefRepository groups) {
        this.educations = educations;
        this.persons = persons;
        this.groups = groups;
    }

    @Transactional
    public List<Change> apply(String event, Map<String, Object> data) {
        Integer id = Events.getInt(data, "id");
        List<Change> changes = new ArrayList<>();

        switch (event) {
            case "person.created" -> persons.save(new PersonRef(id));

            // каскадное удаление
            case "person.deleted" -> {
                List<Education> list = educations.findByPersonId(id);
                educations.deleteAll(list);
                persons.deleteById(id);
                for (Education e : list) {
                    changes.add(new Change("education.deleted", e));
                }
            }

            case "group.created", "group.updated" -> groups.save(new GroupRef(id, Events.getString(data, "status")));
            case "group.deleted" -> groups.deleteById(id);

            // каскадное изменение: группа выпустилась - активные студенты завершили обучение
            case "group.graduated" -> {
                List<Education> list = educations.findByGroupIdAndStatus(id, "активен");
                for (Education e : list) {
                    e.setStatus("завершил обучение");
                    e.setEndDate(Events.getDate(data, "date"));
                    changes.add(new Change("education.updated", e));
                }
                educations.saveAll(list);
            }
            default -> { }
        }
        return changes;
    }
}
