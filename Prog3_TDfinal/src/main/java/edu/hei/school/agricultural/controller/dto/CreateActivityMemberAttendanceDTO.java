package edu.hei.school.agricultural.controller.dto;

import edu.hei.school.agricultural.entity.AttendanceStatus;
import lombok.Data;

@Data
public class CreateActivityMemberAttendanceDTO {
    private String memberIdentifier;
    private AttendanceStatus attendanceStatus;
}