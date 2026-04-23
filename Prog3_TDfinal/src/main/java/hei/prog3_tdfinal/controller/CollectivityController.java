package hei.prog3_tdfinal.controller;

import hei.prog3_tdfinal.dto.CollectivityStatisticsDto;
import hei.prog3_tdfinal.dto.FinancialAccountResponse;
import hei.prog3_tdfinal.dto.UpdateCollectivityIdentityRequest;
import hei.prog3_tdfinal.entity.Collectivity;
import hei.prog3_tdfinal.service.CollectivityService;
import hei.prog3_tdfinal.service.CollectivityStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/collectivities")
@RequiredArgsConstructor
public class CollectivityController {

    private final CollectivityService service;
    private final CollectivityStatisticsService statisticsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Collectivity createCollectivities(@RequestBody java.util.Map<String, Object> data)
            throws SQLException {
        return service.createCollectivity(data);
    }

    @PatchMapping("/{id}")
    public Collectivity updateIdentity(
            @PathVariable UUID id,
            @RequestBody UpdateCollectivityIdentityRequest request) throws SQLException {
        return service.assignIdentity(id, request.getName(), request.getNumber());
    }

    @GetMapping("/{id}")
    public Collectivity getById(@PathVariable UUID id) throws SQLException {
        return service.getById(id);
    }

    @GetMapping("/{id}/financialAccounts")
    public List<FinancialAccountResponse> getFinancialAccounts(
            @PathVariable UUID id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate at) throws SQLException {
        return service.getFinancialAccounts(id, at);
    }

    @GetMapping("/{id}/statistics")
    public CollectivityStatisticsDto getStatistics(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws SQLException {
        return statisticsService.getStatistics(id, startDate, endDate);
    }
}