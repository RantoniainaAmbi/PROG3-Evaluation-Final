package hei.prog3_tdfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectivityStatisticsDto {
    private UUID collectivityId;
    private long totalMembers;
    private double attendanceRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<MemberStatisticsDto> memberStatistics;
}
