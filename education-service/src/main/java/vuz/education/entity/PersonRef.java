package vuz.education.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// копия: какие персоны существуют
@Entity
@Table(name = "person_ref")
public class PersonRef {

    @Id
    private Integer id;

    public PersonRef() { }

    public PersonRef(Integer id) {
        this.id = id;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
}
