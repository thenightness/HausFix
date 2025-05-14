package readings;

import customers.Customer;
import customers.CustomerRepository;
import database.DatabaseConnection;
import database.MySQL;
import jakarta.ws.rs.NotFoundException;
import modules.IReading;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReadingServiceTest {

    private static ReadingService service;
    private static Customer testCustomer;

    @BeforeAll
    static void setup() throws SQLException {
        System.out.println("Initialisiere Datenbankverbindung");
        MySQL.init("mariadb", 3306, System.getenv("MYSQL_DATABASE"), System.getenv("MYSQL_USER"), System.getenv("MYSQL_PASSWORD"));
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.createAllTables();
        System.out.println("✅ Tabellen erstellt und Verbindung erfolgreich.");

        service = new ReadingService();

        List<Customer> customers = CustomerRepository.getAllCustomers();

        if (customers.isEmpty()) {
            System.out.println("Kein Kunde gefunden – erzeuge Testkunde");
            testCustomer = new Customer();
            testCustomer.setId(UUID.randomUUID());
            testCustomer.setFirstName("Test");
            testCustomer.setLastName("Kunde");
            testCustomer.setBirthDate(LocalDate.of(2000, 1, 1));
            testCustomer.setGender(Customer.Gender.M);

            CustomerRepository.createCustomer(testCustomer);
            System.out.println("✅ Testkunde angelegt mit ID: " + testCustomer.getId());
        } else {
            testCustomer = customers.get(0);
            System.out.println("✅ Bestehender Kunde wird verwendet: " + testCustomer.getId());
        }
    }

    private Reading createTestReading() {
        Reading reading = new Reading();
        reading.setCustomer(testCustomer);
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(IReading.KindOfMeter.STROM);
        reading.setMeterCount(123.45);
        reading.setMeterId("M-123");
        reading.setSubstitute(false);
        reading.setComment("Testmessung");

        return reading;
    }

    @Test
    @Order(1)
    void testCreateReading() {
        Reading reading = createTestReading();

        System.out.println("\nTest: createReading()");
        assertDoesNotThrow(() -> service.createReading(reading));
        assertNotNull(reading.getId());
        System.out.println("✅ Reading erfolgreich erstellt mit ID: " + reading.getId());
    }

    @Test
    @Order(2)
    void testGetReading() throws SQLException {
        Reading reading = createTestReading();
        service.createReading(reading);
        System.out.println("\nTest: getReading() für ID " + reading.getId());

        Reading result = service.getReading(reading.getId());

        assertNotNull(result);
        assertEquals(reading.getId(), result.getId());
        assertEquals(reading.getMeterId(), result.getMeterId());
        assertNotNull(reading.getCustomer());
        assertNotNull(reading.getCustomer().getId());

        System.out.println("✅ Reading erfolgreich gefunden:");
        System.out.println("   ID:         " + result.getId());
        System.out.println("   MeterId:    " + result.getMeterId());
        System.out.println("   Count:      " + result.getMeterCount());
        System.out.println("   Comment:    " + result.getComment());
        System.out.println("   CustomerID: " + result.getCustomer().getId());
    }

    @Test
    @Order(3)
    void testUpdateReading() throws SQLException {
        Reading reading = createTestReading();
        service.createReading(reading);

        System.out.println("\nTest: updateReading()");
        reading.setComment("Geändert durch Test");
        String result = service.updateReading(reading);

        System.out.println("Neuer Kommentar: " + reading.getComment());
        assertTrue(result.contains("erfolgreich"));
        System.out.println("✅ Reading erfolgreich aktualisiert.");
    }

    @Test
    @Order(4)
    void testDeleteReading() throws SQLException {
        Reading reading = createTestReading();
        service.createReading(reading);

        System.out.println("\nTest: deleteReading()");
        String result = service.deleteReading(reading.getId());

        assertTrue(result.contains("erfolgreich gelöscht"));
        System.out.println("✅ Reading erfolgreich gelöscht: " + reading.getId());
    }

    @Test
    @Order(5)
    void testGetReadingNotFound() {
        UUID fakeId = UUID.randomUUID();
        System.out.println("\nTest: getReading() mit ungültiger ID " + fakeId);
        assertThrows(NotFoundException.class, () -> service.getReading(fakeId));
        System.out.println("✅ Ausnahme wie erwartet: NotFoundException");
    }
}
