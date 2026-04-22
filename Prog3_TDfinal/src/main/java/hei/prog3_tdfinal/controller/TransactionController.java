package hei.prog3_tdfinal.controller;

import hei.prog3_tdfinal.entity.CollectivityTransaction;
import hei.prog3_tdfinal.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final PaymentService paymentService;


    @GetMapping("/collectivities/{id}/transactions")
    public List<CollectivityTransaction> getTransactions(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) throws SQLException {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        return paymentService.getTransactionHistory(id, start, end);
    }


    @PostMapping("/members/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPayments(
            @PathVariable UUID id,
            @RequestBody List<CollectivityTransaction> transactions
    ) throws SQLException {
        for (CollectivityTransaction transaction : transactions) {
            transaction.setMemberDebitedId(id);
            if (transaction.getCreationDate() == null) {
                transaction.setCreationDate(LocalDateTime.now());
            }
            paymentService.processPayment(transaction);
        }
    }
}