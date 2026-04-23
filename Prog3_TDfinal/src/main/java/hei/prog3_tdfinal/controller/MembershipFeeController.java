package hei.prog3_tdfinal.controller;

import hei.prog3_tdfinal.entity.MembershipFee;
import hei.prog3_tdfinal.service.MembershipFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/collectivities/{collectivityId}/membershipFees")
@RequiredArgsConstructor
public class MembershipFeeController {

    private final MembershipFeeService membershipFeeService;

    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipFee create(
            @PathVariable UUID collectivityId,
            @RequestBody MembershipFee membershipFee) throws SQLException {
        return membershipFeeService.create(collectivityId, membershipFee);
    }

    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MembershipFee> findAll(
            @PathVariable UUID collectivityId) throws SQLException {
        return membershipFeeService.findByCollectivityId(collectivityId);
    }
}