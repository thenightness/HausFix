package customers;

import database.MySQL;
import jakarta.ws.rs.NotFoundException;
import modules.ICustomer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerRepository {

    // Fügt einen neuen Kunden in die Datenbank ein
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

    // Ruft einen Kunden anhand der ID ab
    public static Customer getCustomer(UUID id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        String query = "SELECT * FROM customers WHERE id = ?";
        ResultSet resultSet = MySQL.executeSelect(query, List.of(id.toString()));

        if (resultSet != null && resultSet.next()) {
            return mapResultSetToCustomer(resultSet);
        } else {
            return null;
        }
    }

    // Gibt alle Kunden aus der Datenbank zurück
    public static List<Customer> getAllCustomers() throws SQLException {
        String query = "SELECT * FROM customers";
        ResultSet rs = MySQL.executeSelect(query);
        List<Customer> customers = new ArrayList<>();
        while (rs.next()) {
            customers.add(mapResultSetToCustomer(rs));
        }
        return customers;
    }

    // Aktualisiert einen bestehenden Kunden
    public static void updateCustomer(Customer customer) {
        String query = "UPDATE customers SET firstName = ?, lastName = ?, birthDate = ?, gender = ? WHERE id = ?";
        int rowsAffected = MySQL.executeStatement(query, List.of(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getBirthDate().toString(),
                customer.getGender().name(),
                customer.getId().toString()
        ));
        if (rowsAffected == 0) {
            throw new NotFoundException("Kein Kunde mit ID: " + customer.getId());
        }
    }

    // Löscht einen Kunden anhand der ID
    public static boolean deleteCustomer(UUID id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        String query = "DELETE FROM customers WHERE id = ?";
        int rowsAffected = MySQL.executeStatement(query, List.of(id.toString()));

        return rowsAffected > 0;
    }

    // Hilfsmethode: Wandelt ein ResultSet in ein Customer-Objekt um
    private static Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(UUID.fromString(rs.getString("id")));
        customer.setFirstName(rs.getString("firstName"));
        customer.setLastName(rs.getString("lastName"));
        customer.setBirthDate(LocalDate.parse(rs.getString("birthDate")));
        customer.setGender(ICustomer.Gender.valueOf(rs.getString("gender")));
        return customer;
    }
}
