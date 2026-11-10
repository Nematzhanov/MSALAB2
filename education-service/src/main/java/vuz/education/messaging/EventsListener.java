package vuz.education.messaging;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import vuz.common.EventPublisher;
import vuz.common.Events;
import vuz.education.service.CascadeService;

import java.util.List;
import java.util.Map;

@Component
public class EventsListener {

    private final CascadeService cascade;
    private final EventPublisher events;
    private final JsonMapper json;

    public EventsListener(CascadeService cascade, EventPublisher events, JsonMapper json) {
        this.cascade = cascade;
        this.events = events;
        this.json = json;
    }

    @RabbitListener(queues = QueueConfig.QUEUE)
    public void onEvent(Message message) {
        String event = message.getMessageProperties().getReceivedRoutingKey();
        Map<String, Object> data = Events.read(json, message);

        List<CascadeService.Change> changes = cascade.apply(event, data);
        System.out.println("<- " + event + " " + Events.body(message));

        for (CascadeService.Change change : changes) {
            events.publish(change.event(), change.education().toEvent());
        }
    }
}
