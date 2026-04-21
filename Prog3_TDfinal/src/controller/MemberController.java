import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                // ... mapper les autres champs
                
                ps.setObject(9, collectiveId);
                ps.executeUpdate();
                
                return ResponseEntity.status(HttpStatus.CREATED).body("Member added successfully");
            }
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        }
    }
    
}

