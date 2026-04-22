package hei.prog3_tdfinal.repository;

import hei.prog3_tdfinal.config.DBConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FinancialAccountRepository {
    private final DBConnection dbConnection;


    public void updateAmount(UUID accountId, Double amountToAdd) throws SQLException {
        String sql = "UPDATE financial_account SET amount = amount + ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, amountToAdd);
            ps.setObject(2, accountId);
            ps.executeUpdate();
        }
    }
}