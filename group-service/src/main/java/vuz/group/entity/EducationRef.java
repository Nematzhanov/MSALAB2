package vuz.group.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// копия записи из сервиса обучения
@Entity
@Table(name = "education_ref")
public class EducationRef {

    @Id
    private Integer educationId;
    private Integer personId;
    private Integer groupId;
    private String status;

    public Integer getEducationId() { return educationId; }
    public void setEducationId(Integer educationId) { this.educationId = educationId; }
    public Integer getPersonId() { return personId; }
    public void setPersonId(Integer personId) { this.personId = personId; }
    public Integer getGroupId() { return groupId; }
    public void setGroupId(Integer groupId) { this.groupId = groupId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
