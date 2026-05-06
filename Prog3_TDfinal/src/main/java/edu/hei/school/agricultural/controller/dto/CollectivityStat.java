package edu.hei.school.agricultural.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityStat {
    private String id;
    private List<CollectivityLocalStatistics> collectivityLocalStats;
}
