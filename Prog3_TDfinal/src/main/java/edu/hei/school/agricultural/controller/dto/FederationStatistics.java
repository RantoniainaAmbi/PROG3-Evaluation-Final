package edu.hei.school.agricultural.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FederationStatistics {
    private String collectivityId;
    private String collectivityName;
    private Integer collectivityNumber;
    private Double upToDateMembersPercentage;
    private Integer newMembersCount;
}
