package vuz.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vuz.common.EventPublisher;
import vuz.group.entity.StudyGroup;
import vuz.group.repository.StudyGroupRepository;
import vuz.group.service.EndOfYearService;
import vuz.group.service.GroupEvents;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Tag(name = "События")
public class EndOfYearController {

    private final EndOfYearService service;
    private final StudyGroupRepository groups;
    private final GroupEvents groupEvents;
    private final EventPublisher events;

    public EndOfYearController(EndOfYearService service, StudyGroupRepository groups,
                               GroupEvents groupEvents, EventPublisher events) {
        this.service = service;
        this.groups = groups;
        this.groupEvents = groupEvents;
        this.events = events;
    }

    @Operation(summary = "Конец учебного года",
            description = "Курс +1 у актуальных групп; выпускные группы закрываются, их активные студенты завершают обучение")
    @PostMapping("/events/end-of-year")
    public Map<String, Object> endOfYear() {
        EndOfYearService.Result result = service.run();

        // транзакция уже сохранена, теперь рассылаем события
        String today = LocalDate.now().toString();
        for (Integer id : result.closed()) {
            events.publish("group.graduated", Map.of("id", id, "date", today));
        }
        for (StudyGroup group : groups.findAllById(result.closed())) {
            groupEvents.send("group.updated", group);
        }
        for (StudyGroup group : groups.findAllById(result.promoted())) {
            groupEvents.send("group.updated", group);
        }

        Map<String, Object> answer = new LinkedHashMap<>();
        answer.put("message", "Учебный год завершён");
        answer.put("groups_promoted", result.promoted().size());
        answer.put("groups_closed", result.closed().size());
        answer.put("educations_completed", result.completed());
        return answer;
    }
}
