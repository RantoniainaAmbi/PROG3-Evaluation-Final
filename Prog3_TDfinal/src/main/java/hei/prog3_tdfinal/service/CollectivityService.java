package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.config.DBConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CollectivityService {
    private final DBConnection dbConnection;

    public void createCollectivity(Map<String, Object> data) throws SQLException {
        String sql = "INSERT INTO collectivity (name, description) VALUES (?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, (String) data.get("name"));
            ps.setString(2, (String) data.get("description"));

            ps.executeUpdate();
        }
    }
}
