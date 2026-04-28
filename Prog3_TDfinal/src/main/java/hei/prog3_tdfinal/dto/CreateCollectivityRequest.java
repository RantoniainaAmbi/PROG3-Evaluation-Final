package hei.prog3_tdfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectivityRequest {
    private String location;
    private List<String> members;
    private boolean federationApproval;
    private CreateCollectivityStructureRequest structure;
}