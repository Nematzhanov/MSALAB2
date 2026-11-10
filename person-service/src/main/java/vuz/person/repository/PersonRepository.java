package vuz.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vuz.person.entity.Person;

import java.time.LocalDate;
import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    // отчисленные = у персоны есть хотя бы одно обучение со статусом «отчислен».
    // если параметр не передан (null), по нему не фильтруем
    @Query(nativeQuery = true, value =
            "SELECT * FROM person p " +
            "WHERE EXISTS (SELECT 1 FROM education_ref e WHERE e.person_id = p.id AND e.status = 'отчислен') " +
            "AND (CAST(:birthDate AS date) IS NULL OR p.birth_date = :birthDate) " +
            "AND (CAST(:gender AS varchar) IS NULL OR p.gender = :gender) " +
            "ORDER BY p.id")
    List<Person> findExpelled(@Param("birthDate") LocalDate birthDate, @Param("gender") String gender);
}
