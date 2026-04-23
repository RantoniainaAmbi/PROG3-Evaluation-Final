package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.dto.FinancialAccountResponse;
import hei.prog3_tdfinal.entity.Collectivity;
import hei.prog3_tdfinal.repository.CollectivityRepository;
import hei.prog3_tdfinal.repository.FinancialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectivityService {

    private final CollectivityRepository repository;
    private final FinancialAccountRepository financialAccountRepository;

    public Collectivity createCollectivity(Map<String, Object> data) throws SQLException {
        if (data.get("location") == null || ((String) data.get("location")).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location is required");
        }
        if (data.get("specialty") == null || ((String) data.get("specialty")).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specialty is required");
        }

        Object federationApprovalObj = data.get("federationApproval");
        boolean federationApproval = federationApprovalObj instanceof Boolean && (Boolean) federationApprovalObj;
        if (!federationApproval) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Federation approval is mandatory to create a collectivity");
        }

        @SuppressWarnings("unchecked")
        List<UUID> memberIds = (List<UUID>) data.get("members");
        if (memberIds == null || memberIds.size() < 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least 10 members are required to create a collectivity");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> structure = (Map<String, Object>) data.get("structure");
        if (structure == null
                || !structure.containsKey("president")
                || !structure.containsKey("vicePresident")
                || !structure.containsKey("treasurer")
                || !structure.containsKey("secretary")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "All specific positions (president, vice-president, treasurer, secretary) must be assigned");
        }

        UUID collectivityId = UUID.randomUUID();
        long seniorMembersCount = repository.countMembersWithMinSeniority(collectivityId, 180);
        if (seniorMembersCount < 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "At least 5 members with 6+ months seniority are required to create a collectivity");
        }

        UUID savedId = repository.save(data);

        return Collectivity.builder()
                .id(savedId)
                .location((String) data.get("location"))
                .build();
    }

    public Collectivity assignIdentity(UUID id, String newName, String newNumber) throws SQLException {
        Collectivity current = repository.findById(id);
        if (current == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Collectivity with ID " + id + " not found");
        }

        if (newName == null || newName.trim().isEmpty()
                || newNumber == null || newNumber.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name and number cannot be empty");
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

    
    public Collectivity getById(UUID id) throws SQLException {
        Collectivity collectivity = repository.findByIdFull(id);
        if (collectivity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Collectivity with ID " + id + " not found");
        }
        return collectivity;
    }

    
    public List<FinancialAccountResponse> getFinancialAccounts(UUID id, LocalDate at)
            throws SQLException {
        Collectivity collectivity = repository.findById(id);
        if (collectivity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Collectivity with ID " + id + " not found");
        }
        return financialAccountRepository.findByCollectivityId(id, at);
    }
}