package hei.prog3_tdfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialAccountResponse {
    private String id;
    private String accountType; 
    private Double balance;
    private String currency;

    private String holderName;
    private String bankName;
    private String rib;

    private String mobileBankingService;
    private String mobileNumber;
}