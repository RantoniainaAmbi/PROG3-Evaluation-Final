package edu.hei.school.agricultural.controller.mapper;

import edu.hei.school.agricultural.controller.dto.*;
import edu.hei.school.agricultural.entity.Attendance;
import edu.hei.school.agricultural.entity.AttendanceStatus;
import org.springframework.stereotype.Component;

@Component
public class AttendanceDtoMapper {

    public Attendance toEntity(CreateActivityMemberAttendanceDTO dto) {
        return Attendance.builder()
                .id("ATT-" + System.currentTimeMillis())
                .memberId(dto.getMemberIdentifier())
                .status(AttendanceStatus.valueOf(dto.getAttendanceStatus().name()))
                .build();
    }

    public ActivityMemberAttendanceDTO toDto(Attendance entity, Member member) {
        MemberDescription memberDesc = new MemberDescription();
        memberDesc.setId(member.getId());
        memberDesc.setFirstName(member.getFirstName());
        memberDesc.setLastName(member.getLastName());
        memberDesc.setEmail(member.getEmail());
        memberDesc.setOccupation(MemberOccupation.valueOf(member.getOccupation().name()));

        ActivityMemberAttendanceDTO dto = new ActivityMemberAttendanceDTO();
        dto.setId(entity.getId());
        dto.setMemberDescription(memberDesc);
        dto.setAttendanceStatus(entity.getStatus());
        return dto;
    }
}