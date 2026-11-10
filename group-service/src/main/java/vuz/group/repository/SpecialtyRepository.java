package vuz.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vuz.group.entity.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {
}
