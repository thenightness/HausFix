package readings;

import customers.CustomerRepository;
import database.DatabaseConnection;
import database.MySQL;
import jakarta.ws.rs.NotFoundException;
import modules.IReading;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReadingServiceTest {

    private static ReadingService service;
    private static UUID testCustomerId;

    @BeforeAll
    static void setup() throws SQLException {
        MySQL.init("mariadb", 3306, System.getenv("MYSQL_DATABASE"), System.getenv("MYSQL_USER"), System.getenv("MYSQL_PASSWORD"));
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.createAllTables();
        System.out.println("Connection Successful!");
        service = new ReadingService();
        // Setze eine gültige customerId aus der Datenbank
        testCustomerId = CustomerRepository.getAllCustomers().get(0).getId(); // nimmt den ersten Kunden
    }

    private Reading createTestReading() throws SQLException {
        Reading reading = new Reading();
        reading.setCustomer(CustomerRepository.getCustomer(testCustomerId));
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(IReading.KindOfMeter.STROM);  // Beispielwert – ggf. anpassen
        reading.setMeterCount(123.45);
        reading.setMeterId("M-123");
        reading.setSubstitute(false);
        reading.setComment("Testmessung");

        return reading;
    }

    @Test
    @Order(1)
    void testCreateReading() throws SQLException {
        Reading reading = createTestReading();

        assertDoesNotThrow(() -> service.createReading(reading));
        assertNotNull(reading.getId());
    }

    @Test
    @Order(2)
    void testGetReading() throws SQLException {
        Reading reading = createTestReading();
        service.createReading(reading);

        Reading result = service.getReading(reading.getId());
        assertNotNull(result);
        assertEquals(reading.getId(), result.getId());
        assertEquals(reading.getMeterId(), result.getMeterId());
    }

    @Test
    @Order(3)
    void testUpdateReading() throws SQLException {
        Reading reading = createTestReading();
        service.createReading(reading);

        reading.setComment("Geändert durch Test");
        String result = service.updateReading(reading);

        assertTrue(result.contains("erfolgreich"));
    }

    @Test
    @Order(4)
    void testDeleteReading() throws SQLException {
        Reading reading = createTestReading();
        service.createReading(reading);

        String result = service.deleteReading(reading.getId());
        assertTrue(result.contains("erfolgreich gelöscht"));
    }

    @Test
    @Order(5)
    void testGetReadingNotFound() {
        UUID fakeId = UUID.randomUUID();

        assertThrows(NotFoundException.class, () -> service.getReading(fakeId));
    }
}
