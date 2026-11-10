package vuz.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vuz.education.entity.Education;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Integer> {

    List<Education> findByPersonId(Integer personId);

    List<Education> findByGroupIdAndStatus(Integer groupId, String status);
}
