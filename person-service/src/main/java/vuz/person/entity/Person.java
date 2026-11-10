package vuz.person.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "имя не может быть пустым")
    @Size(max = 100, message = "имя должно быть не длиннее 100 символов")
    private String name;

    @NotBlank(message = "фамилия не может быть пустой")
    @Size(max = 100, message = "фамилия должна быть не длиннее 100 символов")
    private String surname;

    @NotNull(message = "дата рождения обязательна")
    @Past(message = "дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    @NotNull(message = "пол обязателен")
    @Pattern(regexp = "М|Ж", message = "пол должен быть М или Ж")
    private String gender;

    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{3} \\d{2}", message = "СНИЛС должен быть в формате 123-456-789 01")
    private String snils;

    // что отправляем другим сервисам в событиях person.*
    public Map<String, Object> toEvent() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("name", name);
        data.put("surname", surname);
        data.put("birth_date", birthDate);
        data.put("gender", gender);
        data.put("snils", snils);
        return data;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getSnils() { return snils; }
    public void setSnils(String snils) { this.snils = snils; }
}
