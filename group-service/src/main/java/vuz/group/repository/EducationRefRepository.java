package vuz.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vuz.group.entity.EducationRef;

public interface EducationRefRepository extends JpaRepository<EducationRef, Integer> {

    long countByGroupId(Integer groupId);

    long countByGroupIdAndStatus(Integer groupId, String status);
}
