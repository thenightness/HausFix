package readings;

import customers.Customer;
import customers.CustomerRepository;
import database.DatabaseConnection;
import database.MySQL;
import jakarta.ws.rs.NotFoundException;
import modules.ICustomer;
import modules.IReading;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReadingRepositoryTest {

    private Customer customer;
    private Reading reading;

    @BeforeEach
    void setUp() throws SQLException {
        MySQL.init("mariadb", 3306, System.getenv("MYSQL_DATABASE"), System.getenv("MYSQL_USER"), System.getenv("MYSQL_PASSWORD"));
        DatabaseConnection DBConnection = new DatabaseConnection();
        DBConnection.createAllTables();

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Test");
        customer.setLastName("User");
        customer.setBirthDate(LocalDate.of(1980, 1, 1));
        customer.setGender(ICustomer.Gender.M);
        CustomerRepository.createCustomer(customer);


        reading = new Reading();
        reading.setId(UUID.randomUUID());
        reading.setMeterCount(42.5);
        reading.setDateOfReading(LocalDate.now());
        reading.setCustomer(customer);
        reading.setKindOfMeter(IReading.KindOfMeter.HEIZUNG);
        reading.setSubstitute(false);
        reading.setComment("Initial");
        reading.setMeterId("MTR-123");
        ReadingRepository.createReading(reading);
    }

    @Test
    void testCreateAndGetReading() throws SQLException {
        Reading result = ReadingRepository.getReading(reading.getId());
        assertNotNull(result);
        assertEquals(reading.getId(), result.getId());
        assertEquals(reading.getMeterId(), result.getMeterId());
    }

    @Test
    void testGetReadingWithUnknownIdReturnsNull() throws SQLException {
        UUID unknownId = UUID.randomUUID();
        Reading result = ReadingRepository.getReading(unknownId);
        assertNull(result);
    }

    @Test
    void testGetReadingWithNullIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ReadingRepository.getReading(null));
    }

    @Test
    void testUpdateReading() throws SQLException {
        reading.setMeterCount(99.9);
        reading.setComment("Updated");
        ReadingRepository.updateReading(reading);

        Reading updated = ReadingRepository.getReading(reading.getId());
        assertEquals(99.9, updated.getMeterCount());
        assertEquals("Updated", updated.getComment());
    }

    @Test
    void testUpdateReadingWithInvalidIdThrowsException() {
        Reading invalid = new Reading();
        invalid.setId(UUID.randomUUID()); // not in DB
        invalid.setMeterCount(55.6);
        invalid.setDateOfReading(LocalDate.now());
        invalid.setCustomer(customer);
        invalid.setKindOfMeter(IReading.KindOfMeter.HEIZUNG);
        invalid.setSubstitute(false);
        invalid.setComment("Does not exist");
        invalid.setMeterId("MTR-X");

        assertThrows(NotFoundException.class, () -> ReadingRepository.updateReading(invalid));
    }

    @Test
    void testDeleteReading() throws SQLException {
        boolean deleted = ReadingRepository.deleteReading(reading.getId());
        assertTrue(deleted);

        Reading result = ReadingRepository.getReading(reading.getId());
        assertNull(result);
    }

    @Test
    void testDeleteReadingWithUnknownIdReturnsFalse() throws SQLException {
        UUID unknownId = UUID.randomUUID();
        boolean result = ReadingRepository.deleteReading(unknownId);
        assertFalse(result);
    }

    @Test
    void testDeleteReadingWithNullIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ReadingRepository.deleteReading(null));
    }

    @Test
    void testGetReadingWithNullComment() throws SQLException {
        UUID id = UUID.randomUUID();
        String query = "INSERT INTO readings (id, meterCount, dateOfReading, customerId, kindOfMeter, substitute, comment, meterId) " +
                "VALUES (?, ?, ?, ?, ?, ?, NULL, ?)";
        MySQL.executeStatement(query, java.util.List.of(
                id.toString(),
                "44.4",
                LocalDate.now().toString(),
                customer.getId().toString(),
                IReading.KindOfMeter.STROM.toString(),
                "0",
                "MTR-NULL"
        ));

        Reading r = ReadingRepository.getReading(id);
        assertNotNull(r);
        assertEquals("", r.getComment());
    }

    @Test
    void testGetReadingWithNullCustomerId() throws SQLException {
        UUID id = UUID.randomUUID();
        String query = "INSERT INTO readings (id, meterCount, dateOfReading, customerId, kindOfMeter, substitute, comment, meterId) " +
                "VALUES (?, ?, ?, NULL, ?, ?, ?, ?)";
        MySQL.executeStatement(query, java.util.List.of(
                id.toString(),
                "33.3",
                LocalDate.now().toString(),
                IReading.KindOfMeter.HEIZUNG.toString(),
                "1",
                "Comment",
                "MTR-NOCUST"
        ));

        Reading r = ReadingRepository.getReading(id);
        assertNotNull(r);
        assertNull(r.getCustomer());
    }

    @AfterEach
    void cleanUp() throws SQLException {
        MySQL.executeStatement("DELETE FROM readings;", null);
        MySQL.executeStatement("DELETE FROM customers;", null);
    }

    @AfterAll
    static void tearDown() {
        MySQL.disconnect();
    }
}
