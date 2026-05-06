import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerService } from '../../services/customer';
import { Customer } from '../../models/customer.model';

@Component({
  selector: 'app-customers',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './customers.html',
  styleUrl: './customers.scss',
})
export class CustomersComponent implements OnInit {
  customers: Customer[] = [];
  keyword = '';
  errorMessage = '';

  constructor(private customerService: CustomerService, private router: Router) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.customerService.getCustomers().subscribe({
      next: data => this.customers = data,
      error: () => this.errorMessage = 'Erreur lors du chargement des clients'
    });
  }

  searchCustomers(): void {
    if (!this.keyword.trim()) { this.loadCustomers(); return; }
    this.customerService.searchCustomers(this.keyword).subscribe({
      next: data => this.customers = data,
      error: () => this.errorMessage = 'Erreur lors de la recherche'
    });
  }

  deleteCustomer(id: number): void {
    if (!confirm('Confirmer la suppression ?')) return;
    this.customerService.deleteCustomer(id).subscribe({
      next: () => this.customers = this.customers.filter(c => c.id !== id),
      error: () => this.errorMessage = 'Erreur lors de la suppression'
    });
  }

  viewAccounts(id: number): void {
    this.router.navigate(['/customers', id, 'accounts']);
  }
}
