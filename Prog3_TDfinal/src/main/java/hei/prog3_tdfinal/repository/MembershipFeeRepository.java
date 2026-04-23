package hei.prog3_tdfinal.repository;

import hei.prog3_tdfinal.config.DBConnection;
import hei.prog3_tdfinal.entity.ActivityStatus;
import hei.prog3_tdfinal.entity.Frequency;
import hei.prog3_tdfinal.entity.MembershipFee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                    fee.setId((UUID) rs.getObject("id"));
                }
            }
        }
        return fee;
    }

    public List<MembershipFee> findByCollectivityId(UUID collectivityId) throws SQLException {
        String sql = "SELECT id, label, amount, eligible_from, frequency, status, collectivity_id " +
                     "FROM membership_fee WHERE collectivity_id = ?";

        List<MembershipFee> fees = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, collectivityId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MembershipFee fee = new MembershipFee();
                    fee.setId((UUID) rs.getObject("id"));
                    fee.setLabel(rs.getString("label"));
                    fee.setAmount(rs.getDouble("amount"));
                    fee.setEligibleFrom(rs.getDate("eligible_from").toLocalDate());
                    fee.setFrequency(Frequency.valueOf(rs.getString("frequency")));
                    fee.setStatus(ActivityStatus.valueOf(rs.getString("status")));
                    fee.setCollectivityId((UUID) rs.getObject("collectivity_id"));
                    fees.add(fee);
                }
            }
        }
        return fees;
    }
}