package ma.enset.ebankingbackend;

import ma.enset.ebankingbackend.entities.*;
import ma.enset.ebankingbackend.entities.enums.AccountStatus;
import ma.enset.ebankingbackend.entities.enums.OperationType;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import ma.enset.ebankingbackend.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(
            CustomerRepository customerRepository,
            BankAccountRepository bankAccountRepository,
            AccountOperationRepository operationRepository) {
        return args -> {
            // ── 1. Créer des clients ──────────────────────────────────────────
            Stream.of("Hassan", "Youssef", "Aicha").forEach(name -> {
                Customer customer = Customer.builder()
                        .name(name)
                        .email(name.toLowerCase() + "@gmail.com")
                        .build();
                customerRepository.save(customer);
            });

            // ── 2. Créer des comptes pour chaque client ───────────────────────
            customerRepository.findAll().forEach(customer -> {

                // Compte courant
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random() * 90000 + 10000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.ACTIVATED);
                currentAccount.setCurrency("MAD");
                currentAccount.setCustomer(customer);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

                // Compte épargne
                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random() * 50000 + 5000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.ACTIVATED);
                savingAccount.setCurrency("MAD");
                savingAccount.setCustomer(customer);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);
            });

            // ── 3. Créer des opérations pour chaque compte ───────────────────
            bankAccountRepository.findAll().forEach(account -> {
                for (int i = 0; i < 10; i++) {
                    AccountOperation operation = AccountOperation.builder()
                            .operationDate(new Date())
                            .amount(Math.random() * 12000 + 1000)
                            .type(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT)
                            .description("Operation " + i)
                            .bankAccount(account)
                            .build();
                    operationRepository.save(operation);
                }
            });

            // ── 4. Afficher les résultats ─────────────────────────────────────
            System.out.println("========== TEST DAO ==========");

            List<Customer> customers = customerRepository.findAll();
            System.out.println("Nombre de clients : " + customers.size());
            customers.forEach(c -> System.out.println("  >> " + c.getId() + " | " + c.getName() + " | " + c.getEmail()));

            System.out.println("\nNombre de comptes : " + bankAccountRepository.count());
            bankAccountRepository.findAll().forEach(acc -> {
                if (acc instanceof CurrentAccount ca) {
                    System.out.println("  [COURANT]  " + ca.getId() + " | Solde: " + ca.getBalance() + " | OverDraft: " + ca.getOverDraft());
                } else if (acc instanceof SavingAccount sa) {
                    System.out.println("  [EPARGNE]  " + sa.getId() + " | Solde: " + sa.getBalance() + " | Taux: " + sa.getInterestRate());
                }
            });

            System.out.println("\nNombre d'opérations : " + operationRepository.count());
            System.out.println("==============================");

            // ── 5. Test recherche ─────────────────────────────────────────────
            System.out.println("\nRecherche client 'has' :");
            customerRepository.findByNameContainsIgnoreCase("has")
                    .forEach(c -> System.out.println("  >> " + c.getName()));

            System.out.println("\nOpérations du 1er compte (page 0, size 5) :");
            String firstAccountId = bankAccountRepository.findAll().get(0).getId();
            operationRepository
                    .findByBankAccountIdOrderByOperationDateDesc(firstAccountId,
                            org.springframework.data.domain.PageRequest.of(0, 5))
                    .getContent()
                    .forEach(op -> System.out.println("  >> " + op.getType() + " | " + op.getAmount()));
        };
    }
}
