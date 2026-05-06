import { Routes } from '@angular/router';
import { CustomersComponent } from './components/customers/customers';
import { AccountOperationsComponent } from './components/account-operations/account-operations';
import { NewCustomerComponent } from './components/new-customer/new-customer';
import { EditCustomerComponent } from './components/edit-customer/edit-customer';
import { CustomerAccountsComponent } from './components/customer-accounts/customer-accounts';
import { AccountsComponent } from './components/accounts/accounts';

export const routes: Routes = [
  { path: '', redirectTo: 'customers', pathMatch: 'full' },
  { path: 'customers', component: CustomersComponent },
  { path: 'customers/new', component: NewCustomerComponent },
  { path: 'customers/edit/:id', component: EditCustomerComponent },
  { path: 'customers/:id/accounts', component: CustomerAccountsComponent },
  { path: 'accounts', component: AccountsComponent },
  { path: 'accounts/:id/operations', component: AccountOperationsComponent },
];
