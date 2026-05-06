import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CustomerService } from '../../services/customer';

@Component({
  selector: 'app-edit-customer',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './edit-customer.html',
  styleUrl: './edit-customer.scss',
})
export class EditCustomerComponent implements OnInit {
  form: FormGroup;
  customerId!: number;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      name:  ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.customerId = +this.route.snapshot.paramMap.get('id')!;
    this.customerService.getCustomer(this.customerId).subscribe({
      next: c => this.form.patchValue({ name: c.name, email: c.email }),
      error: () => this.errorMessage = 'Client introuvable'
    });
  }

  save(): void {
    if (this.form.invalid) return;
    this.customerService.updateCustomer({ id: this.customerId, ...this.form.value }).subscribe({
      next: () => this.router.navigate(['/customers']),
      error: () => this.errorMessage = 'Erreur lors de la mise à jour'
    });
  }
}
