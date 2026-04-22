package hei.prog3_tdfinal.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import hei.prog3_tdfinal.config.DBConnection;


@RestController
@RequestMapping
public class MemberController {

    private final DBConnection dbConnection;

    public MemberController(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @PostMapping("/collectivity/{collectivityId}/members")
    public ResponseEntity<String> addMemberToCollectivity(@PathVariable UUID collectiviteId, @RequestBody Map<String, Object> newMember) {
        
        try (Connection conn = dbConnection.getConnection()) {
            String sql = "INSERT INTO member (first_name, last_name, birth_date, gender, address, profession, phone_number, email, collectivity_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, (String) newMember.get("firstName"));
                ps.setString(2, (String) newMember.get("name"));

                
                ps.setObject(9, collectiviteId);
                ps.executeUpdate();
                
                return ResponseEntity.status(HttpStatus.CREATED).body("Member added successfully");
            }
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        }
    }
    
}

