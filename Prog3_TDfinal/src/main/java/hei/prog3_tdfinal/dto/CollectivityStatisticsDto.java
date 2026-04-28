package hei.prog3_tdfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectivityStatisticsDto {
    private String collectivityId;
    private long totalMembers;
    private double attendanceRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<MemberStatisticsDto> memberStatistics;
}
