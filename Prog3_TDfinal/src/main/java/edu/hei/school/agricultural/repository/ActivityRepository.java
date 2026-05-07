package edu.hei.school.agricultural.repository;


import edu.hei.school.agricultural.entity.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ActivityRepository {
    private final Connection connection;

    public Activity save(String collectivityId, Activity activity) throws SQLException {
        String sql = """
            INSERT INTO activity (id, label, type, collectivity_id, executive_date, week_ordinal, day_of_week) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, activity.getId());
            ps.setString(2, activity.getLabel());
            ps.setString(3, activity.getType());
            ps.setString(4, collectivityId);
            ps.setDate(5, activity.getExecutiveDate() != null ? Date.valueOf(activity.getExecutiveDate()) : null);

            if (activity.getWeekOrdinal() != null) {
                ps.setInt(6, activity.getWeekOrdinal());
                ps.setString(7, activity.getDayOfWeek());
            } else {
                ps.setNull(6, Types.INTEGER);
                ps.setNull(7, Types.VARCHAR);
            }
            ps.executeUpdate();

            saveOccupations(activity.getId(), activity.getOccupationsConcerned());
        }
        return activity;
    }

    public List<Activity> findAllByCollectivity(String collectivityId) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = """
            SELECT a.id, a.label, a.type, a.executive_date, a.week_ordinal, a.day_of_week, ao.occupation
            FROM activity a
            LEFT JOIN activity_occupation ao ON a.id = ao.activity_id
            WHERE a.collectivity_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Activity> map = new HashMap<>();
                while (rs.next()) {
                    String id = rs.getString("id");
                    Activity activity = map.computeIfAbsent(id, k -> {
                        try {
                            return Activity.builder()
                                    .id(id)
                                    .label(rs.getString("label"))
                                    .type(rs.getString("type"))
                                    .executiveDate(rs.getDate("executive_date") != null ? rs.getDate("executive_date").toLocalDate() : null)
                                    .weekOrdinal(rs.getObject("week_ordinal") != null ? rs.getInt("week_ordinal") : null)
                                    .dayOfWeek(rs.getString("day_of_week"))
                                    .occupationsConcerned(new ArrayList<>())
                                    .build();
                        } catch (SQLException e) { throw new RuntimeException(e); }
                    });
                    if (rs.getString("occupation") != null) {
                        activity.getOccupationsConcerned().add(rs.getString("occupation"));
                    }
                }
                activities.addAll(map.values());
            }
        }
        return activities;
    }

    private void saveOccupations(String activityId, List<String> occupations) throws SQLException {
        if (occupations == null) return;
        String sql = "INSERT INTO activity_occupation (activity_id, occupation) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String occ : occupations) {
                ps.setString(1, activityId);
                ps.setString(2, occ);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}