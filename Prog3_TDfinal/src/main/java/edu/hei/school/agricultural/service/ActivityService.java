package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.CollectivityActivity;
import edu.hei.school.agricultural.controller.mapper.ActivityDtoMapper;
import edu.hei.school.agricultural.entity.Activity;
import edu.hei.school.agricultural.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityDtoMapper activityMapper;

    public List<CollectivityActivity> addActivities(String collectivityId, List<CollectivityActivity> dtos) {
        return dtos.stream().map(dto -> {
            if (dto.getExecutiveDate() == null && dto.getRecurrenceRule() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Either executive date or recurrence rule must be provided");
            }

            Activity entity = activityMapper.toEntity(dto);

            if (dto.getRecurrenceRule() != null) {
                entity.setWeekOrdinal(dto.getRecurrenceRule().getWeekOrdinal());
                entity.setDayOfWeek(dto.getRecurrenceRule().getDayOfWeek());
                entity.setExecutiveDate(null);
            } else {
                entity.setExecutiveDate(dto.getExecutiveDate());
                entity.setWeekOrdinal(null);
                entity.setDayOfWeek(null);
            }

            try {
                activityRepository.save(collectivityId, entity);
                return dto;
            } catch (SQLException e) {
                throw new RuntimeException("Error saving activity", e);
            }
        }).collect(Collectors.toList());
    }

    public List<CollectivityActivity> getActivitiesByCollectivity(String id) {
        try {
            List<Activity> entities = activityRepository.findAllByCollectivity(id);
            return entities.stream()
                    .map(activityMapper::toDto)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching activities for collectivity: " + id, e);
        }
    }
}