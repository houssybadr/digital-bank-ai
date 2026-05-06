import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { CustomersComponent } from './components/customers/customers';
import { AccountOperationsComponent } from './components/account-operations/account-operations';
import { NewCustomerComponent } from './components/new-customer/new-customer';
import { EditCustomerComponent } from './components/edit-customer/edit-customer';
import { CustomerAccountsComponent } from './components/customer-accounts/customer-accounts';
import { AccountsComponent } from './components/accounts/accounts';
import { LoginComponent } from './components/login/login';
import { DashboardComponent } from './components/dashboard/dashboard';
import { ChatbotComponent } from './components/chatbot/chatbot';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard',             component: DashboardComponent,          canActivate: [authGuard] },
  { path: 'customers',             component: CustomersComponent,           canActivate: [authGuard] },
  { path: 'customers/new',         component: NewCustomerComponent,         canActivate: [authGuard] },
  { path: 'customers/edit/:id',    component: EditCustomerComponent,        canActivate: [authGuard] },
  { path: 'customers/:id/accounts',component: CustomerAccountsComponent,    canActivate: [authGuard] },
  { path: 'accounts',              component: AccountsComponent,            canActivate: [authGuard] },
  { path: 'accounts/:id/operations', component: AccountOperationsComponent, canActivate: [authGuard] },
  { path: 'chatbot',               component: ChatbotComponent,             canActivate: [authGuard] },
  { path: '**', redirectTo: 'dashboard' }
];
