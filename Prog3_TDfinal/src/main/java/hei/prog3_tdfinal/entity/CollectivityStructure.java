package hei.prog3_tdfinal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectivityStructure {
    private Member president;
    private Member vicePresident;
    private Member treasurer;
    private Member secretary;
}