package customers;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService() {
        this.customerRepository = new CustomerRepository();
    }

    public List<Customer> getAllCustomers() {
        try {
            return customerRepository.getAllCustomers();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch customers", e);
        }
    }

    public void createCustomer(Customer customer) {
        try {
            customer.setId(UUID.randomUUID());
            customerRepository.createCustomer(customer);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create customer", e);
        }
    }
}
