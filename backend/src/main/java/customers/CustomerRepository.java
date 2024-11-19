package customers;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import database.MySQL;
import modules.ICustomer;

public class CustomerRepository {

    private static void validateCustomerFields(Customer customer) {
        validateFieldsNotNull(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getBirthDate(), customer.getGender());

        if (customer.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Customer birthDate cannot be in the future");
        }

        if (customer.getLastName().trim().isEmpty() || customer.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer first name and last name cannot be empty or null");
        }

        if (!customer.getFirstName().matches("[a-zA-Z\\s]+") || !customer.getLastName().matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException("Customer first name and last name cannot contain special characters");
        }
    }


    private static void validateFieldsNotNull(Object... fields) {
        List<String> fieldNames = List.of("id", "firstName", "lastName", "birthDate", "gender");
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == null) {
                throw new IllegalArgumentException("Customer " + fieldNames.get(i) + " cannot be null");
            }
        }
    }
    // Create
    public static void createCustomer(Customer customer) throws SQLException {
        validateCustomerFields(customer);

        String query = "INSERT INTO customers (id, firstName, lastName, birthDate, gender) VALUES (?, ?, ?, ?, ?)";
        try {
            int rowsAffected = MySQL.executeStatement(query, List.of(
                    customer.getId().toString(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getBirthDate().toString(),
                    customer.getGender().name()
            ));
            //System.out.println("Rows affected: " + rowsAffected); nur für debugging nötig
            if (rowsAffected == 0) {
                throw new SQLException("Failed to create customer with ID: " + customer.getId());
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            System.out.println("Caught SQLIntegrityConstraintViolationException: " + e.getMessage());

            throw new SQLException("A customer with the same ID already exists. Duplicate entries are not allowed.", e);
        } catch (SQLException e) {
            throw e;
        }

    }


    public static Customer getCustomer(UUID id) throws SQLException {

        if (id == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
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
        validateCustomerFields(customer);

        String query = "UPDATE customers SET firstName = ?, lastName = ?, birthDate = ?, gender = ? WHERE id = ?";
        int rowsAffected = MySQL.executeStatement(query, List.of(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getBirthDate().toString(),
                customer.getGender().name(),
                customer.getId().toString()
        ));
        System.out.println("Rows affected: " + rowsAffected);
        if (rowsAffected == 0) {
            throw new SQLException("No such customer found with ID: " + customer.getId());
        }
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
        if (id == null) {
            throw new SQLException("Customer ID cannot be null");
        }
        String query = "DELETE FROM customers WHERE id = ?";
        int rowsAffected = MySQL.executeStatement(query, List.of(id.toString()));
        if (rowsAffected == 0) {
            throw new SQLException("No such customer found");

        }
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
