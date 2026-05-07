package edu.hei.school.agricultural.repository;

import edu.hei.school.agricultural.controller.dto.*;
import edu.hei.school.agricultural.entity.ActivityStatus;
import edu.hei.school.agricultural.entity.Collectivity;
import edu.hei.school.agricultural.entity.Member;
import edu.hei.school.agricultural.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final Connection connection;
    private final MemberMapper memberMapper;
    private final CollectivityMemberRepository collectivityMemberRepository;
    private final MemberRefereeRepository memberRefereeRepository;


    public List<Member> saveAll(List<Member> members) {
        List<Member> memberList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                """
                        insert into "member" (id, 
                                              first_name,
                                              last_name,
                                              birth_date,
                                              gender,
                                              address,
                                              profession,
                                              phone_number,
                                              email,
                                              occupation,
                                              registration_fee_paid,
                                              membership_dues_paid) 
                        values (?, ?, ?, ?, ?::gender, ?, ?, ?, ?, ?::member_occupation, ?, ?) 
                        on conflict (id) do update set first_name = excluded.first_name,
                                                       last_name = excluded.last_name,
                                                       birth_date = excluded.birth_date,
                                                       gender = excluded.gender,
                                                       phone_number = excluded.phone_number,
                                                       email = excluded.email,
                                                       address = excluded.address,
                                                       profession = excluded.profession,
                                                       occupation = excluded.occupation
                        returning id;
                        """)) {
            for (Member member : members) {
                preparedStatement.setString(1, member.getId());
                preparedStatement.setString(2, member.getFirstName());
                preparedStatement.setString(3, member.getLastName());
                preparedStatement.setDate(4, java.sql.Date.valueOf(member.getBirthDate()));
                preparedStatement.setObject(5, member.getGender().name());
                preparedStatement.setString(6, member.getAddress());
                preparedStatement.setString(7, member.getProfession());
                preparedStatement.setString(8, member.getPhoneNumber());
                preparedStatement.setString(9, member.getEmail());
                preparedStatement.setObject(10, member.getOccupation().name());
                preparedStatement.setBoolean(11, member.getRegistrationFeePaid());
                preparedStatement.setBoolean(12, member.getMembershipDuesPaid());
                preparedStatement.addBatch();
            }
            var executedRow = preparedStatement.executeBatch();
            for (int i = 0; i < executedRow.length; i++) {
                Member member = members.get(i);

                attachCollectivityMember(member);
                attachRefereeMember(member);

                memberList.add(findById(member.getId()).orElseThrow());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return memberList;
    }

    private void attachRefereeMember(Member member) {
        List<Member> referees = member.getReferees();
        for (Member referee : referees) {
            memberRefereeRepository.attachMemberReferee(referee, member);
        }
    }

    private void attachCollectivityMember(Member member) {
        List<Collectivity> collectivities = member.getCollectivities();
        for (Collectivity collectivity : collectivities) {
            collectivityMemberRepository.attachMemberToCollectivity(collectivity, member);
        }
    }

    public Optional<Member> findById(String id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select member.id, first_name, last_name, birth_date, gender, phone_number, email, address, profession, occupation,registration_fee_paid, membership_dues_paid
                from "member"
                where id = ?
                """)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var member = memberMapper.mapFromResultSet(resultSet);
                member.setReferees(findRefereesByIdMember(member.getId()));
                return Optional.of(member);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<Member> findAllByCollectivity(Collectivity collectivity) {
        List<Member> memberList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select member.id, first_name, last_name, birth_date, gender, phone_number, email, address, profession, occupation,registration_fee_paid, membership_dues_paid
                from "member"
                    join collectivity_member on member.id = collectivity_member.member_id
                    join collectivity on collectivity.id = collectivity_member.collectivity_id
                where collectivity_member.collectivity_id = ?
                """)) {
            preparedStatement.setString(1, collectivity.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                var memberMapped = memberMapper.mapFromResultSet(resultSet);
                memberMapped.setReferees(findRefereesByIdMember(memberMapped.getId()));
                memberMapped.addCollectivity(collectivity);
                memberList.add(memberMapped);
            }
            return memberList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Member> findRefereesByIdMember(String idMember) {
        List<Member> memberList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                select member.id, first_name, last_name, birth_date, gender, phone_number, email, address, profession, occupation,registration_fee_paid, membership_dues_paid
                from "member"
                    join member_referee on member.id = member_referee.member_referee_id
                where member_referee.member_refereed_id = ?
                """)) {
            preparedStatement.setString(1, idMember);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                memberList.add(memberMapper.mapFromResultSet(resultSet));
            }
            return memberList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CollectivityLocalStatistics> getMembersStatistics(String collectivityId, LocalDate start, LocalDate end, Double totalDue) {
        List<CollectivityLocalStatistics> stats = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("""
        select 
            m.id as member_id, 
            m.first_name, 
            m.last_name,
            m.email,
            m.occupation,
            coalesce(sum(t.amount), 0) as total_collected
        from member m
        left join "transaction" t on m.id = t.member_id 
            and t.creation_date between ? and ?
        where m.collectivity_id = ?
        group by m.id, m.first_name, m.last_name, m.email, m.occupation
        """)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            ps.setString(3, collectivityId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                stats.add(CollectivityLocalStatistics.builder()
                        .memberDescription(MemberDescription.builder()
                                .id(rs.getString("member_id"))
                                .firstName(rs.getString("first_name"))
                                .lastName(rs.getString("last_name"))
                                .email(rs.getString("email"))
                                .occupation(MemberOccupation.valueOf(rs.getString("occupation")))
                                .build())
                        .earnedAmount(rs.getDouble("total_collected"))
                        .unpaidAmount(totalDue - rs.getDouble("total_collected"))
                        .build());
            }
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CollectivityGlobalStatistics getGlobalStatsByCollectivity(
            String collectivityId, String collectivityName,
            LocalDate start, LocalDate end,
            List<edu.hei.school.agricultural.entity.MembershipFee> activeFees) {

        double totalDue = activeFees.stream().mapToDouble(f -> f.getAmount()).sum();
        double percentage = 0.0;
        int newMembersCount = 0;

        String sql = """
    WITH member_stats AS (
        SELECT m.id,
               m.joining_date,
               COALESCE(SUM(t.amount), 0) AS total_paid
        FROM member m
        LEFT JOIN "transaction" t ON m.id = t.member_id 
            AND t.creation_date BETWEEN ? AND ?
        WHERE m.collectivity_id = ?
        GROUP BY m.id, m.joining_date
    )
    SELECT 
        COUNT(id) AS total_members,
        COALESCE(SUM(CASE WHEN total_paid >= ? THEN 1 ELSE 0 END), 0) AS up_to_date_count,
        COALESCE(SUM(CASE WHEN joining_date BETWEEN ? AND ? THEN 1 ELSE 0 END), 0) AS new_members_count
    FROM member_stats
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            ps.setString(3, collectivityId);
            ps.setDouble(4, totalDue);
            ps.setDate(5, Date.valueOf(start));
            ps.setDate(6, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int totalMembers = rs.getInt("total_members");
                    int upToDateCount = rs.getInt("up_to_date_count");
                    newMembersCount = rs.getInt("new_members_count");

                    if (totalMembers > 0) {
                        percentage = (totalDue <= 0) ? 100.0 : (upToDateCount * 100.0) / totalMembers;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return CollectivityGlobalStatistics.builder()
                .collectivityInformation(CollectivityInformation.builder()
                        .id(String.valueOf((collectivityId)))
                        .name(collectivityName)
                        .build())
                .overallMemberCurrentDuePercentage(percentage)
                .newMembersNumber(newMembersCount)
                .build();
    }
}
