package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.dto.FederationStatisticsDto;
import hei.prog3_tdfinal.repository.CollectivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FederationStatisticsService {
    private final CollectivityRepository collectivityRepository;
    private final CollectivityStatisticsService collectivityStatisticsService;

    public FederationStatisticsDto getFederationStatistics(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<FederationStatisticsDto.CollectivityFederationStatDto> stats = new ArrayList<>();
        
        List<FederationStatisticsDto.CollectivityFederationStatDto> collectivityStats = 
                collectivityRepository.getFederationStatistics(startDate, endDate);
        
        stats.addAll(collectivityStats);

        return FederationStatisticsDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .collectivityStatistics(stats)
                .build();
    }
}
