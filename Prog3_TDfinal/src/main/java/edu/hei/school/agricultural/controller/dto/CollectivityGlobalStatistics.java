package edu.hei.school.agricultural.controller.dto;

import lombok.*;

@Data
@Builder
public class CollectivityGlobalStatistics {
    private CollectivityInformation collectivityInformation;

    private Integer newMembersNumber;

    private Double overallMemberCurrentDuePercentage;
}
