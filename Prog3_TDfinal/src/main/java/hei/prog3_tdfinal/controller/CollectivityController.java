package hei.prog3_tdfinal.controller;

import hei.prog3_tdfinal.config.DBConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/collectives")
public class CollectivityController {

    private final DBConnection dbConnection;

    public CollectivityController(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @PostMapping
    public ResponseEntity<String> createCollective(@RequestBody Map<String, Object> newCollective) {
        try (Connection conn = dbConnection.getConnection()) {
            String sql = "INSERT INTO collectivity (location, federation_approval) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, (String) newCollective.get("city"));
                ps.setBoolean(2, (Boolean) newCollective.get("federationAuthorization"));
                ps.executeUpdate();
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    UUID collectiveId = (UUID) rs.getObject(1);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body("Collective created with ID: " + collectiveId);
                }
            }
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
        return ResponseEntity.badRequest().body("Requirements not met");
    }

    
}