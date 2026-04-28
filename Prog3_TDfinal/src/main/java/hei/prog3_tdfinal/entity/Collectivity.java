package hei.prog3_tdfinal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collectivity {
    private String id;
    private String name;
    private String number;
    private String location;
    private boolean federationApproval;
    private CollectivityStructure structure;
    private List<Member> members;
}

