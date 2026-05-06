import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AccountHistory, BankAccount } from '../models/bank-account.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BankAccountService {
  private apiUrl = `${environment.backendHost}/api/accounts`;

  constructor(private http: HttpClient) {}

  getAccounts(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(this.apiUrl);
  }

  getAccount(id: string): Observable<BankAccount> {
    return this.http.get<BankAccount>(`${this.apiUrl}/${id}`);
  }

  getCustomerAccounts(customerId: number): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(`${this.apiUrl}/customer/${customerId}`);
  }

  getAccountHistory(accountId: string, page: number, size: number): Observable<AccountHistory> {
    return this.http.get<AccountHistory>(
      `${this.apiUrl}/${accountId}/pageOperations?page=${page}&size=${size}`
    );
  }

  saveCurrentAccount(initialBalance: number, overDraft: number, customerId: number): Observable<BankAccount> {
    return this.http.post<BankAccount>(
      `${this.apiUrl}/current?initialBalance=${initialBalance}&overDraft=${overDraft}&customerId=${customerId}`, {}
    );
  }

  saveSavingAccount(initialBalance: number, interestRate: number, customerId: number): Observable<BankAccount> {
    return this.http.post<BankAccount>(
      `${this.apiUrl}/saving?initialBalance=${initialBalance}&interestRate=${interestRate}&customerId=${customerId}`, {}
    );
  }

  debit(accountId: string, amount: number, description: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/debit`, { accountId, amount, description });
  }

  credit(accountId: string, amount: number, description: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/credit`, { accountId, amount, description });
  }

  transfer(accountSource: string, accountDestination: string, amount: number, description: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/transfer`, { accountSource, accountDestination, amount, description });
  }
}
