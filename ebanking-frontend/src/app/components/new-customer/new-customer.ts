import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerService } from '../../services/customer';

@Component({
  selector: 'app-new-customer',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './new-customer.html',
  styleUrl: './new-customer.scss',
})
export class NewCustomerComponent {
  form: FormGroup;
  errorMessage = '';

  constructor(private fb: FormBuilder, private customerService: CustomerService, private router: Router) {
    this.form = this.fb.group({
      name:  ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  save(): void {
    if (this.form.invalid) return;
    this.customerService.saveCustomer(this.form.value).subscribe({
      next: () => this.router.navigate(['/customers']),
      error: () => this.errorMessage = 'Erreur lors de la création du client'
    });
  }
}
