package ma.enset.ebankingbackend.services;

import ma.enset.ebankingbackend.dtos.*;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficientException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {

    // ── Customers ──────────────────────────────────────────────────────────
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CustomerDTO updateCustomer(CustomerDTO customerDTO) throws CustomerNotFoundException;
    void deleteCustomer(Long customerId) throws CustomerNotFoundException;
    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> listCustomers();
    List<CustomerDTO> searchCustomers(String keyword);

    // ── Bank Accounts ──────────────────────────────────────────────────────
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException;
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    List<BankAccountDTO> listBankAccounts();
    List<BankAccountDTO> getCustomerAccounts(Long customerId);

    // ── Operations ─────────────────────────────────────────────────────────
    void debit(String accountId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description)
            throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount)
            throws BankAccountNotFoundException, BalanceNotSufficientException;

    // ── History ────────────────────────────────────────────────────────────
    List<AccountOperationDTO> accountHistory(String accountId);
    AccountHistoryDTO getAccountHistory(String accountId, int page, int size)
            throws BankAccountNotFoundException;
}
