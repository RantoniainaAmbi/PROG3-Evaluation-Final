package edu.hei.school.agricultural.controller;

import edu.hei.school.agricultural.controller.dto.CollectivityActivity;
import edu.hei.school.agricultural.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @PostMapping("/{id}/activities")
    public List<CollectivityActivity> addActivities(
            @PathVariable String id,
            @RequestBody List<CollectivityActivity> activities) {

        return activityService.addActivities(id, activities);
    }

    @GetMapping("/{id}/activities")
    public List<CollectivityActivity> getActivities(@PathVariable String id) {
        return activityService.getActivitiesByCollectivity(id);
    }
}