package hei.prog3_tdfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectivityStructureRequest {
    private UUID president;
    private UUID vicePresident;
    private UUID treasurer;
    private UUID secretary;
}