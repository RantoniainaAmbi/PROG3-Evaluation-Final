package hei.prog3_tdfinal.controller;

import hei.prog3_tdfinal.dto.FederationStatisticsDto;
import hei.prog3_tdfinal.service.FederationStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.time.LocalDate;

@RestController
@RequestMapping("/federation")
@RequiredArgsConstructor
public class FederationController {
    private final FederationStatisticsService federationStatisticsService;

    @GetMapping("/statistics")
    public FederationStatisticsDto getStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws SQLException {
        return federationStatisticsService.getFederationStatistics(startDate, endDate);
    }
}
