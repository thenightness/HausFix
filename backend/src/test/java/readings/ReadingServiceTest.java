package readings;

import customers.Customer;
import customers.CustomerRepository;
import database.DatabaseConnection;
import database.MySQL;
import jakarta.ws.rs.InternalServerErrorException;
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
    @Test
    @Order(6)
    void testCreateReadingWithNullCustomer_throwsException() {
        Reading reading = new Reading();
        reading.setCustomer(null);
        reading.setMeterCount(100.0);
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(IReading.KindOfMeter.WASSER);
        reading.setMeterId("M-NULL");

        assertThrows(IllegalArgumentException.class, () -> service.createReading(reading));
    }
    @Test
    @Order(7)
    void testCreateReadingWithCustomerWithoutId_generatesCustomerId() {
        Customer newCustomer = new Customer();
        newCustomer.setFirstName("Generated");
        newCustomer.setLastName("Customer");
        newCustomer.setGender(Customer.Gender.W);
        newCustomer.setBirthDate(LocalDate.of(1995, 5, 5));
        newCustomer.setId(null); // Important for test

        Reading reading = new Reading();
        reading.setCustomer(newCustomer);
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(IReading.KindOfMeter.WASSER);
        reading.setMeterCount(55.5);
        reading.setMeterId("M-GEN");
        reading.setSubstitute(false);
        reading.setComment("No customer ID");

        assertDoesNotThrow(() -> service.createReading(reading));
        assertNotNull(reading.getCustomer().getId());
        assertNotNull(reading.getId());
    }
    @Test
    @Order(8)
    void testUpdateReadingWithNullId_throwsNotFoundException() {
        Reading reading = createTestReading();
        reading.setId(null);

        assertThrows(NotFoundException.class, () -> service.updateReading(reading));
    }
    @Test
    @Order(9)
    void testUpdateReadingWithBrokenData_throwsInternalServerError() throws SQLException {
        Reading reading = createTestReading();
        service.createReading(reading);

        reading.setMeterId(null);

        assertThrows(InternalServerErrorException.class, () -> service.updateReading(reading));
    }
    @Test
    @Order(10)
    void testDeleteReadingWithUnknownId_throwsNotFound() {
        UUID unknownId = UUID.randomUUID();

        assertThrows(NotFoundException.class, () -> service.deleteReading(unknownId));
    }
    @Test
    @Order(11)
    void testGetFilteredReadings() {
        Reading reading = createTestReading();
        service.createReading(reading);

        LocalDate today = LocalDate.now();
        List<Reading> filtered = service.getFilteredReadings(testCustomer.getId(), today.minusDays(1), today.plusDays(1), IReading.KindOfMeter.STROM);

        assertFalse(filtered.isEmpty());
        assertTrue(filtered.stream().anyMatch(r -> r.getMeterId().equals("M-123")));
    }


}
