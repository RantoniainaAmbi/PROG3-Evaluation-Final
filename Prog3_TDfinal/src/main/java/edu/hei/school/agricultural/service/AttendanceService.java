package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.ActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.controller.dto.CreateActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.controller.mapper.AttendanceDtoMapper;
import edu.hei.school.agricultural.entity.Attendance;
import edu.hei.school.agricultural.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceDtoMapper attendanceMapper;

    public List<CreateActivityMemberAttendanceDTO> confirmAttendance(
            String activityId,
            List<CreateActivityMemberAttendanceDTO> dtos) {

        List<Attendance> entities = dtos.stream()
                .map(attendanceMapper::toEntity)
                .toList();

        try {
            attendanceRepository.saveAll(activityId, entities);
            return dtos;
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error saving attendance", e);
        }
    }

    public List<ActivityMemberAttendanceDTO> getAttendance(String activityId) {
        try {
            return attendanceRepository.findAttendanceByActivity(activityId);
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found or database error", e);
        }
    }
}
