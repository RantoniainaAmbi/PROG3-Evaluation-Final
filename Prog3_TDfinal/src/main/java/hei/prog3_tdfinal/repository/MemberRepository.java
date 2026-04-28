package hei.prog3_tdfinal.repository;

import hei.prog3_tdfinal.config.DBConnection;
import hei.prog3_tdfinal.entity.Member;
import hei.prog3_tdfinal.entity.MemberOccupation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final DBConnection dbConnection;

    public void save(String collectivityId, Map<String, Object> data) throws SQLException {
        String memberId = java.util.UUID.randomUUID().toString();
        String sqlPrimary = "INSERT INTO member (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, registration_date, collectivity_id, registration_fee_paid, membership_dues_paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlAlternate = "INSERT INTO member (id, first_name, last_name, birth_date, gender, address, job, phone, email, registration_date, collectivity_id, registration_fee_paid, membership_dues_paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlPrimaryNoCollectivity = "INSERT INTO member (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, registration_date, registration_fee_paid, membership_dues_paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlAlternateNoCollectivity = "INSERT INTO member (id, first_name, last_name, birth_date, gender, address, job, phone, email, registration_date, registration_fee_paid, membership_dues_paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection()) {
            if (!tryInsertMember(conn, sqlPrimary, memberId, collectivityId, data)) {
                if (!tryInsertMember(conn, sqlAlternate, memberId, collectivityId, data)) {
                    if (!tryInsertMember(conn, sqlPrimaryNoCollectivity, memberId, null, data)) {
                        tryInsertMember(conn, sqlAlternateNoCollectivity, memberId, null, data);
                    }
                }
            }
        }
    }

    private boolean tryInsertMember(Connection conn, String sql, String memberId, String collectivityId, Map<String, Object> data) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ps.setString(2, (String) data.get("firstName"));
            ps.setString(3, (String) data.get("lastName"));
            ps.setDate(4, Date.valueOf((String) data.get("birthDate")));

            ps.setObject(5, data.get("gender"), java.sql.Types.OTHER);
            ps.setString(6, (String) data.get("address"));
            boolean alternateColumns = sql.contains(" job") && sql.contains(" phone");
            ps.setString(7, alternateColumns ? (String) data.get("job") : (String) data.get("profession"));
            ps.setString(8, alternateColumns ? (String) data.get("phone") : (String) data.get("phoneNumber"));
            ps.setString(9, (String) data.get("email"));
            ps.setDate(10, new java.sql.Date(System.currentTimeMillis()));

            boolean hasCollectivityId = sql.contains("collectivity_id");
            if (hasCollectivityId) {
                ps.setObject(11, collectivityId);
                ps.setBoolean(12, data.get("registrationFeePaid") != null && (Boolean) data.get("registrationFeePaid"));
                ps.setBoolean(13, data.get("membershipDuesPaid") != null && (Boolean) data.get("membershipDuesPaid"));
            } else {
                ps.setBoolean(11, data.get("registrationFeePaid") != null && (Boolean) data.get("registrationFeePaid"));
                ps.setBoolean(12, data.get("membershipDuesPaid") != null && (Boolean) data.get("membershipDuesPaid"));
            }

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("profession") || msg.contains("phone_number") || msg.contains("job") || msg.contains("phone") || msg.contains("collectivity_id")) {
                return false;
            }
            throw e;
        }
    }

    public void saveSponsorship(String candidateId, String sponsorId, String relation) throws SQLException {
        String sql = "INSERT INTO sponsorship (candidate_id, sponsor_id, relation) VALUES (?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, candidateId);
            ps.setObject(2, sponsorId);
            ps.setString(3, relation);
            ps.executeUpdate();
        }
    }

    public Member findById(String id) throws SQLException {
        try (Connection conn = dbConnection.getConnection()) {
            String professionColumn = getMemberProfessionColumnName(conn);
            String phoneColumn = getMemberPhoneColumnName(conn);
            String selectProfession = professionColumn != null ? professionColumn : "NULL";
            String selectPhone = phoneColumn != null ? phoneColumn : "NULL";
            String sql = "SELECT id, first_name, last_name, birth_date, gender, address, "
                    + selectProfession + " AS profession, "
                    + selectPhone + " AS phone_number, email, registration_date FROM member WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Member.builder()
                                .id(rs.getString("id"))
                                .firstName(rs.getString("first_name"))
                                .lastName(rs.getString("last_name"))
                                .build();
                    }
                }
            }
        }
        return null;
    }

    public Member findByIdWithOccupation(String id) throws SQLException {
        try (Connection conn = dbConnection.getConnection()) {
            String professionColumn = getMemberProfessionColumnName(conn);
            String phoneColumn = getMemberPhoneColumnName(conn);
            String selectProfession = professionColumn != null ? professionColumn : "NULL";
            String selectPhone = phoneColumn != null ? phoneColumn : "NULL";
            String sql = "SELECT id, first_name, last_name, birth_date, gender, address, "
                    + selectProfession + " AS profession, "
                    + selectPhone + " AS phone_number, email, registration_date, occupation FROM member WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Member.builder()
                                .id(rs.getString("id"))
                                .firstName(rs.getString("first_name"))
                                .lastName(rs.getString("last_name"))
                                .registrationDate(rs.getDate("registration_date").toLocalDate())
                                .occupation(MemberOccupation.valueOf(rs.getString("occupation")))
                                .build();
                    }
                }
            }
        }
        return null;
    }

    public boolean isConfirmedMember(String memberId) throws SQLException {
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

    public Long getDaysOfMembership(String memberId) throws SQLException {
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

    public Long countMembersByCollectivityWithSeniority(String collectivityId, long minDays) throws SQLException {
        String sqlWithCollectivityId = "SELECT COUNT(m.id) as count FROM member m " +
                "WHERE m.collectivity_id = ? AND m.registration_date <= NOW() - INTERVAL ? DAY";
        String sqlWithCollectivityNumber = "SELECT COUNT(m.id) as count FROM member m " +
                "JOIN collectivity c ON c.number = substring(m.id from '^C([0-9]+)-') " +
                "WHERE c.id = ? AND m.registration_date <= NOW() - INTERVAL ? DAY";

        try (Connection conn = dbConnection.getConnection()) {
            boolean hasCollectivityId = hasMemberCollectivityIdColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(hasCollectivityId ? sqlWithCollectivityId : sqlWithCollectivityNumber)) {
                ps.setObject(1, collectivityId);
                ps.setLong(2, minDays);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("count");
                    }
                }
            }
        }
        return 0L;
    }

    public Long countActiveMembers(String collectivityId) throws SQLException {
        String sqlWithCollectivityId = "SELECT COUNT(m.id) as count FROM member m WHERE m.collectivity_id = ?";
        String sqlWithCollectivityNumber = "SELECT COUNT(m.id) as count FROM member m " +
                "JOIN collectivity c ON c.number = substring(m.id from '^C([0-9]+)-') " +
                "WHERE c.id = ?";

        try (Connection conn = dbConnection.getConnection()) {
            boolean hasCollectivityId = hasMemberCollectivityIdColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(hasCollectivityId ? sqlWithCollectivityId : sqlWithCollectivityNumber)) {
                ps.setObject(1, collectivityId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("count");
                    }
                }
            }
        }
        return 0L;
    }

    private String getMemberProfessionColumnName(Connection conn) throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        try (ResultSet rs = metadata.getColumns(null, null, "member", "profession")) {
            if (rs.next()) {
                return "profession";
            }
        }
        try (ResultSet rs = metadata.getColumns(null, null, "member", "job")) {
            if (rs.next()) {
                return "job";
            }
        }
        return null;
    }

    private String getMemberPhoneColumnName(Connection conn) throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        try (ResultSet rs = metadata.getColumns(null, null, "member", "phone_number")) {
            if (rs.next()) {
                return "phone_number";
            }
        }
        try (ResultSet rs = metadata.getColumns(null, null, "member", "phone")) {
            if (rs.next()) {
                return "phone";
            }
        }
        return null;
    }

    private boolean hasMemberCollectivityIdColumn(Connection conn) throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        try (ResultSet rs = metadata.getColumns(null, null, "member", "collectivity_id")) {
            return rs.next();
        }
    }
}