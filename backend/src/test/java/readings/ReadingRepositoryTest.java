package readings;

import customers.Customer;
import customers.CustomerRepository;
import database.DatabaseConnection;
import database.MySQL;
import modules.ICustomer;
import modules.IReading;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReadingRepositoryTest {

    private static Customer testCustomer;
    private static ReadingService readingService;

    @BeforeAll
    static void setup() throws SQLException {
        MySQL.init("mariadb", 3306,
                System.getenv("MYSQL_DATABASE"),
                System.getenv("MYSQL_USER"),
                System.getenv("MYSQL_PASSWORD"));
        new DatabaseConnection().createAllTables();

        testCustomer = new Customer();
        testCustomer.setId(UUID.randomUUID());
        testCustomer.setFirstName("Read");
        testCustomer.setLastName("Tester");
        testCustomer.setBirthDate(LocalDate.of(1990, 1, 1));
        testCustomer.setGender(ICustomer.Gender.M);
        CustomerRepository.createCustomer(testCustomer);

        readingService = new ReadingService();
    }

    CreateableReading makeReading() {
        CreateableReading reading = new CreateableReading();
        reading.setCustomerId(testCustomer.getId());
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(IReading.KindOfMeter.STROM);
        reading.setMeterCount(456.78);
        reading.setMeterId("R-456");
        reading.setSubstitute(false);
        reading.setComment("Repo test");
        return reading;
    }

    @Test
    @Order(1)
    void testCreateAndGetReading_success() throws SQLException {
        CreateableReading reading = makeReading();
        UUID id = readingService.createReading(reading);

        Reading result = ReadingRepository.getReading(id);
        assertNotNull(result);
        assertEquals(reading.getMeterId(), result.getMeterId());
        assertEquals(testCustomer.getId(), result.getCustomer().getId());
    }

    @Test
    @Order(2)
    void testUpdateReading_success() throws SQLException {
        CreateableReading reading = makeReading();
        UUID id = readingService.createReading(reading);
        reading.setId(id);
        reading.setComment("Updated comment");

        ReadingRepository.updateReading(reading);

        Reading updated = ReadingRepository.getReading(id);
        assertEquals("Updated comment", updated.getComment());
    }

    @Test
    @Order(3)
    void testDeleteReading_success() throws SQLException {
        CreateableReading reading = makeReading();
        UUID id = readingService.createReading(reading);

        boolean deleted = ReadingRepository.deleteReading(id);
        assertTrue(deleted);

        Reading result = ReadingRepository.getReading(id);
        assertNull(result);
    }

    @Test
    @Order(4)
    void testGetReadingsFiltered_byCustomer() throws SQLException {
        CreateableReading reading = makeReading();
        UUID id = readingService.createReading(reading);

        List<Reading> filtered = ReadingRepository.getReadingsFiltered(testCustomer.getId(), null, null, null);
        assertTrue(filtered.stream().anyMatch(r -> r.getId().equals(id)));
    }

    @Test
    @Order(5)
    void testGetReadingsWithNullCustomer() throws SQLException {
        UUID id = UUID.randomUUID();
        String query = """
        INSERT INTO readings (id, meterCount, dateOfReading, customerId, kindOfMeter, substitute, comment, meterId)
        VALUES (?, ?, ?, NULL, ?, ?, ?, ?)
    """;

        List<String> params = List.of(
                id.toString(),
                "100.0",
                LocalDate.now().toString(),
                IReading.KindOfMeter.STROM.toString(),
                "0",
                "Orphaned reading",
                "ORPH-METER"
        );

        MySQL.executeStatement(query, params);

        List<Reading> result = ReadingRepository.getReadingsWithNullCustomer();
        assertTrue(result.stream().anyMatch(r -> r.getId().equals(id)));
    }


    @AfterEach
    void cleanUp() throws SQLException {
        MySQL.executeStatement("DELETE FROM readings;", null);
    }

    @AfterAll
    static void disconnect() {
        MySQL.disconnect();
    }
}
