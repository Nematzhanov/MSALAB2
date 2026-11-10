package vuz.group.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuz.group.repository.EducationRefRepository;
import vuz.group.repository.StudyGroupRepository;

import java.util.List;

@Service
public class EndOfYearService {

    public record Result(List<Integer> closed, List<Integer> promoted, long completed) { }

    private final StudyGroupRepository groups;
    private final EducationRefRepository educations;

    public EndOfYearService(StudyGroupRepository groups, EducationRefRepository educations) {
        this.groups = groups;
        this.educations = educations;
    }

    // всё в одной транзакции.
    // сначала находим выпускные группы, потом поднимаем курс остальным,
    // иначе группа которая только что стала на последний курс сразу закроется
    @Transactional
    public Result run() {
        List<Integer> closed = groups.findGraduatingIds();
        List<Integer> promoted = groups.findPromotableIds();

        long completed = 0;
        for (Integer id : closed) {
            completed += educations.countByGroupIdAndStatus(id, "активен");
        }

        if (!closed.isEmpty()) groups.close(closed);
        if (!promoted.isEmpty()) groups.promote(promoted);
        return new Result(closed, promoted, completed);
    }
}
