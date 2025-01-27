package customers;

import exceptions.CustomerNotFoundException;
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
                throw new CustomerNotFoundException("Customer with ID " + id + " not found");
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
        }catch (IllegalArgumentException e) {
            throw new RuntimeException("Customer ID darf nicht null sein", e);
        }
    }

    public String updateCustomer(Customer customer) {
        if (customer.getId() == null) {
            throw new IllegalArgumentException("Customer ID darf nicht null sein");
        }
        try {
            customerRepository.updateCustomer(customer);
            return "Kunde mit ID " + customer.getId() + " erfolgreich geupdatet";
        } catch (CustomerNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Updaten vom Kunden: ", e);
        }
    }

    public String deleteCustomer(String id) {
        try {
            // Validate and convert the UUID
            UUID uuid = UUID.fromString(id);

            // Attempt to delete the customer
            boolean isDeleted = customerRepository.deleteCustomer(uuid);

            // Return success if deleted, otherwise throw an exception
            if (isDeleted) {
                return "Kunde mit ID " + id + " erfolgreich gelöscht.";
            } else {
                throw new CustomerNotFoundException("Kunde mit ID " + id + " wurde nicht gefunden.");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for ID: " + id, e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete customer with ID: " + id, e);
        }
    }

}



