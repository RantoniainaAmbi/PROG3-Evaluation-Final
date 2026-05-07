package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.*;
import edu.hei.school.agricultural.repository.CollectivityRepository;
import edu.hei.school.agricultural.repository.MemberRepository;
import edu.hei.school.agricultural.repository.MembershipFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import edu.hei.school.agricultural.entity.Collectivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final CollectivityRepository collectivityRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final MemberRepository memberRepository;

    public CollectivityStat getCollectivityStats(String id, LocalDate startDate, LocalDate endDate) {
        edu.hei.school.agricultural.entity.Collectivity collectivity = collectivityRepository.findById(id)
                .orElseThrow();

        Double totalDue = membershipFeeRepository.getActiveFeesByCollectivityId(id).stream()
                .mapToDouble(f -> f.getAmount())
                .sum();

        List<CollectivityLocalStatistics> collectivityLocalStatistics =
                memberRepository.getMembersStatistics(id, startDate, endDate, totalDue);

        return CollectivityStat.builder()
                .id(id)
                .collectivityLocalStats(collectivityLocalStatistics)
                .build();
    }

    public List<CollectivityGlobalStatistics> getAllStatistics(LocalDate from, LocalDate to) {
        List<Collectivity> collectivities = collectivityRepository.findAll();
        List<CollectivityGlobalStatistics> globalStats = new ArrayList<>();

        for (Collectivity col : collectivities) {
            var activeFees = membershipFeeRepository.getActiveFeesByCollectivityId(col.getId());

            globalStats.add(memberRepository.getGlobalStatsByCollectivity(
                    col.getId(),
                    col.getName(),
                    from,
                    to,
                    activeFees
            ));
        }

        return globalStats;
    }
}