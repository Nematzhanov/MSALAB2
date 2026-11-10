package vuz.common;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Events {

    public static final String EXCHANGE = "vuz.events";

    // создаем очередь и подписываем его с нуж сервисам
    public static Declarables queue(String name, TopicExchange exchange, String... events) {
        Queue queue = new Queue(name, true);
        List<Declarable> list = new ArrayList<>();
        list.add(queue);
        for (String event : events) {
            //привяжи очеред -> к обект -> по теме
            Binding binding = BindingBuilder.bind(queue).to(exchange).with(event);
            list.add(binding);
        }
        return new Declarables(list);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> read(JsonMapper json, Message message) {
        return json.readValue(message.getBody(), Map.class);
    }

    public static String body(Message message) {
        return new String(message.getBody(), StandardCharsets.UTF_8);
    }

    public static Integer getInt(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value == null ? null : ((Number) value).intValue();
    }

    public static LocalDate getDate(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value == null ? null : LocalDate.parse(value.toString());
    }

    public static String getString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value == null ? null : value.toString();
    }
}
