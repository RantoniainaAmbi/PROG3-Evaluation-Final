package hei.prog3_tdfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectivityRequest {
    private String location;
    private List<UUID> members;
    private boolean federationApproval;
    private CreateCollectivityStructureRequest structure;
}