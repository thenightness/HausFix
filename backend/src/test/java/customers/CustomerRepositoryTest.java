package customers;
import database.DatabaseConnection;
import database.MySQL;

import database.TestDBConnection;
import modules.ICustomer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryTest {

    private static final UUID uuid = UUID.randomUUID();
    private Customer customer;
    private static final Logger logger = LoggerFactory.getLogger(CustomerRepositoryTest.class);

    @BeforeEach
    public void setUp() throws SQLException {

        MySQL.init("localhost", 3308, "hausfixtestdb","hausfixtestuser","rfxj7");
        TestDBConnection testDBConnection = new TestDBConnection();
        testDBConnection.createAllTables();


        customer = new Customer();
        customer.setId(uuid);
        customer.setFirstName("Hugh");
        customer.setLastName("Jass");
        customer.setBirthDate(LocalDate.parse("1990-01-01"));
        customer.setGender(ICustomer.Gender.M);

        CustomerRepository.createCustomer(customer);
    }

    @Test
    void testDatabaseConnection() throws SQLException {
        assertTrue(MySQL.isConnected());

    }

    @Test
    void testCreateCustomer() throws SQLException {

        String query = "SELECT * FROM customers WHERE id = ?";
        try (java.sql.ResultSet resultSet = MySQL.executeSelect(query, List.of(customer.getId().toString()))) {
            assertTrue(resultSet.next(), "Customer not found in database.");

           logger.info("Retrieved customer details from database:");
           logger.info("ID: {}", resultSet.getString("id"));
           logger.info("First Name: {}", resultSet.getString("firstName"));
           logger.info("Last Name: {}", resultSet.getString("lastName"));
           logger.info("Birth Date: {}", resultSet.getDate("birthDate").toLocalDate());
           logger.info("Gender: {}", resultSet.getString("gender"));

            assertEquals(customer.getFirstName(), resultSet.getString("firstName"));
            assertEquals(customer.getLastName(), resultSet.getString("lastName"));
            assertEquals(customer.getBirthDate().toString(), resultSet.getDate("birthDate").toLocalDate().toString());
            assertEquals(customer.getGender().name(), resultSet.getString("gender"));
        } catch (SQLException e) {
            fail("An error occurred while verifying the customer in the database: " + e.getMessage());
        }
    }

    @Test
    void testCreateCustomerWithInvalidFirstName() {
        customer.setFirstName("#1 Dude!!");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CustomerRepository.updateCustomer(customer);
        });
        System.out.println("Caught exception: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Customer first name and last name cannot contain special characters"));
    }
    @Test
    void testCreateCustomerWithInvalidLastName() {
        customer.setLastName("+wahh!!");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CustomerRepository.updateCustomer(customer);
        });
        System.out.println("Caught exception: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Customer first name and last name cannot contain special characters"));
    }

    @Test
    void testDuplicateCustomerCreation() {
        Exception exception = assertThrows(SQLException.class, () -> {
            CustomerRepository.createCustomer(customer); // Customer already exists in the database
        });
        System.out.println("Caught exception: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("A customer with the same ID already exists"),
                "Expected custom error message for duplicate entries.");
    }


    @Test
    void testGetAllCustomersWithLargeDataset() throws SQLException {
        for (int i = 0; i < 100; i++) {
            Customer tempCustomer = new Customer();
            tempCustomer.setId(UUID.randomUUID());
            tempCustomer.setFirstName("Test");
            tempCustomer.setLastName("User");
            tempCustomer.setBirthDate(LocalDate.parse("1990-01-01"));
            tempCustomer.setGender(ICustomer.Gender.M);

            CustomerRepository.createCustomer(tempCustomer);
        }

        List<Customer> customers = CustomerRepository.getAllCustomers();
        assertEquals(101, customers.size(), "The number of customers retrieved is incorrect.");
    }



    @Test
    void testCreateCustomerWithNullFirstName() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CustomerRepository.createCustomer(customer);
        });
        System.out.println("Caught exception: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Customer firstName cannot be null"), "Expected IllegalArgumentException due to missing first name.");
    }

    @Test
    void testCreateCustomerWithEmptyLastName() {
        customer.setLastName("");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CustomerRepository.createCustomer(customer);
        });
        System.out.println("Caught exception: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Customer first name and last name cannot be empty or null"), "Expected IllegalArgumentException due to empty last name.");
    }

    @Test
    void testGetCustomerWithInvalidUUID() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CustomerRepository.getCustomer(null);
        });
        System.out.println("Caught exception: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Customer ID cannot be null"));
    }


    @Test
    void testCreateCustomerWithFutureBirthDate() {
        customer.setId(UUID.randomUUID());
        customer.setBirthDate(LocalDate.now().plusDays(1)); // Geburtsdatum ist erst morgen
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CustomerRepository.createCustomer(customer);
        });
        System.out.println("Caught exception: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Customer birthDate cannot be in the future"));
    }


    @Test
    void testGetCustomer() throws SQLException {

        Customer retrievedCustomer = CustomerRepository.getCustomer(customer.getId());

        assertNotNull(retrievedCustomer, "Retrieved customer should not be null.");
        assertEquals(customer.getId(), retrievedCustomer.getId(), "Customer ID should match.");
        assertEquals(customer.getFirstName(), retrievedCustomer.getFirstName(), "Customer first name should match.");
        assertEquals(customer.getLastName(), retrievedCustomer.getLastName(), "Customer last name should match.");
        assertEquals(customer.getBirthDate(), retrievedCustomer.getBirthDate(), "Customer birth date should match.");
        assertEquals(customer.getGender(), retrievedCustomer.getGender(), "Customer gender should match.");
    }

    @Test
    void testGetNonExistentCustomer() throws SQLException {
        UUID nonExistentId = UUID.randomUUID(); //uuid ist keinem kunden zugeordnet
        Customer customer = CustomerRepository.getCustomer(nonExistentId);

        assertNull(customer, "Expected null when retrieving a non-existent customer.");
    }


    @Test
    void testUpdateCustomer() throws SQLException {


        customer.setFirstName("NewFirstName");
        customer.setLastName("NewLastName");
        customer.setBirthDate(LocalDate.parse("2000-01-01"));
        customer.setGender(ICustomer.Gender.W);

        CustomerRepository.updateCustomer(customer);

        String query = "SELECT * FROM customers WHERE id = ?";
        try (ResultSet resultSet = MySQL.executeSelect(query, List.of(customer.getId().toString()))) {
            assertTrue(resultSet.next(), "Customer was not found in the database after update.");

            assertEquals("NewFirstName", resultSet.getString("firstName"));
            assertEquals("NewLastName", resultSet.getString("lastName"));
            assertEquals("2000-01-01", resultSet.getDate("birthDate").toLocalDate().toString());
            assertEquals("W", resultSet.getString("gender"));
        } catch (SQLException e) {
            fail("An error occurred while verifying the updated customer: " + e.getMessage());
        }
    }

    @Test
    void testUpdateNonExistentCustomer() {
        Customer nonExistentCustomer = new Customer();
        nonExistentCustomer.setId(UUID.randomUUID());  //UUID existiert nicht
        nonExistentCustomer.setFirstName("Ghost");
        nonExistentCustomer.setLastName("User");
        nonExistentCustomer.setBirthDate(LocalDate.parse("1980-01-01"));
        nonExistentCustomer.setGender(ICustomer.Gender.U);

        Exception exception = assertThrows(SQLException.class, () -> {
            CustomerRepository.updateCustomer(nonExistentCustomer);
        });
        System.out.println("Caught exception: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("No such customer"), "Expected exception for updating a non-existent customer.");
    }

    @Test
    void testUpdateCustomerPartialFields() throws SQLException {
        customer.setLastName("UpdatedLastName");
        CustomerRepository.updateCustomer(customer);

        Customer retrievedCustomer = CustomerRepository.getCustomer(customer.getId());
        assertNotNull(retrievedCustomer);
        assertEquals("UpdatedLastName", retrievedCustomer.getLastName());
        assertEquals("Hugh", retrievedCustomer.getFirstName(), "First name should not be changed/null.");
    }

    @Test
    void testUpdateCustomerWithInvalidFields() {
        customer.setFirstName("#1 Dude!!");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CustomerRepository.updateCustomer(customer);
        });
        System.out.println("Caught exception: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Customer first name and last name cannot contain special characters"));
    }



    @Test
    void testDeleteCustomer() throws SQLException {

        String query = "SELECT * FROM customers WHERE id = ?";
        try (java.sql.ResultSet resultSet = MySQL.executeSelect(query, List.of(customer.getId().toString()))) {
            assertTrue(resultSet.next(), "Customer not found in database.");

            assertEquals(customer.getFirstName(), resultSet.getString("firstName"));
            assertEquals(customer.getLastName(), resultSet.getString("lastName"));
            assertEquals(customer.getBirthDate().toString(), resultSet.getDate("birthDate").toLocalDate().toString());
            assertEquals(customer.getGender().name(), resultSet.getString("gender"));
        } catch (SQLException e) {
            fail("An error occurred while verifying the customer in the database: " + e.getMessage());
        }

        CustomerRepository.deleteCustomer(customer.getId());

        try (ResultSet resultSet = MySQL.executeSelect(query, List.of(customer.getId().toString()))) {
            assertFalse(resultSet.next(), "Customer was found in the database after deletion.");
        } catch (SQLException e) {
            fail("An error occurred while verifying the customer deletion: " + e.getMessage());
        }
    }


    @Test
    void testGetAllCustomers() throws SQLException {

        Customer customer2 = new Customer();
        customer2.setId(UUID.randomUUID());
        customer2.setFirstName("Dewy");
        customer2.setLastName("Doe");
        customer2.setBirthDate(LocalDate.parse("1992-02-02"));
        customer2.setGender(ICustomer.Gender.W);
        CustomerRepository.createCustomer(customer2);

        List<Customer> customers = CustomerRepository.getAllCustomers();
        logger.info("Retrieved customers:");
        customers.forEach(customer -> logger.info(customer.toString()));

        assertEquals(2, customers.size(), "The number of customers retrieved is incorrect.");

        assertTrue(customers.stream().anyMatch(c ->
                c.getFirstName().equals("Hugh") &&
                        c.getLastName().equals("Jass") &&
                        c.getBirthDate().equals(LocalDate.parse("1990-01-01")) &&
                        c.getGender() == ICustomer.Gender.M
        ), "Customer 1 details do not match.");

        assertTrue(customers.stream().anyMatch(c ->
                c.getFirstName().equals("Dewy") &&
                        c.getLastName().equals("Doe") &&
                        c.getBirthDate().equals(LocalDate.parse("1992-02-02")) &&
                        c.getGender() == ICustomer.Gender.W
        ), "Customer 2 details do not match.");

    }


    @Test
    void testDeleteAllCustomers() throws SQLException {
        Customer customer2 = new Customer();
        customer2.setId(UUID.randomUUID());
        customer2.setFirstName("Dewy");
        customer2.setLastName("Doe");
        customer2.setBirthDate(LocalDate.parse("1992-02-02"));
        customer2.setGender(ICustomer.Gender.W);
        CustomerRepository.createCustomer(customer2);

        List<Customer> customersBeforeDelete = CustomerRepository.getAllCustomers();
        assertEquals(2, customersBeforeDelete.size(), "There should be 2 customers before deletion.");

        CustomerRepository.deleteAllCustomers();

        List<Customer> customersAfterDelete = CustomerRepository.getAllCustomers();
        assertTrue(customersAfterDelete.isEmpty(), "All customers should be deleted.");


    }

    @Test
    void testDeleteNonExistentCustomer() {
        UUID randomId = UUID.randomUUID();
        Exception exception = assertThrows(SQLException.class, () -> {
            CustomerRepository.deleteCustomer(randomId);
        });
        System.out.println("Caught exception: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("No such customer found"));
    }


    @AfterEach
    public void tearDown() throws SQLException {
        MySQL.executeStatement("DELETE FROM customers;", null);

    }


    @AfterAll
    public static void endTests(){
        MySQL.disconnect();
  }


 }