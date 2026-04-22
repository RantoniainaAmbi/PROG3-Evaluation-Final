package hei.prog3_tdfinal.controller;

import hei.prog3_tdfinal.entity.Member;
import hei.prog3_tdfinal.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/collectivities")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;


    @PostMapping("/{collectivityId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public Member addMemberToCollectivity(
            @PathVariable UUID collectivityId,
            @RequestBody Map<String, Object> newMember) throws SQLException {

        return service.addMemberToCollectivity(collectivityId, newMember);
    }
}