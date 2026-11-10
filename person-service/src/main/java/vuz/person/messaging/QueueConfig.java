package vuz.person.messaging;

import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vuz.common.Events;

@Configuration
public class QueueConfig {

    public static final String QUEUE = "person-service";

    @Bean
    public Declarables personQueue(TopicExchange exchange) {
        return Events.queue(QUEUE, exchange,
                "education.created", "education.updated", "education.deleted",
                "group.created", "group.updated", "group.deleted");
    }
}
