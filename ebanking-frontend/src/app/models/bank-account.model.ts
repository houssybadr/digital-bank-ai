import { Customer } from './customer.model';

export type AccountStatus = 'CREATED' | 'ACTIVATED' | 'SUSPENDED';
export type OperationType = 'DEBIT' | 'CREDIT';

export interface BankAccount {
  id: string;
  balance: number;
  createdAt: Date;
  status: AccountStatus;
  currency: string;
  type: string;
  customerDTO: Customer;
  overDraft?: number;
  interestRate?: number;
}

export interface AccountOperation {
  id: number;
  operationDate: Date;
  amount: number;
  type: OperationType;
  description: string;
}

export interface AccountHistory {
  accountId: string;
  balance: number;
  currentPage: number;
  totalPages: number;
  pageSize: number;
  accountOperationDTOs: AccountOperation[];
}
