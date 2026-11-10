package vuz.education.messaging;

import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vuz.common.Events;

@Configuration
public class QueueConfig {

    public static final String QUEUE = "education-service";

    @Bean
    public Declarables educationQueue(TopicExchange exchange) {
        return Events.queue(QUEUE, exchange,
                "person.created", "person.deleted",
                "group.created", "group.updated", "group.deleted", "group.graduated");
    }
}