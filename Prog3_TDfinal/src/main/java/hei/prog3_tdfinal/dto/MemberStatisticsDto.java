package hei.prog3_tdfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberStatisticsDto {
    private String memberId;
    private String firstName;
    private String lastName;
    private double attendanceRate;
    private double totalCollected;
    private double outstandingAmount;
}
