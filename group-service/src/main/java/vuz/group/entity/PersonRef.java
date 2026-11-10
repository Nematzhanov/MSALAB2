package vuz.group.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

// копия персоны из сервиса персон (для списков студентов)
@Entity
@Table(name = "person_ref")
public class PersonRef {

    @Id
    private Integer id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String gender;
    private String snils;

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
