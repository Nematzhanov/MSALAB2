package vuz.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import vuz.person.entity.EducationRef;

import java.util.List;

public interface EducationRefRepository extends JpaRepository<EducationRef, Integer> {

    long countByPersonId(Integer personId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM education_ref WHERE person_id = ?1")
    int deleteByPersonId(Integer personId);

    @Query(nativeQuery = true, value =
            "SELECT e.education_id AS \"educationId\", e.group_id AS \"groupId\", g.name AS \"groupName\", " +
            "g.start_year AS \"startYear\", g.specialty_name AS \"specialty\", g.course AS \"course\", " +
            "CAST(e.start_date AS varchar) AS \"startDate\", CAST(e.end_date AS varchar) AS \"endDate\", " +
            "e.status AS \"status\" " +
            "FROM education_ref e LEFT JOIN group_ref g ON g.id = e.group_id " +
            "WHERE e.person_id = ?1 ORDER BY e.start_date")
    List<HistoryRow> findHistory(Integer personId);
}
