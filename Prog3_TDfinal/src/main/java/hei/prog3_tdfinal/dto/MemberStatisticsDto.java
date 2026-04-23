package hei.prog3_tdfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberStatisticsDto {
    private UUID memberId;
    private String firstName;
    private String lastName;
    private double attendanceRate;
    private double totalCollected;
    private double outstandingAmount;
}
