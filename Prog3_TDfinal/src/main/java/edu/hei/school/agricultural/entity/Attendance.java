package edu.hei.school.agricultural.entity;

import lombok.*;

@Data
@Builder
public class Attendance {
    private String id;
    private String activityId;
    private String memberId;
    private AttendanceStatus status;
}