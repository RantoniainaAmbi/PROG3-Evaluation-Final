package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.entity.Member;
import hei.prog3_tdfinal.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;


    public void addMemberToCollectivity(UUID collectivityId, Map<String, Object> data) throws SQLException {

        boolean registrationFeePaid = (boolean) data.getOrDefault("registrationFeePaid", false);
        boolean membershipDuesPaid = (boolean) data.getOrDefault("membershipDuesPaid", false);

        if (!registrationFeePaid || !membershipDuesPaid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot create member: Registration fee and membership dues must be paid.");
        }

        repository.save(collectivityId, data);
    }


    public Member getMemberById(UUID id) throws SQLException {
        Member member = repository.findById(id);
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        }
        return member;
    }
}