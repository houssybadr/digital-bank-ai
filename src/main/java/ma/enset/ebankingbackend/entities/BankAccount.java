package ma.enset.ebankingbackend.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.enset.ebankingbackend.entities.enums.AccountStatus;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bank_accounts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", length = 4)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    @Id
    private String id;

    private double balance;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private String currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccountOperation> accountOperations;
}
