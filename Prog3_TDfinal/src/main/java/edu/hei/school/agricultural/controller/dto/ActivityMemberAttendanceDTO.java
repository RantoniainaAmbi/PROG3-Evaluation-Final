package edu.hei.school.agricultural.controller.dto;

import edu.hei.school.agricultural.entity.AttendanceStatus;
import lombok.Data;

@Data
public class ActivityMemberAttendanceDTO {
    private String id;
    private MemberDescription memberDescription;
    private AttendanceStatus attendanceStatus;
}