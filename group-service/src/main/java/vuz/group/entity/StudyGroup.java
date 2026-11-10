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
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "study_group")
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "название группы не может быть пустым")
    @Size(max = 50, message = "название группы не длиннее 50 символов")
    private String name;

    @NotNull(message = "год начала обучения обязателен")
    @Min(value = 1950, message = "год начала обучения должен быть не раньше 1950")
    @Max(value = 2100, message = "год начала обучения должен быть не позже 2100")
    private Integer startYear;

    @NotNull(message = "специальность обязательна")
    private Integer specialtyId;

    @NotNull(message = "курс обязателен")
    @Min(value = 1, message = "курс от 1 до 6")
    @Max(value = 6, message = "курс от 1 до 6")
    private Integer course;

    @Pattern(regexp = "актуальна|закрыта", message = "статус группы: актуальна или закрыта")
    private String status;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getStartYear() { return startYear; }
    public void setStartYear(Integer startYear) { this.startYear = startYear; }
    public Integer getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(Integer specialtyId) { this.specialtyId = specialtyId; }
    public Integer getCourse() { return course; }
    public void setCourse(Integer course) { this.course = course; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
