import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BankAccountService } from '../../services/bank-account';
import { AccountHistory, BankAccount } from '../../models/bank-account.model';

@Component({
  selector: 'app-account-operations',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './account-operations.html',
  styleUrl: './account-operations.scss',
})
export class AccountOperationsComponent implements OnInit {
  accountId!: string;
  account!: BankAccount;
  accountHistory!: AccountHistory;
  currentPage = 0;
  pageSize = 5;
  errorMessage = '';
  successMessage = '';
  operationForm!: FormGroup;
  transferForm!: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private bankAccountService: BankAccountService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.accountId = this.route.snapshot.paramMap.get('id')!;
    this.operationForm = this.fb.group({
      operationType: ['DEBIT', Validators.required],
      amount: [0, [Validators.required, Validators.min(1)]],
      description: ['']
    });
    this.transferForm = this.fb.group({
      accountDestination: ['', Validators.required],
      amount: [0, [Validators.required, Validators.min(1)]],
      description: ['']
    });
    this.loadAccount();
    this.loadHistory();
  }

  loadAccount(): void {
    this.bankAccountService.getAccount(this.accountId).subscribe({
      next: data => this.account = data,
      error: () => this.errorMessage = 'Compte introuvable'
    });
  }

  loadHistory(): void {
    this.bankAccountService.getAccountHistory(this.accountId, this.currentPage, this.pageSize).subscribe({
      next: data => this.accountHistory = data,
      error: () => this.errorMessage = 'Erreur chargement historique'
    });
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadHistory();
  }

  executeOperation(): void {
    if (this.operationForm.invalid) return;
    this.errorMessage = ''; this.successMessage = '';
    const { operationType, amount, description } = this.operationForm.value;
    const op$ = operationType === 'DEBIT'
      ? this.bankAccountService.debit(this.accountId, amount, description)
      : this.bankAccountService.credit(this.accountId, amount, description);
    op$.subscribe({
      next: () => {
        this.successMessage = `${operationType} effectué avec succès`;
        this.loadAccount();
        this.loadHistory();
        this.operationForm.reset({ operationType: 'DEBIT', amount: 0, description: '' });
      },
      error: err => this.errorMessage = err.error?.error || 'Erreur lors de l\'opération'
    });
  }

  executeTransfer(): void {
    if (this.transferForm.invalid) return;
    this.errorMessage = ''; this.successMessage = '';
    const { accountDestination, amount, description } = this.transferForm.value;
    this.bankAccountService.transfer(this.accountId, accountDestination, amount, description).subscribe({
      next: () => {
        this.successMessage = 'Virement effectué avec succès';
        this.loadAccount();
        this.loadHistory();
        this.transferForm.reset({ accountDestination: '', amount: 0, description: '' });
      },
      error: err => this.errorMessage = err.error?.error || 'Erreur lors du virement'
    });
  }

  get pages(): number[] {
    return Array.from({ length: this.accountHistory?.totalPages || 0 }, (_, i) => i);
  }
}
