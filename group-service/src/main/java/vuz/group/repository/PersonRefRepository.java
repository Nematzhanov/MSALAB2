package vuz.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vuz.group.entity.PersonRef;

import java.util.List;

public interface PersonRefRepository extends JpaRepository<PersonRef, Integer> {

    // студенты группы: обучение в этой группе со статусом «активен»
    @Query(nativeQuery = true, value =
            "SELECT p.* FROM person_ref p WHERE p.id IN (" +
            "  SELECT e.person_id FROM education_ref e WHERE e.group_id = ?1 AND e.status = 'активен') " +
            "ORDER BY p.surname, p.name")
    List<PersonRef> findStudentsOfGroup(Integer groupId);

    // студенты специальности: специальность -> группы -> обучение -> персона
    @Query(nativeQuery = true, value =
            "SELECT p.* FROM person_ref p WHERE p.id IN (" +
            "  SELECT e.person_id FROM education_ref e JOIN study_group g ON g.id = e.group_id " +
            "  WHERE g.specialty_id = ?1 AND e.status = 'активен') " +
            "ORDER BY p.surname, p.name")
    List<PersonRef> findStudentsOfSpecialty(Integer specialtyId);
}
