package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.dto.CollectivityStatisticsDto;
import hei.prog3_tdfinal.dto.MemberStatisticsDto;
import hei.prog3_tdfinal.repository.CollectivityRepository;
import hei.prog3_tdfinal.repository.FinancialAccountRepository;
import hei.prog3_tdfinal.repository.MemberRepository;
import hei.prog3_tdfinal.repository.MembershipFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectivityStatisticsService {
    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final FinancialAccountRepository financialAccountRepository;

    public CollectivityStatisticsDto getStatistics(UUID collectivityId, LocalDate startDate, LocalDate endDate) throws SQLException {
        if (collectivityRepository.findById(collectivityId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
        }

        long totalMembers = memberRepository.countActiveMembers(collectivityId);
        double attendanceRate = calculateAttendanceRate(collectivityId, startDate, endDate);
        List<MemberStatisticsDto> memberStats = getMemberStatistics(collectivityId, startDate, endDate);

        return CollectivityStatisticsDto.builder()
                .collectivityId(collectivityId)
                .totalMembers(totalMembers)
                .attendanceRate(attendanceRate)
                .memberStatistics(memberStats)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public List<MemberStatisticsDto> getMemberStatistics(UUID collectivityId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return membershipFeeRepository.getMemberStatistics(collectivityId, startDate, endDate);
    }

    private double calculateAttendanceRate(UUID collectivityId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return 0.0;
    }
}
