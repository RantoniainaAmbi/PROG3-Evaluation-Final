package hei.prog3_tdfinal.entity;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
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
    private List<Member> referees;
}
