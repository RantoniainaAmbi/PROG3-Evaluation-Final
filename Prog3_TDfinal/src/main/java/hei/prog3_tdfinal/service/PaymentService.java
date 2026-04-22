package hei.prog3_tdfinal.service;

import hei.prog3_tdfinal.entity.CollectivityTransaction;
import hei.prog3_tdfinal.repository.CollectivityTransactionRepository;
import hei.prog3_tdfinal.repository.FinancialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CollectivityTransactionRepository transactionRepository;
    private final FinancialAccountRepository accountRepository;


    public void processPayment(CollectivityTransaction transaction) throws SQLException {
        transactionRepository.save(transaction);

        accountRepository.updateAmount(transaction.getAccountCreditedId(), transaction.getAmount());
    }


    public List<CollectivityTransaction> getTransactionHistory(UUID collectivityId, LocalDateTime from, LocalDateTime to) throws SQLException {
        return transactionRepository.findByFilter(collectivityId, from, to);
    }
}