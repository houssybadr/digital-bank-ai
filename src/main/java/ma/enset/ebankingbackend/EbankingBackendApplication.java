package ma.enset.ebankingbackend;

import ma.enset.ebankingbackend.dtos.CustomerDTO;
import ma.enset.ebankingbackend.entities.AppUser;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.repositories.AppUserRepository;
import ma.enset.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
@EnableScheduling
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService,
                                       AppUserRepository userRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            // ── Créer les utilisateurs ────────────────────────────────────
            if (!userRepository.existsByUsername("admin")) {
                userRepository.save(AppUser.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .roles(List.of("ADMIN", "USER"))
                        .enabled(true).build());
            }
            if (!userRepository.existsByUsername("user")) {
                userRepository.save(AppUser.builder()
                        .username("user")
                        .password(passwordEncoder.encode("user123"))
                        .roles(List.of("USER"))
                        .enabled(true).build());
            }

            // ── Créer des clients ─────────────────────────────────────────
            Stream.of("Hassan", "Youssef", "Aicha").forEach(name -> {
                CustomerDTO dto = new CustomerDTO();
                dto.setName(name);
                dto.setEmail(name.toLowerCase() + "@gmail.com");
                bankAccountService.saveCustomer(dto);
            });

            // ── Créer des comptes pour chaque client ──────────────────────
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

            // ── Créer des opérations ──────────────────────────────────────
            bankAccountService.listBankAccounts().forEach(account -> {
                try {
                    for (int i = 0; i < 5; i++) {
                        bankAccountService.credit(account.getId(),
                                Math.random() * 12000 + 1000, "Virement entrant " + i);
                        bankAccountService.debit(account.getId(),
                                Math.random() * 3000 + 100, "Retrait " + i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            System.out.println("==============================================");
            System.out.println(" Digital Banking App démarrée avec succès");
            System.out.println(" Swagger UI : http://localhost:8085/swagger-ui.html");
            System.out.println(" H2 Console : http://localhost:8085/h2-console");
            System.out.println(" Login admin : admin / admin123");
            System.out.println(" Login user  : user  / user123");
            System.out.println("==============================================");
        };
    }
}
