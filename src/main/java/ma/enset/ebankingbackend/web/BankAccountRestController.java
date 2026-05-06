package ma.enset.ebankingbackend.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.enset.ebankingbackend.dtos.*;
import ma.enset.ebankingbackend.exceptions.BalanceNotSufficientException;
import ma.enset.ebankingbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankingbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankingbackend.services.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
@Tag(name = "Bank Account Management", description = "APIs for managing bank accounts and operations")
@CrossOrigin("*")
public class BankAccountRestController {

    private final BankAccountService bankAccountService;

    // ── Accounts ───────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get all bank accounts")
    public List<BankAccountDTO> listAccounts() {
        return bankAccountService.listBankAccounts();
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get bank account by ID")
    public ResponseEntity<BankAccountDTO> getBankAccount(@PathVariable String accountId) {
        try {
            return ResponseEntity.ok(bankAccountService.getBankAccount(accountId));
        } catch (BankAccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all accounts for a specific customer")
    public List<BankAccountDTO> getCustomerAccounts(@PathVariable Long customerId) {
        return bankAccountService.getCustomerAccounts(customerId);
    }

    @PostMapping("/current")
    @Operation(summary = "Create a new current account")
    public ResponseEntity<?> saveCurrentAccount(
            @RequestParam double initialBalance,
            @RequestParam double overDraft,
            @RequestParam Long customerId) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bankAccountService.saveCurrentBankAccount(initialBalance, overDraft, customerId));
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/saving")
    @Operation(summary = "Create a new saving account")
    public ResponseEntity<?> saveSavingAccount(
            @RequestParam double initialBalance,
            @RequestParam double interestRate,
            @RequestParam Long customerId) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bankAccountService.saveSavingBankAccount(initialBalance, interestRate, customerId));
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ── Operations ─────────────────────────────────────────────────────────

    @PostMapping("/debit")
    @Operation(summary = "Debit an account")
    public ResponseEntity<?> debit(@Valid @RequestBody DebitDTO debitDTO) {
        try {
            bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
            return ResponseEntity.ok("Debit operation completed successfully");
        } catch (BankAccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BalanceNotSufficientException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/credit")
    @Operation(summary = "Credit an account")
    public ResponseEntity<?> credit(@Valid @RequestBody CreditDTO creditDTO) {
        try {
            bankAccountService.credit(creditDTO.getAccountId(), creditDTO.getAmount(), creditDTO.getDescription());
            return ResponseEntity.ok("Credit operation completed successfully");
        } catch (BankAccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer between two accounts")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequestDTO transferDTO) {
        try {
            bankAccountService.transfer(
                    transferDTO.getAccountSource(),
                    transferDTO.getAccountDestination(),
                    transferDTO.getAmount());
            return ResponseEntity.ok("Transfer completed successfully");
        } catch (BankAccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BalanceNotSufficientException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ── History ────────────────────────────────────────────────────────────

    @GetMapping("/{accountId}/operations")
    @Operation(summary = "Get all operations for an account")
    public ResponseEntity<?> getAccountOperations(@PathVariable String accountId) {
        return ResponseEntity.ok(bankAccountService.accountHistory(accountId));
    }

    @GetMapping("/{accountId}/pageOperations")
    @Operation(summary = "Get paginated operation history for an account")
    public ResponseEntity<?> getAccountHistory(
            @PathVariable String accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            return ResponseEntity.ok(bankAccountService.getAccountHistory(accountId, page, size));
        } catch (BankAccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
