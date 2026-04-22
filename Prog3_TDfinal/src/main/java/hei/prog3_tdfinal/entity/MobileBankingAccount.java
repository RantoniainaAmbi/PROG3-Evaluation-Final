package hei.prog3_tdfinal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class MobileBankingAccount extends FinancialAccount {
    private String holderName;

    @Enumerated(EnumType.STRING)
    private MobileBankingService mobileBankingService;

    private String mobileNumber;
}
