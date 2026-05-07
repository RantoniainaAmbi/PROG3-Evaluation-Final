package edu.hei.school.agricultural.controller.mapper;

import edu.hei.school.agricultural.controller.dto.CollectivityActivity;
import edu.hei.school.agricultural.controller.dto.MonthlyRecurrenceRule;
import edu.hei.school.agricultural.entity.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityDtoMapper {

    public Activity toEntity(CollectivityActivity dto) {
        return Activity.builder()
                .id(dto.getId())
                .label(dto.getLabel())
                .type(dto.getActivityType())
                .occupationsConcerned(dto.getMemberOccupationConcerned())
                .executiveDate(dto.getExecutiveDate())
                .weekOrdinal(dto.getRecurrenceRule() != null ? dto.getRecurrenceRule().getWeekOrdinal() : null)
                .dayOfWeek(dto.getRecurrenceRule() != null ? dto.getRecurrenceRule().getDayOfWeek() : null)
                .build();
    }

    public CollectivityActivity toDto(Activity entity) {
        CollectivityActivity dto = new CollectivityActivity();
        dto.setId(entity.getId());
        dto.setLabel(entity.getLabel());
        dto.setActivityType(entity.getType());
        dto.setMemberOccupationConcerned(entity.getOccupationsConcerned());
        dto.setExecutiveDate(entity.getExecutiveDate());

        if (entity.getWeekOrdinal() != null || entity.getDayOfWeek() != null) {
            MonthlyRecurrenceRule rule = new MonthlyRecurrenceRule();
            rule.setWeekOrdinal(entity.getWeekOrdinal());
            rule.setDayOfWeek(entity.getDayOfWeek());
            dto.setRecurrenceRule(rule);
        }
        return dto;
    }
}