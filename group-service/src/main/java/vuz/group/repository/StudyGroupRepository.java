package vuz.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vuz.group.entity.StudyGroup;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Integer> {

    long countBySpecialtyId(Integer specialtyId);

    List<StudyGroup> findBySpecialtyId(Integer specialtyId);

    // выпускные: актуальная группа, курс >= срока обучения специальности
    @Query(nativeQuery = true, value =
            "SELECT g.id FROM study_group g JOIN specialty s ON s.id = g.specialty_id " +
            "WHERE g.status = 'актуальна' AND g.course >= s.study_years")
    List<Integer> findGraduatingIds();

    @Query(nativeQuery = true, value =
            "SELECT g.id FROM study_group g JOIN specialty s ON s.id = g.specialty_id " +
            "WHERE g.status = 'актуальна' AND g.course < s.study_years")
    List<Integer> findPromotableIds();

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE study_group SET status = 'закрыта' WHERE id IN (?1)")
    int close(List<Integer> ids);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE study_group SET course = course + 1 WHERE id IN (?1)")
    int promote(List<Integer> ids);
}
