package hei.prog3_tdfinal.repository;

import hei.prog3_tdfinal.config.DBConnection;
import hei.prog3_tdfinal.dto.MemberStatisticsDto;
import hei.prog3_tdfinal.entity.ActivityStatus;
import hei.prog3_tdfinal.entity.Frequency;
import hei.prog3_tdfinal.entity.MembershipFee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MembershipFeeRepository {

    private final DBConnection dbConnection;

    public MembershipFee save(MembershipFee fee) throws SQLException {
        String sql = "INSERT INTO membership_fee (id, label, amount, eligible_from, frequency, status, collectivity_id) " +
                     "VALUES (gen_random_uuid(), ?, ?, ?, ?::frequency, ?::activity_status, ?) RETURNING id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fee.getLabel());
            ps.setDouble(2, fee.getAmount());
            ps.setDate(3, Date.valueOf(fee.getEligibleFrom()));
            ps.setString(4, fee.getFrequency().name());
            ps.setString(5, fee.getStatus() != null ? fee.getStatus().name() : ActivityStatus.ACTIVE.name());
            ps.setObject(6, fee.getCollectivityId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    fee.setId(rs.getString("id"));
                }
            }
        }
        return fee;
    }

    public List<MembershipFee> findByCollectivityId(String collectivityId) throws SQLException {
        String sql = "SELECT id, label, amount, eligible_from, frequency, status, collectivity_id " +
                     "FROM membership_fee WHERE collectivity_id = ?";

        List<MembershipFee> fees = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, collectivityId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MembershipFee fee = new MembershipFee();
                    fee.setId(rs.getString("id"));
                    fee.setLabel(rs.getString("label"));
                    fee.setAmount(rs.getDouble("amount"));
                    fee.setEligibleFrom(rs.getDate("eligible_from").toLocalDate());
                    fee.setFrequency(Frequency.valueOf(rs.getString("frequency")));
                    fee.setStatus(ActivityStatus.valueOf(rs.getString("status")));
                    fee.setCollectivityId(rs.getString("collectivity_id"));
                    fees.add(fee);
                }
            }
        }
        return fees;
    }

    public List<MemberStatisticsDto> getMemberStatistics(String collectivityId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sqlWithCollectivityId = "SELECT m.id, m.first_name, m.last_name, COALESCE(SUM(ct.amount), 0) as total_collected FROM member m " +
                "LEFT JOIN collectivity_transaction ct ON m.id = ct.member_debited_id AND ct.creation_date >= ? AND ct.creation_date <= ? " +
                "WHERE m.collectivity_id = ? " +
                "GROUP BY m.id, m.first_name, m.last_name";
        String sqlWithCollectivityNumber = "SELECT m.id, m.first_name, m.last_name, COALESCE(SUM(ct.amount), 0) as total_collected FROM member m " +
                "JOIN collectivity c ON c.number = substring(m.id from '^C([0-9]+)-') " +
                "LEFT JOIN collectivity_transaction ct ON m.id = ct.member_debited_id AND ct.creation_date >= ? AND ct.creation_date <= ? " +
                "WHERE c.id = ? " +
                "GROUP BY m.id, m.first_name, m.last_name";

        List<MemberStatisticsDto> statistics = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection()) {
            boolean hasCollectivityId = hasMemberCollectivityIdColumn(conn);
            try (PreparedStatement ps = conn.prepareStatement(hasCollectivityId ? sqlWithCollectivityId : sqlWithCollectivityNumber)) {
                ps.setDate(1, Date.valueOf(startDate));
                ps.setDate(2, Date.valueOf(endDate));
                ps.setObject(3, collectivityId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        MemberStatisticsDto stat = MemberStatisticsDto.builder()
                                .memberId(rs.getString("id"))
                                .firstName(rs.getString("first_name"))
                                .lastName(rs.getString("last_name"))
                                .totalCollected(rs.getDouble("total_collected"))
                                .attendanceRate(0.0)
                                .outstandingAmount(0.0)
                                .build();
                        statistics.add(stat);
                    }
                }
            }
        }
        return statistics;
    }

    private boolean hasMemberCollectivityIdColumn(Connection conn) throws SQLException {
        DatabaseMetaData metadata = conn.getMetaData();
        try (ResultSet rs = metadata.getColumns(null, null, "member", "collectivity_id")) {
            return rs.next();
        }
    }
}