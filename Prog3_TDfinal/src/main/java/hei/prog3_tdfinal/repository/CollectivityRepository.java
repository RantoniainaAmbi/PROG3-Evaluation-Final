package hei.prog3_tdfinal.repository;

import hei.prog3_tdfinal.config.DBConnection;
import hei.prog3_tdfinal.entity.Collectivity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

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
}
