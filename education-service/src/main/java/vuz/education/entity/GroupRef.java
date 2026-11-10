package vuz.education.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// копия: какие группы существуют и закрыта ли группа
@Entity
@Table(name = "group_ref")
public class GroupRef {

    @Id
    private Integer id;
    private String status;

    public GroupRef() { }

    public GroupRef(Integer id, String status) {
        this.id = id;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
