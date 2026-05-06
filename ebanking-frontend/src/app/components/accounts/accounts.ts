import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BankAccountService } from '../../services/bank-account';
import { BankAccount } from '../../models/bank-account.model';

@Component({
  selector: 'app-accounts',
  imports: [CommonModule, FormsModule],
  templateUrl: './accounts.html',
  styleUrl: './accounts.scss',
})
export class AccountsComponent implements OnInit {
  accounts: BankAccount[] = [];
  errorMessage = '';

  constructor(private bankAccountService: BankAccountService, private router: Router) {}

  ngOnInit(): void {
    this.bankAccountService.getAccounts().subscribe({
      next: data => this.accounts = data,
      error: () => this.errorMessage = 'Erreur lors du chargement des comptes'
    });
  }

  viewOperations(accountId: string): void {
    this.router.navigate(['/accounts', accountId, 'operations']);
  }
}
