package ma.enset.ebankingbackend.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.enset.ebankingbackend.dtos.DashboardStatsDTO;
import ma.enset.ebankingbackend.entities.enums.OperationType;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import ma.enset.ebankingbackend.repositories.CustomerRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Statistics and KPIs")
@CrossOrigin("*")
public class DashboardController {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository operationRepository;

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics")
    public DashboardStatsDTO getStats() {
        return DashboardStatsDTO.builder()
                .totalCustomers(customerRepository.count())
                .totalAccounts(bankAccountRepository.count())
                .totalOperations(operationRepository.count())
                .totalDebitOperations(operationRepository.countByType(OperationType.DEBIT))
                .totalCreditOperations(operationRepository.countByType(OperationType.CREDIT))
                .totalDebitAmount(operationRepository.sumAmountByType(OperationType.DEBIT))
                .totalCreditAmount(operationRepository.sumAmountByType(OperationType.CREDIT))
                .currentAccountCount(bankAccountRepository.countByAccountType("CURR"))
                .savingAccountCount(bankAccountRepository.countByAccountType("SAVI"))
                .build();
    }
}
