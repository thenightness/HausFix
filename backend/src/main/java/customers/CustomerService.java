package customers;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CustomerService {

    public void createCustomer(Customer customer) {
        try {
            if (customer.getId() == null) {
                customer.setId(UUID.randomUUID());
            }
            if (null == CustomerRepository.getCustomer(customer.getId())) {
                CustomerRepository.createCustomer(customer);
            } else {
                throw new BadRequestException();
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to create customer: ", e);
        }
    }

    public Customer getCustomer(UUID id) {
        if (id == null) {
            throw new NotFoundException("Customer ID cannot be null");
        }

        try {
            Customer customer = CustomerRepository.getCustomer(id);
            if (customer == null) {
                throw new NotFoundException("Customer with ID " + id + " not found");
            }
            return customer;
        } catch (SQLException e) {
            throw new InternalServerErrorException("Error fetching customer with ID " + id, e);
        }
    }

    public List<Customer> getAllCustomers() {
        try {
            return CustomerRepository.getAllCustomers();
        } catch (SQLException e) {
            throw new InternalServerErrorException("Failed to fetch customers: ", e);
        }
    }

    public String updateCustomer(Customer customer) {
        if (customer.getId() == null) {
            throw new NotFoundException("Customer ID darf nicht null sein");
        }
        CustomerRepository.updateCustomer(customer);
        return "Kunde mit ID " + customer.getId() + " erfolgreich geupdatet";
    }

    public String deleteCustomer(UUID uuid) {
        try {
            // Attempt to delete the customer
            boolean isDeleted = CustomerRepository.deleteCustomer(uuid);

            // Return success if deleted, otherwise throw an exception
            if (isDeleted) {
                return "Kunde mit ID " + uuid + " erfolgreich gelöscht.";
            } else {
                throw new NotFoundException("Kunde mit ID " + uuid + " wurde nicht gefunden.");
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Failed to delete customer with ID: " + uuid, e);
        }
    }
}