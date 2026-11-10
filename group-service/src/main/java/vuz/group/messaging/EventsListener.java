package vuz.group.messaging;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import vuz.common.Events;
import vuz.group.entity.EducationRef;
import vuz.group.entity.PersonRef;
import vuz.group.repository.EducationRefRepository;
import vuz.group.repository.PersonRefRepository;

import java.util.Map;

// обновляем копии обучения и персон
@Component
public class EventsListener {

    private final EducationRefRepository educations;
    private final PersonRefRepository persons;
    private final JsonMapper json;

    public EventsListener(EducationRefRepository educations, PersonRefRepository persons, JsonMapper json) {
        this.educations = educations;
        this.persons = persons;
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
                ref.setStatus(Events.getString(data, "status"));
                educations.save(ref);
            }
            case "education.deleted" -> educations.deleteById(Events.getInt(data, "id"));
            case "person.created", "person.updated" -> {
                PersonRef ref = new PersonRef();
                ref.setId(Events.getInt(data, "id"));
                ref.setName(Events.getString(data, "name"));
                ref.setSurname(Events.getString(data, "surname"));
                ref.setBirthDate(Events.getDate(data, "birth_date"));
                ref.setGender(Events.getString(data, "gender"));
                ref.setSnils(Events.getString(data, "snils"));
                persons.save(ref);
            }
            case "person.deleted" -> persons.deleteById(Events.getInt(data, "id"));
            default -> { }
        }
        System.out.println("<- " + event + " " + Events.body(message));
    }
}
