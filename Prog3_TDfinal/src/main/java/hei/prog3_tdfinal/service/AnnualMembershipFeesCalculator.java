package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.entity.Frequency;
import hei.prog3_tdfinal.entity.MembershipFee;
import hei.prog3_tdfinal.repository.MembershipFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AnnualMembershipFeesCalculator {
    private final MembershipFeeRepository membershipFeeRepository;

    public double calculateTotalAnnualDues(String collectivityId) throws SQLException {
        List<MembershipFee> fees = membershipFeeRepository.findByCollectivityId(collectivityId);
        
        double total = 0.0;
        for (MembershipFee fee : fees) {
            if (fee.getFrequency() == Frequency.ANNUALLY && fee.getAmount() != null) {
                total += fee.getAmount();
            }
        }
        return total;
    }

    public double calculateMandatoryDues(String collectivityId) throws SQLException {
        List<MembershipFee> fees = membershipFeeRepository.findByCollectivityId(collectivityId);
        
        double total = 0.0;
        for (MembershipFee fee : fees) {
            if (fee.getFrequency() == Frequency.ANNUALLY && fee.getAmount() != null) {
                total += fee.getAmount();
            }
        }
        return total;
    }
}
