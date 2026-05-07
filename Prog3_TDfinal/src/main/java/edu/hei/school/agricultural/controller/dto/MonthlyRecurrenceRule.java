package edu.hei.school.agricultural.controller.dto;

import lombok.Data;

@Data
public class MonthlyRecurrenceRule {
    private Integer weekOrdinal;
    private String dayOfWeek;
}