package edu.hei.school.agricultural.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Activity {
    private String id;
    private String label;
    private String type;
    private String collectivityId;

    private List<String> occupationsConcerned;

    private LocalDate executiveDate;
    private Integer weekOrdinal;
    private String dayOfWeek;
}