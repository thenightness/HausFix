package customers;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import database.MySQL;
import modules.ICustomer;

public class CustomerRepository {

    // Create
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

    public static Customer getCustomer(UUID id) throws SQLException {
        String query = "SELECT * FROM customers WHERE id = ?";
        ResultSet rs = MySQL.executeSelect(query, List.of(id.toString()));
        Customer customer = null;

        if (rs != null && rs.next()) {
            customer = new Customer();
            customer.setId(UUID.fromString(rs.getString("id")));
            customer.setFirstName(rs.getString("firstName"));
            customer.setLastName(rs.getString("lastName"));
            customer.setBirthDate(LocalDate.parse(rs.getString("birthDate")));
            customer.setGender(ICustomer.Gender.valueOf(rs.getString("gender")));
        }

        rs.close(); // Schließe das ResultSet hier
        return customer;
    }


    // Update
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

    /*public static void updateCustomer(Customer customer) throws SQLException {
        String query = "UPDATE customers SET firstName = ?, lastName = ?, birthDate = ?, gender = ? WHERE id = ?";

        // Erstelle PreparedStatement und setze Parameter direkt
        try (PreparedStatement p = MySQL.instance.connection.prepareStatement(query)) {
            // Setze die Parameter des PreparedStatements
            p.setString(1, customer.getFirstName());
            p.setString(2, customer.getLastName());
            p.setString(3, customer.getBirthDate().toString());
            p.setString(4, customer.getGender().name());
            p.setString(5, customer.getId().toString());

            // Führe das SQL-Update aus
            stmt.executeUpdate();
        }
    }*/


    // Delete
    public static void deleteCustomer(UUID id) throws SQLException {
        String query = "DELETE FROM customers WHERE id = ?";
        MySQL.executeStatement(query, List.of(id.toString()));
    }

    // Get All
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

    // Delete All
    public static void deleteAllCustomers() throws SQLException {
        String query = "DELETE FROM customers";
        MySQL.executeStatement(query, null);  // Führt das Lösch-Statement aus
    }

}
