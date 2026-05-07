package edu.hei.school.agricultural.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateCollectivityActivityDTO {
    private String label;
    @JsonProperty("type")
    private String activityType;
    private List<String> memberOccupationConcerned;
    private MonthlyRecurrenceRule recurrenceRule;
    private LocalDate executiveDate;
}
