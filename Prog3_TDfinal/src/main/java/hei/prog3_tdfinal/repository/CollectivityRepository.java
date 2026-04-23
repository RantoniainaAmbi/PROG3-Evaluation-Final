package hei.prog3_tdfinal.repository;

import hei.prog3_tdfinal.config.DBConnection;
import hei.prog3_tdfinal.entity.Collectivity;
import hei.prog3_tdfinal.entity.Gender;
import hei.prog3_tdfinal.entity.Member;
import hei.prog3_tdfinal.entity.MemberOccupation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class CollectivityRepository {
    private final DBConnection dbConnection;

    public void save(Map<String, Object> data) throws SQLException {
        String sql = "INSERT INTO collectivity (location, specialty, creation_date, federation_approval) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, (String) data.get("location"));
            ps.setString(2, (String) data.get("specialty"));
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            ps.setBoolean(4, data.get("federationApproval") != null && (Boolean) data.get("federationApproval"));
            ps.executeUpdate();
        }
    }

    public void updateIdentity(UUID id, String name, String number) throws SQLException {
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

    public Collectivity findById(UUID id) throws SQLException {
        String sql = "SELECT id, name, number FROM collectivity WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Collectivity.builder()
                            .id((UUID) rs.getObject("id"))
                            .name(rs.getString("name"))
                            .number(rs.getString("number"))
                            .build();
                }
            }
        }
        return null;
    }

    
    public Collectivity findByIdFull(UUID id) throws SQLException {
        String sql = """
                SELECT
                    c.id            AS c_id,
                    c.name          AS c_name,
                    c.number        AS c_number,
                    c.location      AS c_location,
                    c.federation_approval,
                    m.id            AS m_id,
                    m.first_name,
                    m.last_name,
                    m.birth_date,
                    m.gender,
                    m.address,
                    m.profession,
                    m.phone_number,
                    m.email,
                    m.occupation,
                    m.registration_date
                FROM collectivity c
                LEFT JOIN member m ON m.collectivity_id = c.id
                WHERE c.id = ?
                """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                Collectivity collectivity = null;
                List<Member> members = new ArrayList<>();

                while (rs.next()) {
                    if (collectivity == null) {
                        collectivity = Collectivity.builder()
                                .id((UUID) rs.getObject("c_id"))
                                .name(rs.getString("c_name"))
                                .number(rs.getString("c_number"))
                                .location(rs.getString("c_location"))
                                .federationApproval(rs.getBoolean("federation_approval"))
                                .build();
                    }

                    UUID memberId = (UUID) rs.getObject("m_id");
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