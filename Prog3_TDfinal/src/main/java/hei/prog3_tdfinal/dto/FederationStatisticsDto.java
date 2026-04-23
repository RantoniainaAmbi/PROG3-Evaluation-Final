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
public class FederationStatisticsDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<CollectivityFederationStatDto> collectivityStatistics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CollectivityFederationStatDto {
        private String collectivityName;
        private double attendanceRate;
        private long newMembers;
        private double paidUpPercentage;
    }
}
