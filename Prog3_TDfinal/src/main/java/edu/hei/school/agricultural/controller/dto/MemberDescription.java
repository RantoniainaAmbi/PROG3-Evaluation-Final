package edu.hei.school.agricultural.controller.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDescription {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private MemberOccupation occupation;
}