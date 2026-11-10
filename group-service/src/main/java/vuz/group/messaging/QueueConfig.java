package vuz.group.messaging;

import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vuz.common.Events;

@Configuration
public class QueueConfig {

    public static final String QUEUE = "group-service";

    @Bean
    public Declarables groupQueue(TopicExchange exchange) {
        return Events.queue(QUEUE, exchange,
                "education.created", "education.updated", "education.deleted",
                "person.created", "person.updated", "person.deleted");
    }
}
