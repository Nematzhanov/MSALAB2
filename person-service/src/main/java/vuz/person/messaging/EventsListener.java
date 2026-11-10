package vuz.person.messaging;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import vuz.common.Events;
import vuz.person.entity.EducationRef;
import vuz.person.entity.GroupRef;
import vuz.person.repository.EducationRefRepository;
import vuz.person.repository.GroupRefRepository;

import java.util.Map;

// обновляем свои копии данных других сервисов
@Component
public class EventsListener {

    private final EducationRefRepository educations;
    private final GroupRefRepository groups;
    private final JsonMapper json;

    public EventsListener(EducationRefRepository educations, GroupRefRepository groups, JsonMapper json) {
        this.educations = educations;
        this.groups = groups;
        this.json = json;
    }

    @RabbitListener(queues = QueueConfig.QUEUE)
    public void onEvent(Message message) {
        String event = message.getMessageProperties().getReceivedRoutingKey();
        Map<String, Object> data = Events.read(json, message);

        switch (event) {
            case "education.created", "education.updated" -> {
                EducationRef ref = new EducationRef();
                ref.setEducationId(Events.getInt(data, "id"));
                ref.setPersonId(Events.getInt(data, "person_id"));
                ref.setGroupId(Events.getInt(data, "group_id"));
                ref.setStartDate(Events.getDate(data, "start_date"));
                ref.setEndDate(Events.getDate(data, "end_date"));
                ref.setStatus(Events.getString(data, "status"));
                educations.save(ref);
            }
            case "education.deleted" -> educations.deleteById(Events.getInt(data, "id"));
            case "group.created", "group.updated" -> {
                GroupRef ref = new GroupRef();
                ref.setId(Events.getInt(data, "id"));
                ref.setName(Events.getString(data, "name"));
                ref.setStartYear(Events.getInt(data, "start_year"));
                ref.setCourse(Events.getInt(data, "course"));
                ref.setStatus(Events.getString(data, "status"));
                ref.setSpecialtyName(Events.getString(data, "specialty_name"));
                groups.save(ref);
            }
            case "group.deleted" -> groups.deleteById(Events.getInt(data, "id"));
            default -> { }
        }
        System.out.println("<- " + event + " " + Events.body(message));
    }
}
