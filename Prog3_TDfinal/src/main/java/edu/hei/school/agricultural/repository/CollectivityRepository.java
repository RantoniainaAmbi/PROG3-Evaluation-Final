package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.controller.dto.FederationStatistics;
import edu.hei.school.agricultural.entity.Collectivity;
import edu.hei.school.agricultural.entity.CollectivityStructure;
import edu.hei.school.agricultural.mapper.CollectivityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class CollectivityRepository {
    private final Connection connection;
    private final CollectivityMapper collectivityMapper;

    public List<Collectivity> saveAll(List<Collectivity> collectivities) {
        List<Collectivity> memberList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                """
                        insert into "collectivity" (id, name, number, location, president_id, vice_president_id, treasurer_id, secretary_id) 
                        values (?, ?, ?, ?, ?, ?, ?, ?) 
                        on conflict (id) do update set name = excluded.name,
                                                       number = excluded.number,
                                                       location = excluded.location,
                                                       president_id = excluded.president_id,
                                                       treasurer_id = excluded.treasurer_id,
                                                       secretary_id = excluded.secretary_id 
                        """)) {
            for (Collectivity collectivity : collectivities) {
                CollectivityStructure collectivityStructure = collectivity.getCollectivityStructure();
                preparedStatement.setString(1, collectivity.getId());
                preparedStatement.setString(2, collectivity.getName());
                Integer number = collectivity.getNumber();
                if (number == null) {
                    preparedStatement.setNull(3, Types.INTEGER);
                } else {
                    preparedStatement.setInt(3, number);
                }
                preparedStatement.setObject(4, collectivity.getLocation());
                preparedStatement.setString(5, collectivityStructure.getPresident().getId());
                preparedStatement.setString(6, collectivityStructure.getVicePresident().getId());
                preparedStatement.setString(7, collectivityStructure.getTreasurer().getId());
                preparedStatement.setString(8, collectivityStructure.getSecretary().getId());
                preparedStatement.addBatch();
            }
            var executedRow = preparedStatement.executeBatch();
            for (int i = 0; i < executedRow.length; i++) {
                memberList.add(findById(collectivities.get(i).getId()).orElseThrow());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return memberList;
    }


    public boolean isNumberExists(Integer number) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id
                from "collectivity"
                where number = ?
                """)) {
            preparedStatement.setInt(1, number);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean isNameExists(String name) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id
                from "collectivity"
                where name = ?
                """)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public Optional<Collectivity> findById(String id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id, name, number, location, president_id, vice_president_id, treasurer_id, secretary_id
                from "collectivity"
                where id = ?
                """)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(collectivityMapper.mapFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<Collectivity> findAllByMemberId(String memberId) {
        List<Collectivity> collectivities = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select id, name, number, location, president_id, vice_president_id, treasurer_id, secretary_id
                from "collectivity" 
                join "collectivity_member" on collectivity.id = collectivity_member.collectivity_id
                where collectivity_member.member_id = ?
                """)) {
            preparedStatement.setString(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                collectivities.add(collectivityMapper.mapFromResultSet(resultSet));
            }
            return collectivities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FederationStatistics> getFederationStatistics(LocalDate start, LocalDate end) {
        List<FederationStatistics> stats = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("""
        select
            c.id as collectivity_id,
            c.name as collectivity_name,
            c.number as collectivity_number,
            count(distinct cm.member_id) as total_members,
            count(distinct case 
                when mf_active.id is not null 
                     and coalesce(paid.paid_amount, 0) >= mf_active.total_due 
                then cm.member_id 
            end) as up_to_date_members,
            count(distinct case 
                when cm.registration_date between ? and ?
                then cm.member_id 
            end) as new_members
        from collectivity c
        left join collectivity_member cm on c.id = cm.collectivity_id
        left join (
            select collectivity_id, sum(amount) as total_due
            from membership_fee
            where status = 'ACTIVE'::activity_status
            group by collectivity_id
        ) mf_active on c.id = mf_active.collectivity_id
        left join (
            select t.collectivity_id, t.member_id, sum(t.amount) as paid_amount
            from "transaction" t
            where t.creation_date between ? and ?
            group by t.collectivity_id, t.member_id
        ) paid on c.id = paid.collectivity_id and cm.member_id = paid.member_id
        group by c.id, c.name, c.number
        """)) {
            ps.setDate(1, java.sql.Date.valueOf(start));
            ps.setDate(2, java.sql.Date.valueOf(end));
            ps.setDate(3, java.sql.Date.valueOf(start));
            ps.setDate(4, java.sql.Date.valueOf(end));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int totalMembers = rs.getInt("total_members");
                int upToDate = rs.getInt("up_to_date_members");
                double percentage = totalMembers == 0 ? 0.0 : (upToDate * 100.0 / totalMembers);

                stats.add(FederationStatistics.builder()
                        .collectivityId(rs.getString("collectivity_id"))
                        .collectivityName(rs.getString("collectivity_name"))
                        .collectivityNumber(rs.getObject("collectivity_number", Integer.class))
                        .upToDateMembersPercentage(Math.round(percentage * 100.0) / 100.0)
                        .newMembersCount(rs.getInt("new_members"))
                        .build());
            }
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
