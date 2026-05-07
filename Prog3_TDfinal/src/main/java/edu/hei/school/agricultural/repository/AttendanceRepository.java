package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.controller.dto.ActivityMemberAttendanceDTO;
import edu.hei.school.agricultural.controller.dto.MemberDescription;
import edu.hei.school.agricultural.controller.dto.MemberOccupation;
import edu.hei.school.agricultural.entity.Attendance;
import edu.hei.school.agricultural.entity.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AttendanceRepository {
    private final Connection connection;

    public void saveAll(String activityId, List<Attendance> attendances) throws SQLException {
        String sql = """
            INSERT INTO attendance (id, activity_id, member_id, status)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (activity_id, member_id) 
            DO UPDATE SET status = EXCLUDED.status 
            WHERE attendance.status = 'UNDEFINED'
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Attendance att : attendances) {
                ps.setString(1, att.getId());
                ps.setString(2, activityId);
                ps.setString(3, att.getMemberId());
                ps.setString(4, String.valueOf(att.getStatus()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<ActivityMemberAttendanceDTO> findAttendanceByActivity(String activityId) throws SQLException {
        List<ActivityMemberAttendanceDTO> list = new ArrayList<>();
        String sql = """
            SELECT m.id, m.first_name, m.last_name, m.email, m.occupation,
                   COALESCE(att.status, 'UNDEFINED') as attendance_status,
                   att.id as attendance_id
            FROM member m
            INNER JOIN activity a ON a.id = ?
            LEFT JOIN attendance att ON (att.member_id = m.id AND att.activity_id = a.id)
            WHERE m.collectivity_id = a.collectivity_id
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, activityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MemberDescription member = new MemberDescription();
                    member.setId(rs.getString("id"));
                    member.setFirstName(rs.getString("first_name"));
                    member.setLastName(rs.getString("last_name"));
                    member.setEmail(rs.getString("email"));
                    member.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));

                    ActivityMemberAttendanceDTO dto = new ActivityMemberAttendanceDTO();
                    dto.setId(rs.getString("attendance_id"));
                    dto.setMemberDescription(member);
                    dto.setAttendanceStatus(AttendanceStatus.valueOf(rs.getString("attendance_status")));

                    list.add(dto);
                }
            }
        }
        return list;
    }
}