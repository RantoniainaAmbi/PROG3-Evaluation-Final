package hei.prog3_tdfinal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipFee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String label;
    private Double amount;
    private LocalDate eligibleFrom;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @Enumerated(EnumType.STRING)
    private ActivityStatus status;

    private String collectivityId;
}
