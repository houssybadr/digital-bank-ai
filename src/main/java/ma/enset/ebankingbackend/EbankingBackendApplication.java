package ma.enset.ebankingbackend;

import ma.enset.ebankingbackend.dtos.CustomerDTO;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
        return args -> {
            // ── Créer des clients via le service ─────────────────────────────
            Stream.of("Hassan", "Youssef", "Aicha").forEach(name -> {
                CustomerDTO dto = new CustomerDTO();
                dto.setName(name);
                dto.setEmail(name.toLowerCase() + "@gmail.com");
                bankAccountService.saveCustomer(dto);
            });

            // ── Créer des comptes pour chaque client ─────────────────────────
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(
                            Math.random() * 90000 + 10000, 9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(
                            Math.random() * 50000 + 5000, 5.5, customer.getId());
                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });

            // ── Effectuer des opérations sur chaque compte ───────────────────
            bankAccountService.listBankAccounts().forEach(account -> {
                try {
                    for (int i = 0; i < 5; i++) {
                        bankAccountService.credit(account.getId(),
                                Math.random() * 12000 + 1000, "Credit " + i);
                        bankAccountService.debit(account.getId(),
                                Math.random() * 3000 + 100, "Debit " + i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        };
    }
}
