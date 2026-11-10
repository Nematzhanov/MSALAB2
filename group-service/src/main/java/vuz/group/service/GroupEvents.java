package vuz.group.service;

import org.springframework.stereotype.Component;
import vuz.common.EventPublisher;
import vuz.group.entity.Specialty;
import vuz.group.entity.StudyGroup;
import vuz.group.repository.SpecialtyRepository;

import java.util.LinkedHashMap;
import java.util.Map;

// отправка событий group.* (сервису персон нужны название группы и специальности для истории)
@Component
public class GroupEvents {

    private final SpecialtyRepository specialties;
    private final EventPublisher publisher;

    public GroupEvents(SpecialtyRepository specialties, EventPublisher publisher) {
        this.specialties = specialties;
        this.publisher = publisher;
    }

    public void send(String event, StudyGroup group) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", group.getId());
        data.put("name", group.getName());
        data.put("start_year", group.getStartYear());
        data.put("course", group.getCourse());
        data.put("status", group.getStatus());
        data.put("specialty_id", group.getSpecialtyId());
        data.put("specialty_name", specialties.findById(group.getSpecialtyId()).map(Specialty::getName).orElse(null));
        publisher.publish(event, data);
    }
}
