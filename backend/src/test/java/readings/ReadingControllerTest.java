package readings;

import customers.Customer;
import customers.CustomerRepository;
import database.DatabaseConnection;
import database.MySQL;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import modules.ICustomer;
import modules.IReading;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReadingControllerTest extends JerseyTest {

    private static Customer testCustomer;

    @Override
    protected Application configure() {
        ReadingController.readingService = new ReadingService();
        return new ResourceConfig(ReadingController.class);
    }

    @BeforeAll
    static void setupDatabase() throws Exception {
        MySQL.init("mariadb", 3306, System.getenv("MYSQL_DATABASE"), System.getenv("MYSQL_USER"), System.getenv("MYSQL_PASSWORD"));
        new DatabaseConnection().createAllTables();

        testCustomer = new Customer();
        testCustomer.setId(UUID.randomUUID());
        testCustomer.setFirstName("Filter");
        testCustomer.setLastName("Test");
        testCustomer.setBirthDate(LocalDate.of(1990, 1, 1));
        testCustomer.setGender(ICustomer.Gender.M);
        CustomerRepository.createCustomer(testCustomer);
    }

    @BeforeEach
    void insertReading() {
        Reading reading = new Reading();
        reading.setCustomer(testCustomer);
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(IReading.KindOfMeter.STROM);
        reading.setMeterCount(222.22);
        reading.setMeterId("FILT-METER");
        reading.setSubstitute(false);
        reading.setComment("Filtered reading");

        ReadingController.readingService.createReading(reading);
    }

    @AfterEach
    void cleanup() throws Exception {
        MySQL.executeStatement("DELETE FROM readings;", null);
    }

    @Test
    @Order(1)
    void testFilterWithAllParams() {
        WebTarget target = target("readings")
                .queryParam("customer", testCustomer.getId())
                .queryParam("start", LocalDate.now().minusDays(1))
                .queryParam("end", LocalDate.now().plusDays(1))
                .queryParam("kindOfMeter", "STROM");

        Response response = target.request().get();

        assertEquals(200, response.getStatus());
        String body = response.readEntity(String.class);
        assertTrue(body.contains("readings"));
        assertTrue(body.contains("FILT-METER"));
    }

    @Test
    @Order(2)
    void testFilterMissingCustomer_returns400() {
        WebTarget target = target("readings")
                .queryParam("start", "2024-01-01")
                .queryParam("end", "2025-01-01");

        Response response = target.request().get();

        assertEquals(400, response.getStatus());
        String body = response.readEntity(String.class);
        assertTrue(body.contains("Parameter 'customer' ist erforderlich"));
    }

    @Test
    @Order(3)
    void testFilterMissingEndParam_usesToday() {
        WebTarget target = target("readings")
                .queryParam("customer", testCustomer.getId())
                .queryParam("start", LocalDate.now().minusDays(2))
                .queryParam("kindOfMeter", "STROM");

        Response response = target.request().get();

        assertEquals(200, response.getStatus());
        String body = response.readEntity(String.class);
        assertTrue(body.contains("FILT-METER"));
    }

    @Test
    @Order(4)
    void testFilterInvalidKindOfMeter_returns400() {
        WebTarget target = target("readings")
                .queryParam("customer", testCustomer.getId())
                .queryParam("kindOfMeter", "INVALID");

        Response response = target.request().get();

        assertEquals(400, response.getStatus());
        String body = response.readEntity(String.class);
        assertTrue(body.contains("Ungültige Anfrage"));
    }

    @Test
    @Order(5)
    void testFilterInvalidDateFormat_returns400() {
        WebTarget target = target("readings")
                .queryParam("customer", testCustomer.getId())
                .queryParam("start", "not-a-date");

        Response response = target.request().get();

        assertEquals(400, response.getStatus());
        String body = response.readEntity(String.class);
        assertTrue(body.contains("Ungültige Anfrage"));
    }
    @Test
    @Order(6)
    void testFilterOnlyCustomerParam_returnsReadings() {
        WebTarget target = target("readings")
                .queryParam("customer", testCustomer.getId());

        Response response = target.request().get();

        assertEquals(200, response.getStatus());
        String body = response.readEntity(String.class);
        assertTrue(body.contains("readings"));
        assertTrue(body.contains("FILT-METER"));
    }


    @Test
    @Order(8)
    void testFilterWithStartAfterEnd_returnsEmptyReadingsList() {
        WebTarget target = target("readings")
                .queryParam("customer", testCustomer.getId())
                .queryParam("start", LocalDate.now().plusDays(5))
                .queryParam("end", LocalDate.now().minusDays(5));

        Response response = target.request().get();

        assertEquals(200, response.getStatus());
        String body = response.readEntity(String.class);
        assertTrue(body.contains("\"readings\""));
    }
    @Test
    @Order(9)
    void testFilterWithEmptyKindOfMeter_returnsReadings() {
        WebTarget target = target("readings")
                .queryParam("customer", testCustomer.getId())
                .queryParam("kindOfMeter", ""); // key is present, but value is blank

        Response response = target.request().get();

        assertEquals(200, response.getStatus());
        String body = response.readEntity(String.class);
        assertTrue(body.contains("\"readings\""));
    }


}
