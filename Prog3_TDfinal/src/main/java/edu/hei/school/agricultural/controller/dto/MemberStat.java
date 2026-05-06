package edu.hei.school.agricultural.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberStat {
    private String memberId;
    private String memberName;
    private Double collectedAmount;
    private Double unpaidAmount;
}