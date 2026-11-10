package vuz.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vuz.person.entity.GroupRef;

public interface GroupRefRepository extends JpaRepository<GroupRef, Integer> {
}
