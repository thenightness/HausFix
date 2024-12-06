package customers;

import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService() {
        this.customerRepository = new CustomerRepository();
    }

    public Customer getCustomer(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        try {
            Customer customer = customerRepository.getCustomer(id);
            if (customer == null) {
                throw new RuntimeException("Customer with ID " + id + " not found");
            }
            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching customer with ID " + id, e);
        }
    }



    public List<Customer> getAllCustomers() {
        try {
            return customerRepository.getAllCustomers();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch customers: ", e);
        }
    }

    public String createCustomer(Customer customer) {
        try {
            if (customer.getId() == null) {
                customer.setId(UUID.randomUUID());
            }
            customerRepository.createCustomer(customer);
            return "Kunde mit ID " + customer.getId() + " erfolgreich erstellt";
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create customer: ", e);
        }
    }

    public String updateCustomer(Customer customer) {
        try{
            if (customer.getId() == null) {
                throw new RuntimeException("Customer ID not found");
            }
            customerRepository.updateCustomer(customer);
            return "Kunde mit ID " + customer.getId() + " erfolgreich geupdated";
        }
        catch (Exception e){
            throw new RuntimeException("Failed to update customer: ", e);
        }
    }

    public String deleteCustomer(String id) {
        try {
            UUID uuid = UUID.fromString(id);

            boolean isDeleted = customerRepository.deleteCustomer(uuid);

            if (isDeleted) {
                return "Kunde mit ID " + id + " erfolgreich gelöscht.";
            } else {
                return "Kunde mit ID " + id + " wurde nicht gefunden.";
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid UUID format for ID: " + id, e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete customer with ID: " + id, e);
        }
    }
}



