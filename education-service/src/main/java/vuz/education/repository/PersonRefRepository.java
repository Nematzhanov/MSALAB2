package vuz.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vuz.education.entity.PersonRef;

public interface PersonRefRepository extends JpaRepository<PersonRef, Integer> {
}
