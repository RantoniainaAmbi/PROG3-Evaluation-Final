package hei.prog3_tdfinal.entity;

import hei.prog3_tdfinal.entity.PaymentMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Column(nullable = false)
    private String accountCreditedId;

    @Column(nullable = false)
    private String memberDebitedId;

    @Column(nullable = false)
    private String collectivityId;
}