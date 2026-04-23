package hei.prog3_tdfinal.dto;

import hei.prog3_tdfinal.entity.Gender;
import hei.prog3_tdfinal.entity.MemberOccupation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private Long phoneNumber;
    private String email;
    private MemberOccupation occupation;
    private LocalDate registrationDate;
    private List<SponsorInfo> sponsors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SponsorInfo {
        private UUID id;
        private String firstName;
        private String lastName;
        private String relation;
    }
}
