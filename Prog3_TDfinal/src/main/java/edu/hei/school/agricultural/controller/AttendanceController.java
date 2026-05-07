package edu.hei.school.agricultural.controller;

import edu.hei.school.agricultural.controller.dto.ActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.controller.dto.CreateActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities/{id}/activities/{activityId}/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<CreateActivityMemberAttendanceDTO> confirmAttendance(
            @PathVariable String id,
            @PathVariable String activityId,
            @RequestBody List<CreateActivityMemberAttendanceDTO> attendanceList) {

        return attendanceService.confirmAttendance(activityId, attendanceList);
    }

    @GetMapping
    public List<ActivityMemberAttendanceDTO> getAttendance(
            @PathVariable String id,
            @PathVariable String activityId) {

        return attendanceService.getAttendance(activityId);
    }
}