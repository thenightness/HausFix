package customers;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import database.MySQL;
import modules.ICustomer;

public class CustomerRepository {

    // Create - Füge einen neuen Kunden hinzu
    public static void createCustomer(Customer customer) throws SQLException {
        String query = "INSERT INTO customers (id, firstName, lastName, birthDate, gender) VALUES (?, ?, ?, ?, ?)";
        MySQL.executeStatement(query, List.of(
                customer.getId().toString(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getBirthDate().toString(),
                customer.getGender().name()
        ));
    }

    // Read - Hole einen Kunden basierend auf der UUID
    public static Customer getCustomer(UUID id) throws SQLException {
        String query = "SELECT * FROM customers WHERE id = ?";
        ResultSet rs = MySQL.executeSelect(query);

        if (rs.next()) {
            Customer customer = new Customer();
            customer.setId(UUID.fromString(rs.getString("id")));
            customer.setFirstName(rs.getString("firstName"));
            customer.setLastName(rs.getString("lastName"));
            customer.setBirthDate(LocalDate.parse(rs.getString("birthDate")));
            customer.setGender(ICustomer.Gender.valueOf(rs.getString("gender")));
            return customer;
        }
        return null; // Gibt null zurück, wenn kein Kunde gefunden wird
    }

    // Update - Aktualisiere die Daten eines bestehenden Kunden
    public static void updateCustomer(Customer customer) throws SQLException {
        String query = "UPDATE customers SET firstName = ?, lastName = ?, birthDate = ?, gender = ? WHERE id = ?";
        MySQL.executeStatement(query, List.of(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getBirthDate().toString(),
                customer.getGender().name(),
                customer.getId().toString()
        ));
    }

    // Delete - Lösche einen Kunden aus der Datenbank
    public static void deleteCustomer(UUID id) throws SQLException {
        String query = "DELETE FROM customers WHERE id = ?";
        MySQL.executeStatement(query, List.of(id.toString()));
    }

    // Get All - Hole alle Kunden
    public static List<Customer> getAllCustomers() throws SQLException {
        String query = "SELECT * FROM customers";
        ResultSet rs = MySQL.executeSelect(query);
        List<Customer> customers = new ArrayList<>();

        while (rs.next()) {
            Customer customer = new Customer();
            customer.setId(UUID.fromString(rs.getString("id")));
            customer.setFirstName(rs.getString("firstName"));
            customer.setLastName(rs.getString("lastName"));
            customer.setBirthDate(LocalDate.parse(rs.getString("birthDate")));
            customer.setGender(ICustomer.Gender.valueOf(rs.getString("gender")));
            customers.add(customer);
        }
        return customers;
    }
}
