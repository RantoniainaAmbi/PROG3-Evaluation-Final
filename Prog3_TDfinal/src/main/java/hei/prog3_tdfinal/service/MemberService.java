package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.entity.Member;
import hei.prog3_tdfinal.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;
    private final AnnualMembershipFeesCalculator feesCalculator;

    private void validateSponsorsAreConfirmed(List<Map<String, Object>> sponsors) throws SQLException {
        for (Map<String, Object> sponsor : sponsors) {
            String sponsorId = (String) sponsor.get("id");
            if (!repository.isConfirmedMember(sponsorId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "All sponsors must be confirmed members");
            }
        }
    }

    private void validateSponsorsSeniority(List<Map<String, Object>> sponsors) throws SQLException {
        for (Map<String, Object> sponsor : sponsors) {
            String sponsorId = (String) sponsor.get("id");
            Long days = repository.getDaysOfMembership(sponsorId);
            if (days < 90) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "All sponsors must have at least 90 days of membership");
            }
        }
    }

    private void validateSponsors(String targetCollectivityId, List<Map<String, Object>> sponsors) throws SQLException {
        if (sponsors == null || sponsors.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least 2 confirmed sponsors are required for admission");
        }

        validateSponsorsAreConfirmed(sponsors);
        validateSponsorsSeniority(sponsors);

        long targetCollectivitySponsors = sponsors.stream()
                .filter(s -> targetCollectivityId.equals((String) s.get("collectivityId")))
                .count();

        long otherCollectivitySponsors = sponsors.size() - targetCollectivitySponsors;

        if (targetCollectivitySponsors < otherCollectivitySponsors) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sponsors from the target collectivity must be equal to or greater than sponsors from other collectivities");
        }
    }

    public Member addMemberToCollectivity(String collectivityId, Map<String, Object> data) throws SQLException {
        Object regFee = data.get("registrationFeePaid");
        Object duesPaid = data.get("membershipDuesPaid");

        boolean isRegPaid = regFee instanceof Boolean && (Boolean) regFee;
        boolean isDuesPaid = duesPaid instanceof Boolean && (Boolean) duesPaid;

        if (!isRegPaid || !isDuesPaid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot create member: Registration fee (50.000 MGA) and membership dues must be paid.");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sponsors = (List<Map<String, Object>>) data.get("sponsors");
        
        if (sponsors == null || sponsors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least 2 confirmed sponsors are required for admission");
        }

        validateSponsors(collectivityId, sponsors);

        double annualDues = feesCalculator.calculateTotalAnnualDues(collectivityId);
        Object paidAmount = data.get("totalPaidAmount");
        if (paidAmount instanceof Number) {
            double paid = ((Number) paidAmount).doubleValue();
            double required = 50000.0 + annualDues;
            if (paid < required) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Total payment must include registration fee (50.000 MGA) + annual dues (" + annualDues + " MGA)");
            }
        }

        repository.save(collectivityId, data);
        
        String candidateId = (String) data.get("id");
        if (candidateId != null) {
            for (Map<String, Object> sponsor : sponsors) {
                String sponsorId = (String) sponsor.get("id");
                String relation = (String) sponsor.get("relation");
                repository.saveSponsorship(candidateId, sponsorId, relation);
            }
        }
        
        return Member.builder()
                .firstName((String) data.get("firstName"))
                .lastName((String) data.get("lastName"))
                .email((String) data.get("email"))
                .build();
    }

    public Member getMemberById(String id) throws SQLException {
        Member member = repository.findById(id);
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member with ID " + id + " not found");
        }
        return member;
    }
}