package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.entity.ActivityStatus;
import hei.prog3_tdfinal.entity.MembershipFee;
import hei.prog3_tdfinal.repository.MembershipFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipFeeService {

    private final MembershipFeeRepository membershipFeeRepository;

    public MembershipFee create(String collectivityId, MembershipFee fee) throws SQLException {
        if (fee.getAmount() == null || fee.getAmount() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le montant de la cotisation (amount) ne peut pas être inférieur à 0."
            );
        }

        if (fee.getLabel() == null || fee.getLabel().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le label est obligatoire.");
        }

        if (fee.getEligibleFrom() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date eligibleFrom est obligatoire.");
        }

        if (fee.getFrequency() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fréquence est obligatoire.");
        }

        fee.setCollectivityId(collectivityId);

        if (fee.getStatus() == null) {
            fee.setStatus(ActivityStatus.ACTIVE);
        }

        return membershipFeeRepository.save(fee);
    }

    public List<MembershipFee> findByCollectivityId(String collectivityId) throws SQLException {
        return membershipFeeRepository.findByCollectivityId(collectivityId);
    }
}