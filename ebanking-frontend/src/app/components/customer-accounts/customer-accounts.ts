import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BankAccountService } from '../../services/bank-account';
import { CustomerService } from '../../services/customer';
import { BankAccount } from '../../models/bank-account.model';
import { Customer } from '../../models/customer.model';

@Component({
  selector: 'app-customer-accounts',
  imports: [CommonModule, RouterLink],
  templateUrl: './customer-accounts.html',
  styleUrl: './customer-accounts.scss',
})
export class CustomerAccountsComponent implements OnInit {
  accounts: BankAccount[] = [];
  customer!: Customer;
  customerId!: number;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private bankAccountService: BankAccountService,
    private customerService: CustomerService
  ) {}

  ngOnInit(): void {
    this.customerId = +this.route.snapshot.paramMap.get('id')!;
    this.customerService.getCustomer(this.customerId).subscribe({
      next: c => this.customer = c,
      error: () => this.errorMessage = 'Client introuvable'
    });
    this.bankAccountService.getCustomerAccounts(this.customerId).subscribe({
      next: data => this.accounts = data,
      error: () => this.errorMessage = 'Erreur lors du chargement des comptes'
    });
  }

  viewOperations(accountId: string): void {
    this.router.navigate(['/accounts', accountId, 'operations']);
  }
}
