package customers;

import database.DatabaseConnection;
import database.MySQL;
import jakarta.ws.rs.NotFoundException;
import modules.ICustomer;
import org.junit.jupiter.api.*;
import readings.Reading;
import readings.ReadingRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryTest {

    private Customer customer;

    @BeforeEach
    public void setUp() throws SQLException {
        MySQL.init("mariadb", 3306, System.getenv("MYSQL_DATABASE"), System.getenv("MYSQL_USER"), System.getenv("MYSQL_PASSWORD"));
        DatabaseConnection DBConnection = new DatabaseConnection();
        DBConnection.createAllTables();

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Hugh");
        customer.setLastName("Jass");
        customer.setBirthDate(LocalDate.of(1990, 1, 1));
        customer.setGender(ICustomer.Gender.M);

        CustomerRepository.createCustomer(customer);
    }

    @Test
    void testCreateCustomer() throws SQLException {
        Customer result = CustomerRepository.getCustomer(customer.getId());
        assertNotNull(result, "Customer should be found in the database");
        assertEquals(customer.getFirstName(), result.getFirstName());
    }

    @Test
    void testGetAllCustomers() throws SQLException {
        List<Customer> customers = CustomerRepository.getAllCustomers();
        assertTrue(customers.size() >= 1);
    }

    @Test
    void testUpdateCustomer() throws SQLException {
        customer.setFirstName("Updated");
        CustomerRepository.updateCustomer(customer);
        Customer updated = CustomerRepository.getCustomer(customer.getId());
        assertEquals("Updated", updated.getFirstName());
    }

    @Test
    void testDeleteCustomer_andOrphanReadings() throws SQLException {
        assertNotNull(CustomerRepository.getCustomer(customer.getId()));

        Customer deletedCustomer = CustomerRepository.deleteCustomer(customer.getId());

        assertNotNull(deletedCustomer);
        assertEquals(customer.getId(), deletedCustomer.getId());

        assertNull(CustomerRepository.getCustomer(customer.getId()));
        List<Reading> orphaned = ReadingRepository.getReadingsWithNullCustomer();
        assertTrue(orphaned.stream().anyMatch(r -> r.getCustomer() == null));
    }


    @Test
    void testGetCustomerWithUnknownIdReturnsNull() throws SQLException {
        UUID unknownId = UUID.randomUUID();
        Customer result = CustomerRepository.getCustomer(unknownId);
        assertNull(result, "Expected null for non-existing customer ID");
    }
    @Test
    void testUpdateCustomerWithNonExistentIdThrowsException() {
        Customer nonExistent = new Customer();
        nonExistent.setId(UUID.randomUUID());
        nonExistent.setFirstName("Test");
        nonExistent.setLastName("User");
        nonExistent.setBirthDate(LocalDate.of(1980, 1, 1));
        nonExistent.setGender(ICustomer.Gender.U);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            CustomerRepository.updateCustomer(nonExistent);
        });

        assertTrue(ex.getMessage().contains("Kein Kunde mit ID"), "Expected NotFoundException for update");
    }
    @Test
    void testDeleteCustomerWithNullIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            CustomerRepository.deleteCustomer(null);
        });
    }
    @Test
    void testGetCustomerWithNullIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            CustomerRepository.getCustomer(null);
        });
    }
    @Test
    void testCreateCustomerWithNullValuesThrowsException() {
        Customer badCustomer = new Customer();
        badCustomer.setId(UUID.randomUUID());
        badCustomer.setFirstName(null);
        badCustomer.setLastName(null);
        badCustomer.setBirthDate(null);
        badCustomer.setGender(null);

        assertThrows(NullPointerException.class, () -> {
            CustomerRepository.createCustomer(badCustomer);
        });
    }
    @Test
    void testSetNullFirstName() {
        Customer customer = new Customer();
        customer.setFirstName(null);
        assertNull(customer.getFirstName());
    }
    @Test
    void testSetNullLastName() {
        Customer customer = new Customer();
        customer.setLastName(null);
        assertNull(customer.getFirstName());
    }
    @Test
    void testSetNullBirthdate() {
        Customer customer = new Customer();
        customer.setBirthDate(null);
        assertNull(customer.getFirstName());
    }
    @Test
    void testSetNullGender() {
        Customer customer = new Customer();
        customer.setGender(null);
        assertNull(customer.getFirstName());
    }



    @AfterEach
    public void cleanUp() throws SQLException {
        MySQL.executeStatement("DELETE FROM customers;", null);
    }

    @AfterAll
    public static void tearDown() {
        MySQL.disconnect();
    }
}
