package hei.prog3_tdfinal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class FinancialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Double amount;

    @Column(name = "collectivity_id", nullable = false)
    private String collectivityId;
}