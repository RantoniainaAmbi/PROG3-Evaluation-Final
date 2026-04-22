package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.entity.Collectivity;
import hei.prog3_tdfinal.repository.CollectivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectivityService {
    private final CollectivityRepository repository;


    public void createCollectivity(Map<String, Object> data) throws SQLException {
        repository.save(data);
    }


    public Collectivity assignIdentity(UUID id, String newName, String newNumber) throws SQLException {
        Collectivity current = repository.findById(id);

        if (current == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
        }


        if (current.getName() != null || current.getNumber() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Identity assignment is final. Name and number cannot be changed once set.");
        }


        if (repository.existsByName(newName) || repository.existsByNumber(newNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "The provided name or number is already in use by another collectivity.");
        }

        repository.updateIdentity(id, newName, newNumber);

        current.setName(newName);
        current.setNumber(newNumber);

        return current;
    }
}