package hei.prog3_tdfinal.repository;

import hei.prog3_tdfinal.config.DBConnection;
import hei.prog3_tdfinal.entity.CollectivityTransaction;
import hei.prog3_tdfinal.entity.PaymentMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CollectivityTransactionRepository {
    private final DBConnection dbConnection;

    public List<CollectivityTransaction> findByFilter(UUID collectivityId, LocalDateTime from, LocalDateTime to) throws SQLException {
        List<CollectivityTransaction> transactions = new ArrayList<>();
        String sql = "SELECT id, creation_date, amount, payment_mode, account_credited_id, member_debited_id, collectivity_id " +
                "FROM collectivity_transaction " +
                "WHERE collectivity_id = ? AND creation_date BETWEEN ? AND ? " +
                "ORDER BY creation_date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, collectivityId);
            ps.setTimestamp(2, Timestamp.valueOf(from));
            ps.setTimestamp(3, Timestamp.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }

    public void save(CollectivityTransaction transaction) throws SQLException {
        String sql = "INSERT INTO collectivity_transaction (amount, payment_mode, account_credited_id, member_debited_id, collectivity_id, creation_date) " +
                "VALUES (?, ?::payment_mode, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, transaction.getAmount());
            ps.setString(2, transaction.getPaymentMode().name());
            ps.setObject(3, transaction.getAccountCreditedId());
            ps.setObject(4, transaction.getMemberDebitedId());
            ps.setObject(5, transaction.getCollectivityId());
            ps.setTimestamp(6, Timestamp.valueOf(transaction.getCreationDate()));

            ps.executeUpdate();
        }
    }

    private CollectivityTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new CollectivityTransaction(
                (UUID) rs.getObject("id"),
                rs.getTimestamp("creation_date").toLocalDateTime(),
                rs.getDouble("amount"),
                PaymentMode.valueOf(rs.getString("payment_mode")),
                (UUID) rs.getObject("account_credited_id"),
                (UUID) rs.getObject("member_debited_id"),
                (UUID) rs.getObject("collectivity_id")
        );
    }
}