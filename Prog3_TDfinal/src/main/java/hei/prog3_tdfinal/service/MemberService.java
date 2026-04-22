package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.config.DBConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final DBConnection dbConnection;

    public void addMemberToCollectivity(UUID collectiviteId, Map<String, Object> data) throws SQLException {
        String sql = "INSERT INTO member (first_name, last_name, birth_date, gender, address, phone, email, collectivity_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, (String) data.get("firstName"));
            ps.setString(2, (String) data.get("lastName"));
            ps.setDate(3, Date.valueOf((String) data.get("birthDate"))); // Format YYYY-MM-DD
            ps.setString(4, (String) data.get("gender"));
            ps.setString(5, (String) data.get("address"));
            ps.setString(6, (String) data.get("phone"));
            ps.setString(7, (String) data.get("email"));
            ps.setObject(8, collectiviteId);

            ps.executeUpdate();
        }
    }
}