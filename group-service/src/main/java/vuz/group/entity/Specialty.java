package vuz.group.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "specialty")
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "название специальности не может быть пустым")
    private String name;

    @NotBlank(message = "институт не может быть пустым")
    private String institute;

    @NotBlank(message = "кафедра не может быть пустой")
    private String department;

    @NotNull(message = "срок обучения обязателен")
    @Min(value = 1, message = "срок обучения от 1 до 6 лет")
    @Max(value = 6, message = "срок обучения от 1 до 6 лет")
    private Integer studyYears;

    @Pattern(regexp = "актуально|неактуально", message = "статус специальности: актуально или неактуально")
    private String status;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getInstitute() { return institute; }
    public void setInstitute(String institute) { this.institute = institute; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Integer getStudyYears() { return studyYears; }
    public void setStudyYears(Integer studyYears) { this.studyYears = studyYears; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
