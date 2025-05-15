package readings;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import customers.Customer;
import customers.CustomerRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

public class CustomerDeserializer extends JsonDeserializer<Customer> {
    @Override
    public Customer deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        // 1️⃣ Kunde als UUID-String
        if (node.isTextual()) {
            try {
                UUID uuid = UUID.fromString(node.asText());
                return (Customer) CustomerRepository.getCustomer(uuid);
            } catch (Exception e) {
                throw new JsonParseException(jsonParser, "Ungültige UUID für Customer", e);
            }
        }

        // 2️⃣ Kunde als Objekt mit Daten
        if (node.isObject()) {
            Customer customer = new Customer();

            if (node.has("uuid") && !node.get("uuid").isNull()) {
                customer.setId(UUID.fromString(node.get("uuid").asText()));
            } else {
                customer.setId(UUID.randomUUID()); // 🟢 automatisch eine neue ID setzen
            }

            customer.setFirstName(node.get("firstName").asText());
            customer.setLastName(node.get("lastName").asText());
            try {
                customer.setGender(Customer.Gender.valueOf(node.get("gender").asText()));
            } catch (IllegalArgumentException e) {
                throw new JsonParseException(jsonParser, "Ungültiges Geschlecht: " + node.get("gender").asText(), e);
            }

            if (node.has("birthDate") && !node.get("birthDate").isNull()) {
                customer.setBirthDate(LocalDate.parse(node.get("birthDate").asText()));
            }

            return customer;
        }

        throw new JsonParseException(jsonParser, "Customer muss entweder eine UUID oder ein Objekt sein", jsonParser.getCurrentLocation());
    }
}
