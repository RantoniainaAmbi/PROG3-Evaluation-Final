package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.CollectivityGlobalStatistics;
import edu.hei.school.agricultural.controller.dto.CollectivityStat;
import edu.hei.school.agricultural.controller.dto.CollectivityLocalStatistics;
import edu.hei.school.agricultural.entity.Collectivity;
import edu.hei.school.agricultural.entity.MembershipFee;
import edu.hei.school.agricultural.exception.BadRequestException;
import edu.hei.school.agricultural.exception.NotFoundException;
import edu.hei.school.agricultural.repository.CollectivityRepository;
import edu.hei.school.agricultural.repository.MemberRepository;
import edu.hei.school.agricultural.repository.MembershipFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static edu.hei.school.agricultural.entity.ActivityStatus.ACTIVE;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class CollectivityService {
    private final CollectivityRepository collectivityRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final MemberRepository memberRepository;

    public List<Collectivity> createCollectivities(List<Collectivity> collectivities) {
        for (Collectivity collectivity : collectivities) {
            if (!collectivity.hasEnoughMembers()) {
                throw new BadRequestException("Collectivity must have at least 10 members, otherwise actual is " + collectivity.getMembers().size());
            }
            collectivity.setId(randomUUID().toString());
        }
        return collectivityRepository.saveAll(collectivities);
    }

    public Collectivity getCollectivityById(String id) {
        return collectivityRepository.findById(id).orElseThrow(() -> new NotFoundException("Collectivity.id= " + id + " not found"));
    }

    public Collectivity updateInformations(String collectivityId, String actualName, Integer actualNumber) {
        Collectivity collectivity = collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity.id= " + collectivityId + " not found"));
        if (actualNumber != null && collectivityRepository.isNumberExists(actualNumber)) {
            throw new BadRequestException("Collectivity.number=" + actualNumber + " already exists");
        }
        if (actualName != null && collectivityRepository.isNameExists(actualName)) {
            throw new BadRequestException("Collectivity.name=" + actualName + " already exists");
        }
        collectivity.setName(actualName);
        collectivity.setNumber(actualNumber);
        return collectivityRepository.saveAll(List.of(collectivity)).get(0);
    }

    public List<MembershipFee> getMembershipFeesByCollectivityIdentifier(String collectivityIdentifier) {
        Collectivity collectivity = collectivityRepository.findById(collectivityIdentifier)
                .orElseThrow(() ->
                        new NotFoundException("Collectivity.id= " + collectivityIdentifier + " not found"));

        return membershipFeeRepository.getMembershipFeesByCollectivityId(collectivity.getId());
    }

    public List<MembershipFee> createMembershipFees(String collectivityIdentifier, List<MembershipFee> membershipFees) {
        Collectivity collectivity = collectivityRepository.findById(collectivityIdentifier)
                .orElseThrow(() ->
                        new NotFoundException("Collectivity.id= " + collectivityIdentifier + " not found"));
        for (MembershipFee membershipFee : membershipFees) {
            membershipFee.setId(randomUUID().toString());
            membershipFee.setStatus(ACTIVE);
            membershipFee.setCollectivityOwner(collectivity);
        }
        return membershipFeeRepository.saveAll(membershipFees);
    }

    public CollectivityStat getCollectivityStats(String id, LocalDate startDate, LocalDate endDate) {
        Collectivity collectivity = collectivityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Collectivity.id= " + id + " not found"));

        Double totalDue = membershipFeeRepository.getActiveFeesByCollectivityId(id).stream()
                .mapToDouble(MembershipFee::getAmount)
                .sum();

        List<CollectivityLocalStatistics> collectivityLocalStatistics = memberRepository.getMembersStatistics(id, startDate, endDate, totalDue);

        return CollectivityStat.builder()
                .id(id)
                .collectivityLocalStats(collectivityLocalStatistics)
                .build();
    }

    private Double calculatePotentialUnpaid(List<MembershipFee> activeFees, LocalDate start, LocalDate end) {
        return activeFees.stream()
                .mapToDouble(MembershipFee::getAmount)
                .sum();
    }

    public List<CollectivityGlobalStatistics> getAllStatistics(LocalDate from, LocalDate to) {
        List<Collectivity> collectivities = collectivityRepository.findAll();
        List<CollectivityGlobalStatistics> globalStats = new ArrayList<>();

        for (Collectivity col : collectivities) {
            List<MembershipFee> activeFees = membershipFeeRepository.getActiveFeesByCollectivityId(col.getId());

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
