package readings;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import customers.Customer;
import customers.CustomerRepository;
import database.DatabaseConnection;
import database.MySQL;
import modules.ICustomer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerDeserializerTest {

    private static ObjectMapper mapper;
    private static Customer existingCustomer;

    @BeforeAll
    static void setupDatabase() throws Exception {
        MySQL.init("mariadb", 3306, System.getenv("MYSQL_DATABASE"), System.getenv("MYSQL_USER"), System.getenv("MYSQL_PASSWORD"));
        new DatabaseConnection().createAllTables();

        existingCustomer = new Customer();
        existingCustomer.setId(UUID.randomUUID());
        existingCustomer.setFirstName("Real");
        existingCustomer.setLastName("User");
        existingCustomer.setGender(ICustomer.Gender.M);
        existingCustomer.setBirthDate(LocalDate.of(2000, 1, 1));
        CustomerRepository.createCustomer(existingCustomer);

        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Customer.class, new CustomerDeserializer());
        mapper.registerModule(module);
    }

    @AfterAll
    static void cleanup() throws Exception {
        MySQL.executeStatement("DELETE FROM customers;", null);
    }

    @Test
    @Order(1)
    void testDeserializeFromValidUuid() throws IOException {
        String json = "\"" + existingCustomer.getId() + "\"";

        Customer result = mapper.readValue(json, Customer.class);

        assertNotNull(result);
        assertEquals(existingCustomer.getId(), result.getId());
        assertEquals("Real", result.getFirstName());
    }

    @Test
    @Order(2)
    void testDeserializeFromInvalidUuidString_throwsParseException() {
        String json = "\"not-a-uuid\"";

        JsonParseException ex = assertThrows(JsonParseException.class, () ->
                mapper.readValue(json, Customer.class)
        );

        assertTrue(ex.getMessage().contains("Ungültige UUID"));
    }

    @Test
    @Order(3)
    void testDeserializeFromObjectWithAllFields() throws IOException {
        String json = """
        {
          "uuid": "11111111-1111-1111-1111-111111111111",
          "firstName": "Alice",
          "lastName": "Smith",
          "gender": "W",
          "birthDate": "1999-12-31"
        }
        """;

        Customer result = mapper.readValue(json, Customer.class);

        assertEquals("Alice", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals(Customer.Gender.W, result.getGender());
        assertEquals(LocalDate.of(1999, 12, 31), result.getBirthDate());
    }

    @Test
    @Order(4)
    void testDeserializeWithMissingUuid_generatesId() throws IOException {
        String json = """
        {
          "firstName": "NoID",
          "lastName": "Customer",
          "gender": "M",
          "birthDate": "2001-06-01"
        }
        """;

        Customer result = mapper.readValue(json, Customer.class);

        assertNotNull(result.getId());
        assertEquals("NoID", result.getFirstName());
    }

    @Test
    @Order(5)
    void testDeserializeWithUuidExplicitNull_generatesId() throws IOException {
        String json = """
        {
          "uuid": null,
          "firstName": "NullUUID",
          "lastName": "Field",
          "gender": "M",
          "birthDate": "1990-01-01"
        }
        """;

        Customer result = mapper.readValue(json, Customer.class);

        assertNotNull(result.getId());
        assertEquals("NullUUID", result.getFirstName());
    }

    @Test
    @Order(6)
    void testDeserializeWithInvalidGender_throwsJsonParseException() {
        String json = """
        {
          "uuid": "22222222-2222-2222-2222-222222222222",
          "firstName": "Wrong",
          "lastName": "Gender",
          "gender": "X",
          "birthDate": "2000-01-01"
        }
        """;

        JsonParseException ex = assertThrows(JsonParseException.class, () ->
                mapper.readValue(json, Customer.class)
        );

        assertTrue(ex.getMessage().contains("Ungültiges Geschlecht"));
    }

    @Test
    @Order(7)
    void testDeserializeWithMissingBirthDate_isNull() throws IOException {
        String json = """
        {
          "uuid": "33333333-3333-3333-3333-333333333333",
          "firstName": "NoBirth",
          "lastName": "Field",
          "gender": "M"
        }
        """;

        Customer result = mapper.readValue(json, Customer.class);

        assertNotNull(result);
        assertNull(result.getBirthDate());
    }

    @Test
    @Order(8)
    void testDeserializeWithNullBirthDate_isNull() throws IOException {
        String json = """
        {
          "uuid": "44444444-4444-4444-4444-444444444444",
          "firstName": "Null",
          "lastName": "Date",
          "gender": "W",
          "birthDate": null
        }
        """;

        Customer result = mapper.readValue(json, Customer.class);

        assertNotNull(result);
        assertEquals("Null", result.getFirstName());
        assertNull(result.getBirthDate());
    }

    @Test
    @Order(9)
    void testDeserializeWithInvalidBirthDateFormat_throwsException() {
        String json = """
        {
          "uuid": "55555555-5555-5555-5555-555555555555",
          "firstName": "Bad",
          "lastName": "Date",
          "gender": "M",
          "birthDate": "31-12-1999"
        }
        """;

        Exception ex = assertThrows(Exception.class, () ->
                mapper.readValue(json, Customer.class)
        );

        assertTrue(ex.getMessage().contains("could not be parsed"));
    }

    @Test
    @Order(10)
    void testDeserializeJsonIsArray_throwsJsonParseException() {
        String json = "[\"something\"]";

        JsonParseException ex = assertThrows(JsonParseException.class, () ->
                mapper.readValue(json, Customer.class)
        );

        assertTrue(ex.getMessage().contains("Customer muss entweder eine UUID oder ein Objekt sein"));
    }

    @Test
    @Order(11)
    void testDeserializeJsonIsNumber_throwsJsonParseException() {
        String json = "42";

        JsonParseException ex = assertThrows(JsonParseException.class, () ->
                mapper.readValue(json, Customer.class)
        );

        assertTrue(ex.getMessage().contains("Customer muss entweder eine UUID oder ein Objekt sein"));
    }

    @Test
    @Order(12)
    void testDeserializeJsonIsNull_returnsNull() throws IOException {
        String json = "null";

        Customer result = mapper.readValue(json, Customer.class);
        assertNull(result);
    }
}
