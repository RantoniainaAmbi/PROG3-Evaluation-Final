package hei.prog3_tdfinal.repository;

import hei.prog3_tdfinal.config.DBConnection;
import hei.prog3_tdfinal.dto.FederationStatisticsDto;
import hei.prog3_tdfinal.entity.Collectivity;
import hei.prog3_tdfinal.entity.Gender;
import hei.prog3_tdfinal.entity.Member;
import hei.prog3_tdfinal.entity.MemberOccupation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class CollectivityRepository {
    private final DBConnection dbConnection;

    public String save(Map<String, Object> data) throws SQLException {
        String collectivityId = java.util.UUID.randomUUID().toString();
        try (Connection conn = dbConnection.getConnection()) {
            String locationColumn = getCollectivityLocationColumnName(conn);
            boolean hasFederationApproval = hasCollectivityFederationApprovalColumn(conn);

            StringBuilder sqlBuilder = new StringBuilder("INSERT INTO collectivity (id");
            if (locationColumn != null) {
                sqlBuilder.append(", ").append(locationColumn);
            }
            sqlBuilder.append(", specialty, creation_date");
            if (hasFederationApproval) {
                sqlBuilder.append(", federation_approval");
            }
            sqlBuilder.append(") VALUES (?");
            if (locationColumn != null) {
                sqlBuilder.append(", ?");
            }
            sqlBuilder.append(", ?, ?");
            if (hasFederationApproval) {
                sqlBuilder.append(", ?");
            }
            sqlBuilder.append(")");

            String sql = sqlBuilder.toString();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int idx = 1;
                ps.setObject(idx++, collectivityId);
                if (locationColumn != null) {
                    ps.setString(idx++, (String) data.get("location"));
                }
                ps.setString(idx++, (String) data.get("specialty"));
                ps.setDate(idx++, new java.sql.Date(System.currentTimeMillis()));
                if (hasFederationApproval) {
                    ps.setBoolean(idx++, data.get("federationApproval") != null && (Boolean) data.get("federationApproval"));
                }
                ps.executeUpdate();
            }
        }
        return collectivityId;
    }

    public void updateIdentity(String id, String name, String number) throws SQLException {
        String sql = "UPDATE collectivity SET name = ?, number = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, number);
            ps.setObject(3, id);
            ps.executeUpdate();
        }
    }

    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT COUNT(id) FROM collectivity WHERE name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean existsByNumber(String number) throws SQLException {
        String sql = "SELECT COUNT(id) FROM collectivity WHERE number = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public Collectivity findById(String id) throws SQLException {
        String sql = "SELECT id, name, number FROM collectivity WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Collectivity.builder()
                            .id(rs.getString("id"))
                            .name(rs.getString("name"))
                            .number(rs.getString("number"))
                            .build();
                }
            }
        }
        return null;
    }

    
    public Collectivity findByIdFull(String id) throws SQLException {
        try (Connection conn = dbConnection.getConnection()) {
            String joinCondition = hasMemberCollectivityIdColumn(conn)
                    ? "LEFT JOIN member m ON m.collectivity_id = c.id"
                    : "LEFT JOIN member m ON c.number = substring(m.id from '^C([0-9]+)-')";

            String locationColumn = getCollectivityLocationColumnName(conn);
            boolean hasFederationApproval = hasCollectivityFederationApprovalColumn(conn);
            String memberProfessionColumn = getMemberProfessionColumnName(conn);
            String memberPhoneColumn = getMemberPhoneColumnName(conn);
            String selectLocation = locationColumn != null ? "c." + locationColumn + " AS c_location" : "NULL AS c_location";
            String selectFederationApproval = hasFederationApproval ? "c.federation_approval" : "NULL AS federation_approval";
            String selectProfession = memberProfessionColumn != null ? "m." + memberProfessionColumn + " AS profession" : "NULL AS profession";
            String selectPhone = memberPhoneColumn != null ? "m." + memberPhoneColumn + " AS phone_number" : "NULL AS phone_number";
            String sql = "SELECT\n"
                    + "    c.id            AS c_id,\n"
                    + "    c.name          AS c_name,\n"
                    + "    c.number        AS c_number,\n"
                    + "    " + selectLocation + ",\n"
                    + "    " + selectFederationApproval + ",\n"
                    + "    m.id            AS m_id,\n"
                    + "    m.first_name,\n"
                    + "    m.last_name,\n"
                    + "    m.birth_date,\n"
                    + "    m.gender,\n"
                    + "    m.address,\n"
                    + "    " + selectProfession + ",\n"
                    + "    " + selectPhone + ",\n"
                    + "    m.email,\n"
                    + "    m.occupation,\n"
                    + "    m.registration_date\n"
                    + "FROM collectivity c\n"
                    + joinCondition + "\n"
                    + "WHERE c.id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    Collectivity collectivity = null;
                    List<Member> members = new ArrayList<>();

                    while (rs.next()) {
                        if (collectivity == null) {
                            collectivity = Collectivity.builder()
                                    .id(rs.getString("c_id"))
                                    .name(rs.getString("c_name"))
                                    .number(rs.getString("c_number"))
                                    .location(rs.getString("c_location"))
                                    .federationApproval(rs.getBoolean("federation_approval"))
                                    .build();
                        }

                        String memberId = rs.getString("m_id");
                        if (memberId != null) {
                            String genderStr = rs.getString("gender");
                            String occupationStr = rs.getString("occupation");

                            Member member = Member.builder()
                                    .id(memberId)
                                    .firstName(rs.getString("first_name"))
                                    .lastName(rs.getString("last_name"))
                                    .birthDate(rs.getDate("birth_date") != null
                                            ? rs.getDate("birth_date").toLocalDate() : null)
                                    .gender(genderStr != null ? Gender.valueOf(genderStr) : null)
                                    .address(rs.getString("address"))
                                    .profession(rs.getString("profession"))
                                    .phoneNumber(rs.getLong("phone_number"))
                                    .email(rs.getString("email"))
                                    .occupation(occupationStr != null
                                            ? MemberOccupation.valueOf(occupationStr) : null)
                                    .registrationDate(rs.getDate("registration_date") != null
                                            ? rs.getDate("registration_date").toLocalDate() : null)
                                    .build();
                            members.add(member);
                        }
                    }

                    if (collectivity != null) {
                        collectivity.setMembers(members);
                    }
                    return collectivity;
                }
            }
        }
    }

    public long countActiveMembersInCollectivity(String collectivityId) throws SQLException {
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

    public long countMembersWithMinSeniority(String collectivityId, int minDays) throws SQLException {
        String sqlWithCollectivityId = "SELECT COUNT(m.id) as count FROM member m " +
                "WHERE m.collectivity_id = ? AND m.registration_date <= CURRENT_DATE - INTERVAL ? DAY";
        String sqlWithCollectivityNumber = "SELECT COUNT(m.id) as count FROM member m " +
                "JOIN collectivity c ON c.number = substring(m.id from '^C([0-9]+)-') " +
                "WHERE c.id = ? AND m.registration_date <= CURRENT_DATE - INTERVAL ? DAY";

        try (Connection conn = dbConnection.getConnection()) {
            boolean hasCollectivityId = hasMemberCollectivityIdColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(hasCollectivityId ? sqlWithCollectivityId : sqlWithCollectivityNumber)) {
                ps.setObject(1, collectivityId);
                ps.setInt(2, minDays);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("count");
                    }
                }
            }
        }
        return 0L;
    }

    public long countMembersWithMinSeniorityByIds(List<String> memberIds, int minDays) throws SQLException {
        if (memberIds == null || memberIds.isEmpty()) {
            return 0L;
        }

        String placeholders = String.join(", ", Collections.nCopies(memberIds.size(), "?"));
        String sql = "SELECT COUNT(id) AS count FROM member WHERE id IN (" + placeholders + ") " +
                "AND registration_date <= CURRENT_DATE - INTERVAL ? DAY";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            for (String memberId : memberIds) {
                ps.setString(idx++, memberId);
            }
            ps.setInt(idx, minDays);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("count");
                }
            }
        }
        return 0L;
    }

    public List<FederationStatisticsDto.CollectivityFederationStatDto> getFederationStatistics(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sqlWithCollectivityId = "SELECT c.name, COUNT(DISTINCT m.id) as new_members FROM collectivity c " +
                "LEFT JOIN member m ON m.collectivity_id = c.id AND m.registration_date >= ? AND m.registration_date <= ? " +
                "GROUP BY c.id, c.name";
        String sqlWithCollectivityNumber = "SELECT c.name, COUNT(DISTINCT m.id) as new_members FROM collectivity c " +
                "LEFT JOIN member m ON c.number = substring(m.id from '^C([0-9]+)-') AND m.registration_date >= ? AND m.registration_date <= ? " +
                "GROUP BY c.id, c.name";

        List<FederationStatisticsDto.CollectivityFederationStatDto> statistics = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection()) {
            boolean hasCollectivityId = hasMemberCollectivityIdColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(hasCollectivityId ? sqlWithCollectivityId : sqlWithCollectivityNumber)) {
                ps.setDate(1, Date.valueOf(startDate));
                ps.setDate(2, Date.valueOf(endDate));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        FederationStatisticsDto.CollectivityFederationStatDto stat = 
                                FederationStatisticsDto.CollectivityFederationStatDto.builder()
                                .collectivityName(rs.getString("name"))
                                .newMembers(rs.getLong("new_members"))
                                .attendanceRate(0.0)
                                .paidUpPercentage(0.0)
                                .build();
                        statistics.add(stat);
                    }
                }
            }
        }
        return statistics;
    }

    private boolean hasMemberCollectivityIdColumn(Connection conn) throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        try (ResultSet rs = metadata.getColumns(null, null, "member", "collectivity_id")) {
            return rs.next();
        }
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

    private String getCollectivityLocationColumnName(Connection conn) throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        try (ResultSet rsLocation = metadata.getColumns(null, null, "collectivity", "location")) {
            if (rsLocation.next()) {
                return "location";
            }
        }
        try (ResultSet rsCity = metadata.getColumns(null, null, "collectivity", "city")) {
            if (rsCity.next()) {
                return "city";
            }
        }
        return null;
    }

    private boolean hasCollectivityFederationApprovalColumn(Connection conn) throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        try (ResultSet rs = metadata.getColumns(null, null, "collectivity", "federation_approval")) {
            return rs.next();
        }
    }
}