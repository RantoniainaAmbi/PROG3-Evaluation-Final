package edu.hei.school.agricultural.service;

import edu.hei.school.agricultural.controller.dto.CollectivityActivity;
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

    public List<CollectivityActivity> addActivities(String collectivityId, List<CollectivityActivity> dtos) {
        return dtos.stream().map(dto -> {
            if (dto.getExecutiveDate() != null && dto.getRecurrenceRule() != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Executive date and recurrence rule are mutually exclusive"
                );            }

            Activity entity = Activity.builder()
                    .id(dto.getId())
                    .label(dto.getLabel())
                    .type(dto.getActivityType())
                    .occupationsConcerned(dto.getMemberOccupationConcerned())
                    .executiveDate(dto.getExecutiveDate())
                    .weekOrdinal(dto.getRecurrenceRule() != null ? dto.getRecurrenceRule().getWeekOrdinal() : null)
                    .dayOfWeek(dto.getRecurrenceRule() != null ? dto.getRecurrenceRule().getDayOfWeek() : null)
                    .build();

            try {
                activityRepository.save(collectivityId, entity);
                return dto;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}