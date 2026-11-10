package vuz.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    // точка обмена в RabbitMQ, создаётся при запуске если её нет
    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(Events.EXCHANGE, true, false);
    }

    // заголовок swagger
    @Bean
    public OpenAPI openApi(@Value("${spring.application.name}") String name) {
        return new OpenAPI().info(new Info()
                .title(name)
                .version("1.0")
                .description("ЛР2 «Разбиение на микросервисы», модель «ВУЗ»"));
    }
}
