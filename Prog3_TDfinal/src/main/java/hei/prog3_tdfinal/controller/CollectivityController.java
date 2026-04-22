package hei.prog3_tdfinal.controller;

import hei.prog3_tdfinal.dto.UpdateCollectivityIdentityRequest;
import hei.prog3_tdfinal.entity.Collectivity;
import hei.prog3_tdfinal.service.CollectivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/collectivities")
@RequiredArgsConstructor
public class CollectivityController {

    private final CollectivityService service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCollectivities(@RequestBody List<Map<String, Object>> collectives) throws SQLException {
        for (Map<String, Object> data : collectives) {
            service.createCollectivity(data);
        }
    }


    @PatchMapping("/{id}")
    public Collectivity updateIdentity(
            @PathVariable UUID id,
            @RequestBody UpdateCollectivityIdentityRequest request) throws SQLException {
        return service.assignIdentity(id, request.getName(), request.getNumber());
    }
}