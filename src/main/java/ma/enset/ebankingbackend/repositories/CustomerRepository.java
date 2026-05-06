package ma.enset.ebankingbackend.repositories;

import ma.enset.ebankingbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByNameContainsIgnoreCase(String keyword);

    @Query("SELECT c FROM Customer c WHERE c.name LIKE :keyword OR c.email LIKE :keyword")
    List<Customer> searchCustomers(@Param("keyword") String keyword);
}
