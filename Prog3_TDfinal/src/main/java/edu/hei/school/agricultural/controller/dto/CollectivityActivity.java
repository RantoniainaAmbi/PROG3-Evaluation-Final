package edu.hei.school.agricultural.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CollectivityActivity extends CreateCollectivityActivityDTO {
    private String id;
}