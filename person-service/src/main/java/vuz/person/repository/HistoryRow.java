package vuz.person.repository;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// одна строка истории обучения (результат запроса findHistory)
@JsonPropertyOrder({"education_id", "group_id", "group_name", "start_year", "specialty", "course",
        "start_date", "end_date", "status"})
public interface HistoryRow {
    Integer getEducationId();
    Integer getGroupId();
    String getGroupName();
    Integer getStartYear();
    String getSpecialty();
    Integer getCourse();
    String getStartDate();
    String getEndDate();
    String getStatus();
}
