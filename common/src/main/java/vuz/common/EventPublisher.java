package vuz.common;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class EventPublisher {

    private final RabbitTemplate rabbit;
    private final JsonMapper json;

    public EventPublisher(RabbitTemplate rabbit, JsonMapper json) {
        this.rabbit = rabbit;
        this.json = json;
    }

    // сообщение сохраняется на диск брокера, ждём подтверждение что брокер его принял
    public void publish(String event, Map<String, Object> data) {
        String body = json.writeValueAsString(data);

        MessageProperties properties = new MessageProperties();
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        Message message = new Message(body.getBytes(StandardCharsets.UTF_8), properties);

        rabbit.invoke(operations -> {
            operations.send(Events.EXCHANGE, event, message);
            operations.waitForConfirmsOrDie(5000);
            return true;
        });
        System.out.println("-> " + event + " " + body);
    }
}
