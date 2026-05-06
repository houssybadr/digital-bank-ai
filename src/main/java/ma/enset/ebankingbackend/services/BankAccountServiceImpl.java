package ma.enset.ebankingbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankingbackend.dtos.*;
import ma.enset.ebankingbackend.entities.*;
import ma.enset.ebankingbackend.entities.enums.AccountStatus;
import ma.enset.ebankingbackend.entities.enums.OperationType;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficientException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.mappers.BankAccountMapperImpl;
import ma.enset.ebankingbackend.repositories.AccountOperationRepository;
import ma.enset.ebankingbackend.repositories.BankAccountRepository;
import ma.enset.ebankingbackend.repositories.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository operationRepository;
    private final BankAccountMapperImpl mapper;

    // ── Customers ──────────────────────────────────────────────────────────

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving customer: {}", customerDTO.getName());
        Customer customer = mapper.fromCustomerDTO(customerDTO);
        Customer saved = customerRepository.save(customer);
        return mapper.fromCustomer(saved);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) throws CustomerNotFoundException {
        log.info("Updating customer id: {}", customerDTO.getId());
        customerRepository.findById(customerDTO.getId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerDTO.getId()));
        Customer customer = mapper.fromCustomerDTO(customerDTO);
        Customer saved = customerRepository.save(customer);
        return mapper.fromCustomer(saved);
    }

    @Override
    public void deleteCustomer(Long customerId) throws CustomerNotFoundException {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        customerRepository.deleteById(customerId);
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
        return mapper.fromCustomer(customer);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll()
                .stream().map(mapper::fromCustomer).collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        return customerRepository.searchCustomers("%" + keyword + "%")
                .stream().map(mapper::fromCustomer).collect(Collectors.toList());
    }

    // ── Bank Accounts ──────────────────────────────────────────────────────

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        CurrentAccount account = new CurrentAccount();
        account.setId(UUID.randomUUID().toString());
        account.setBalance(initialBalance);
        account.setCreatedAt(new Date());
        account.setStatus(AccountStatus.CREATED);
        account.setCurrency("MAD");
        account.setCustomer(customer);
        account.setOverDraft(overDraft);

        CurrentAccount saved = bankAccountRepository.save(account);
        log.info("Current account created: {}", saved.getId());
        return mapper.fromCurrentBankAccount(saved);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));

        SavingAccount account = new SavingAccount();
        account.setId(UUID.randomUUID().toString());
        account.setBalance(initialBalance);
        account.setCreatedAt(new Date());
        account.setStatus(AccountStatus.CREATED);
        account.setCurrency("MAD");
        account.setCustomer(customer);
        account.setInterestRate(interestRate);

        SavingAccount saved = bankAccountRepository.save(account);
        log.info("Saving account created: {}", saved.getId());
        return mapper.fromSavingBankAccount(saved);
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + accountId));
        if (account instanceof CurrentAccount ca) return mapper.fromCurrentBankAccount(ca);
        return mapper.fromSavingBankAccount((SavingAccount) account);
    }

    @Override
    public List<BankAccountDTO> listBankAccounts() {
        return bankAccountRepository.findAll().stream().map(account -> {
            if (account instanceof CurrentAccount ca) return (BankAccountDTO) mapper.fromCurrentBankAccount(ca);
            return (BankAccountDTO) mapper.fromSavingBankAccount((SavingAccount) account);
        }).collect(Collectors.toList());
    }

    @Override
    public List<BankAccountDTO> getCustomerAccounts(Long customerId) {
        return bankAccountRepository.findByCustomerId(customerId).stream().map(account -> {
            if (account instanceof CurrentAccount ca) return (BankAccountDTO) mapper.fromCurrentBankAccount(ca);
            return (BankAccountDTO) mapper.fromSavingBankAccount((SavingAccount) account);
        }).collect(Collectors.toList());
    }

    // ── Operations ─────────────────────────────────────────────────────────

    @Override
    public void debit(String accountId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + accountId));

        double allowedBalance = account.getBalance();
        if (account instanceof CurrentAccount ca) allowedBalance += ca.getOverDraft();

        if (allowedBalance < amount)
            throw new BalanceNotSufficientException("Insufficient balance for account: " + accountId);

        AccountOperation operation = AccountOperation.builder()
                .operationDate(new Date())
                .amount(amount)
                .type(OperationType.DEBIT)
                .description(description)
                .bankAccount(account)
                .build();
        operationRepository.save(operation);
        account.setBalance(account.getBalance() - amount);
        bankAccountRepository.save(account);
    }

    @Override
    public void credit(String accountId, double amount, String description)
            throws BankAccountNotFoundException {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + accountId));

        AccountOperation operation = AccountOperation.builder()
                .operationDate(new Date())
                .amount(amount)
                .type(OperationType.CREDIT)
                .description(description)
                .bankAccount(account)
                .build();
        operationRepository.save(operation);
        account.setBalance(account.getBalance() + amount);
        bankAccountRepository.save(account);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource, amount, "Transfer to " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfer from " + accountIdSource);
    }

    // ── History ────────────────────────────────────────────────────────────

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) {
        return operationRepository.findByBankAccountId(accountId)
                .stream().map(mapper::fromAccountOperation).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size)
            throws BankAccountNotFoundException {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found: " + accountId));

        Page<AccountOperation> accountOperations = operationRepository
                .findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));

        AccountHistoryDTO dto = new AccountHistoryDTO();
        dto.setAccountId(accountId);
        dto.setBalance(account.getBalance());
        dto.setCurrentPage(page);
        dto.setPageSize(size);
        dto.setTotalPages(accountOperations.getTotalPages());
        dto.setAccountOperationDTOs(
                accountOperations.getContent().stream()
                        .map(mapper::fromAccountOperation)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
