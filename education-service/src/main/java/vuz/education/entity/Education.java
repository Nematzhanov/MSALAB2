package vuz.education.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "education")
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // персона и группа хранятся в других сервисах, тут только их id
    @NotNull(message = "персона обязательна")
    private Integer personId;

    @NotNull(message = "группа обязательна")
    private Integer groupId;

    @NotNull(message = "дата начала обязательна")
    private LocalDate startDate;

    private LocalDate endDate;
    private String status;

    public Map<String, Object> toEvent() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("person_id", personId);
        data.put("group_id", groupId);
        data.put("start_date", startDate);
        data.put("end_date", endDate);
        data.put("status", status);
        return data;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
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
