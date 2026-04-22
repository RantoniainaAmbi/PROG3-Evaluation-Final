package hei.prog3_tdfinal.dto;

import hei.prog3_tdfinal.entity.Gender;
import hei.prog3_tdfinal.entity.MemberOccupation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMemberRequest {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private Long phoneNumber;
    private String email;
    private MemberOccupation occupation;
    private UUID collectivityIdentifier;
    private List<UUID> referees;
    private boolean registrationFeePaid;
    private boolean membershipDuesPaid;
}
