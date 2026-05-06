package ma.enset.ebankingbackend.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDTO {
    private long totalCustomers;
    private long totalAccounts;
    private long totalOperations;
    private long totalDebitOperations;
    private long totalCreditOperations;
    private double totalDebitAmount;
    private double totalCreditAmount;
    private long currentAccountCount;
    private long savingAccountCount;
}
