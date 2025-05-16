package customers;

import database.DatabaseConnection;
import database.MySQL;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import modules.ICustomer;
import modules.IReading;
import org.junit.jupiter.api.*;
import readings.CreateableReading;
import readings.ReadingRepository;
import response.DeletedCustomerResponse;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerServiceTest {

    private final CustomerService service = new CustomerService();
    private Customer customer;

    @BeforeEach
    void setUp() throws SQLException {
        MySQL.init("mariadb", 3306, System.getenv("MYSQL_DATABASE"), System.getenv("MYSQL_USER"), System.getenv("MYSQL_PASSWORD"));
        DatabaseConnection DBConnection = new DatabaseConnection();
        DBConnection.createAllTables();

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Hugh");
        customer.setLastName("Jass");
        customer.setBirthDate(LocalDate.of(1990, 1, 1));
        customer.setGender(ICustomer.Gender.M);
    }

    @Test
    @Order(1)
    void createCustomer_success() throws SQLException {
        service.createCustomer(customer);
        Customer result = CustomerRepository.getCustomer(customer.getId());
        assertNotNull(result);
        assertEquals("Hugh", result.getFirstName());
    }

    @Test
    @Order(2)
    void createCustomer_alreadyExists_ThrowsBadRequest() throws SQLException {
        CustomerRepository.createCustomer(customer);
        assertThrows(BadRequestException.class, () -> service.createCustomer(customer));
    }

    @Test
    @Order(3)
    void getCustomer_success() throws SQLException {
        CustomerRepository.createCustomer(customer);
        Customer result = service.getCustomer(customer.getId());
        assertNotNull(result);
        assertEquals(customer.getLastName(), result.getLastName());
    }

    @Test
    @Order(4)
    void getCustomer_nullId_ThrowsNotFound() {
        assertThrows(NotFoundException.class, () -> service.getCustomer(null));
    }

    @Test
    @Order(5)
    void getCustomer_notFound_ThrowsNotFound() {
        assertThrows(NotFoundException.class, () -> service.getCustomer(UUID.randomUUID()));
    }

    @Test
    @Order(6)
    void getAllCustomers_success() throws SQLException {
        CustomerRepository.createCustomer(customer);
        List<Customer> all = service.getAllCustomers();
        assertFalse(all.isEmpty());
    }

    @Test
    @Order(7)
    void updateCustomer_success() throws SQLException {
        CustomerRepository.createCustomer(customer);
        customer.setFirstName("Updated");
        String result = service.updateCustomer(customer);
        assertTrue(result.contains("erfolgreich"));

        Customer updated = CustomerRepository.getCustomer(customer.getId());
        assertEquals("Updated", updated.getFirstName());
    }

    @Test
    @Order(8)
    void updateCustomer_nullId_ThrowsNotFound() {
        customer.setId(null);
        assertThrows(NotFoundException.class, () -> service.updateCustomer(customer));
    }

    @Test
    @Order(9)
    void deleteCustomer_success() throws SQLException {
        CustomerRepository.createCustomer(customer);

        CreateableReading reading = new CreateableReading();
        reading.setId(UUID.randomUUID());
        reading.setMeterCount(123.45);
        reading.setDateOfReading(LocalDate.now());
        reading.setCustomerId(customer.getId());
        reading.setKindOfMeter(IReading.KindOfMeter.STROM);
        reading.setSubstitute(false);
        reading.setComment("Test reading");
        reading.setMeterId("M123");

        new readings.ReadingService().createReading(reading);

        DeletedCustomerResponse response = service.deleteCustomer(customer.getId());

        assertNotNull(response);
        assertNotNull(response.getDeletedCustomer());
        assertEquals(customer.getId(), response.getDeletedCustomer().getId());

        assertNull(CustomerRepository.getCustomer(customer.getId()));

        assertTrue(
                response.getOrphanedReadings().stream()
                        .anyMatch(r -> r.getId().equals(reading.getId()) && r.getCustomer() == null)
        );
    }

    @Test
    @Order(10)
    void deleteCustomer_notFound_ThrowsNotFound() {
        UUID unknownId = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> service.deleteCustomer(unknownId));
    }

    @Test
    @Order(11)
    void deleteCustomer_nullId_ThrowsIllegalArgumentException() {
        UUID invalid = null;
        assertThrows(IllegalArgumentException.class, () -> service.deleteCustomer(invalid));
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
