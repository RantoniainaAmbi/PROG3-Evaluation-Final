package hei.prog3_tdfinal.repository;

import hei.prog3_tdfinal.config.DBConnection;
import hei.prog3_tdfinal.entity.Member;
import hei.prog3_tdfinal.entity.MemberOccupation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final DBConnection dbConnection;

    public void save(UUID collectivityId, Map<String, Object> data) throws SQLException {
        String sql = "INSERT INTO member (first_name, last_name, birth_date, gender, address, profession, phone_number, email, registration_date, collectivity_id, registration_fee_paid, membership_dues_paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, (String) data.get("firstName"));
            ps.setString(2, (String) data.get("lastName"));
            ps.setDate(3, Date.valueOf((String) data.get("birthDate")));

            ps.setObject(4, data.get("gender"), java.sql.Types.OTHER);
            ps.setString(5, (String) data.get("address"));
            ps.setString(6, (String) data.get("profession"));

            ps.setString(7, (String) data.get("phoneNumber"));
            ps.setString(8, (String) data.get("email"));
            ps.setDate(9, new java.sql.Date(System.currentTimeMillis()));
            ps.setObject(10, collectivityId);
            ps.setBoolean(11, data.get("registrationFeePaid") != null && (Boolean) data.get("registrationFeePaid"));
            ps.setBoolean(12, data.get("membershipDuesPaid") != null && (Boolean) data.get("membershipDuesPaid"));

            ps.executeUpdate();
        }
    }

    public void saveSponsorship(UUID candidateId, UUID sponsorId, String relation) throws SQLException {
        String sql = "INSERT INTO sponsorship (candidate_id, sponsor_id, relation) VALUES (?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, candidateId);
            ps.setObject(2, sponsorId);
            ps.setString(3, relation);
            ps.executeUpdate();
        }
    }

    public Member findById(UUID id) throws SQLException {
        String sql = "SELECT id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, registration_date FROM member WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Member.builder()
                            .id((UUID) rs.getObject("id"))
                            .firstName(rs.getString("first_name"))
                            .lastName(rs.getString("last_name"))
                            .build();
                }
            }
        }
        return null;
    }

    public Member findByIdWithOccupation(UUID id) throws SQLException {
        String sql = "SELECT id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, registration_date, occupation FROM member WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Member.builder()
                            .id((UUID) rs.getObject("id"))
                            .firstName(rs.getString("first_name"))
                            .lastName(rs.getString("last_name"))
                            .registrationDate(rs.getDate("registration_date").toLocalDate())
                            .occupation(MemberOccupation.valueOf(rs.getString("occupation")))
                            .build();
                }
            }
        }
        return null;
    }

    public boolean isConfirmedMember(UUID memberId) throws SQLException {
        String sql = "SELECT occupation FROM member WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String occupation = rs.getString("occupation");
                    return occupation != null && (occupation.equals("SENIOR") || occupation.equals("CONFIRMED"));
                }
            }
        }
        return false;
    }

    public Long getDaysOfMembership(UUID memberId) throws SQLException {
        String sql = "SELECT registration_date FROM member WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date registrationDate = rs.getDate("registration_date");
                    if (registrationDate != null) {
                        LocalDate regDate = registrationDate.toLocalDate();
                        LocalDate today = java.time.LocalDate.now();
                        return java.time.temporal.ChronoUnit.DAYS.between(regDate, today);
                    }
                }
            }
        }
        return 0L;
    }

    public Long countMembersByCollectivityWithSeniority(UUID collectivityId, long minDays) throws SQLException {
        String sql = "SELECT COUNT(id) as count FROM member WHERE collectivity_id = ? AND registration_date <= NOW() - INTERVAL ? DAY";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, collectivityId);
            ps.setLong(2, minDays);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("count");
                }
            }
        }
        return 0L;
    }

    public Long countActiveMembers(UUID collectivityId) throws SQLException {
        String sql = "SELECT COUNT(id) as count FROM member WHERE collectivity_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, collectivityId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("count");
                }
            }
        }
        return 0L;
    }
}