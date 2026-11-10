package vuz.person.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

// копия записи из сервиса обучения
@Entity
@Table(name = "education_ref")
public class EducationRef {

    @Id
    private Integer educationId;
    private Integer personId;
    private Integer groupId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public Integer getEducationId() { return educationId; }
    public void setEducationId(Integer educationId) { this.educationId = educationId; }
    public Integer getPersonId() { return personId; }
    public void setPersonId(Integer personId) { this.personId = personId; }
    public Integer getGroupId() { return groupId; }
    public void setGroupId(Integer groupId) { this.groupId = groupId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
