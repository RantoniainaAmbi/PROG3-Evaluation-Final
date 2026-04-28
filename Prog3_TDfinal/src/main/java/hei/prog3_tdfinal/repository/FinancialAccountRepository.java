package hei.prog3_tdfinal.repository;

import hei.prog3_tdfinal.config.DBConnection;
import hei.prog3_tdfinal.dto.FinancialAccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FinancialAccountRepository {

    private final DBConnection dbConnection;

    public void updateAmount(String accountId, Double amountToAdd) throws SQLException {
        String sql = "UPDATE financial_account SET amount = amount + ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amountToAdd);
            ps.setObject(2, accountId);
            ps.executeUpdate();
        }
    }

    
    public List<FinancialAccountResponse> findByCollectivityId(String collectivityId, LocalDate at)
            throws SQLException {

        List<FinancialAccountResponse> results = new ArrayList<>();

        String sql = """
                SELECT
                    fa.id,
                    fa.amount                    AS current_amount,
                    CASE
                        WHEN ca.id IS NOT NULL THEN 'CASH'
                        WHEN ba.id IS NOT NULL THEN 'BANK'
                        WHEN mba.id IS NOT NULL THEN 'MOBILE_BANKING'
                    END                          AS account_type,
                    ba.holder_name               AS bank_holder,
                    ba.bank,
                    ba.rib_number,
                    mba.holder_name              AS mobile_holder,
                    mba.mobile_service,
                    mba.mobile_number
                FROM financial_account fa
                LEFT JOIN cash_account ca ON ca.id = fa.id
                LEFT JOIN bank_account ba ON ba.id = fa.id
                LEFT JOIN mobile_banking_account mba ON mba.id = fa.id
                WHERE fa.collectivity_id = ?
                """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, collectivityId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String accountId = rs.getString("id");
                    String accountType = rs.getString("account_type");

                    double balance;
                    if (at == null) {
                        balance = rs.getDouble("current_amount");
                    } else {
                        balance = computeBalanceAt(conn, accountId, at);
                    }

                    FinancialAccountResponse.FinancialAccountResponseBuilder builder =
                            FinancialAccountResponse.builder()
                                    .id(accountId)
                                    .accountType(accountType)
                                    .balance(balance)
                                    .currency("MGA");

                    if ("BANK".equals(accountType)) {
                        builder.holderName(rs.getString("bank_holder"))
                               .bankName(rs.getString("bank"))
                               .rib(rs.getString("rib_number"));
                    } else if ("MOBILE_BANKING".equals(accountType)) {
                        builder.holderName(rs.getString("mobile_holder"))
                               .mobileBankingService(rs.getString("mobile_service"))
                               .mobileNumber(rs.getString("mobile_number"));
                    }

                    results.add(builder.build());
                }
            }
        }
        return results;
    }

   
    private double computeBalanceAt(Connection conn, String accountId, LocalDate at)
            throws SQLException {
        String sql = """
                SELECT COALESCE(SUM(ct.amount), 0)
                FROM collectivity_transaction ct
                WHERE ct.account_credited_id = ?
                  AND DATE(ct.creation_date) <= ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, accountId);
            ps.setDate(2, Date.valueOf(at));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }
}