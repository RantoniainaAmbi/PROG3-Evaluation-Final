package hei.prog3_tdfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectivityResponse {
    private String id;
    private String name;
    private String number;
    private String location;
    private String specialty;
    private LocalDate creationDate;
    private boolean federationApproval;
    private List<MemberResponse> members;
}
