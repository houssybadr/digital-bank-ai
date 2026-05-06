package ma.enset.ebankingbackend.mappers;

import ma.enset.ebankingbackend.dtos.*;
import ma.enset.ebankingbackend.entities.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {

    public CustomerDTO fromCustomer(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        BeanUtils.copyProperties(customer, dto);
        return dto;
    }

    public Customer fromCustomerDTO(CustomerDTO dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        return customer;
    }

    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount account) {
        CurrentBankAccountDTO dto = new CurrentBankAccountDTO();
        BeanUtils.copyProperties(account, dto);
        dto.setCustomerDTO(fromCustomer(account.getCustomer()));
        dto.setType("CurrentAccount");
        return dto;
    }

    public CurrentAccount fromCurrentBankAccountDTO(CurrentBankAccountDTO dto) {
        CurrentAccount account = new CurrentAccount();
        BeanUtils.copyProperties(dto, account);
        account.setCustomer(fromCustomerDTO(dto.getCustomerDTO()));
        return account;
    }

    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount account) {
        SavingBankAccountDTO dto = new SavingBankAccountDTO();
        BeanUtils.copyProperties(account, dto);
        dto.setCustomerDTO(fromCustomer(account.getCustomer()));
        dto.setType("SavingAccount");
        return dto;
    }

    public SavingAccount fromSavingBankAccountDTO(SavingBankAccountDTO dto) {
        SavingAccount account = new SavingAccount();
        BeanUtils.copyProperties(dto, account);
        account.setCustomer(fromCustomerDTO(dto.getCustomerDTO()));
        return account;
    }

    public AccountOperationDTO fromAccountOperation(AccountOperation operation) {
        AccountOperationDTO dto = new AccountOperationDTO();
        BeanUtils.copyProperties(operation, dto);
        return dto;
    }
}
