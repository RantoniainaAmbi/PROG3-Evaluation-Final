package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.config.DBConnection;
import hei.prog3_tdfinal.entity.Collectivity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectivityService {
    private final DBConnection dbConnection;
    private final CollectivityRepository repository;

    public void createCollectivity(Map<String, Object> data) throws SQLException {
        String sql = "INSERT INTO collectivity (name, description) VALUES (?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, (String) data.get("name"));
            ps.setString(2, (String) data.get("description"));

            ps.executeUpdate();
        }
    }

    public Collectivity assignIdentity(UUID id, String newName, String newNumber) {
        Collectivity current = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found"));

        if (current.getName() != null || current.getNumber() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and number are immutable once assigned");
        }

        if (repository.existsByNameOrNumber(newName, newNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Name or number already exists");
        }

        current.setName(newName);
        current.setNumber(newNumber);
        return repository.save(current);
    }
}
