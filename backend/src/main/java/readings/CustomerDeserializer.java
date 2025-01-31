package readings;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import customers.CustomerRepository;
import modules.ICustomer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class CustomerDeserializer extends JsonDeserializer<ICustomer> {
    @Override
    public ICustomer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        UUID uuid = jsonParser.readValueAs(UUID.class);
        try {
            return CustomerRepository.getCustomer(uuid);
        } catch (SQLException e) {
            throw new JsonParseException(jsonParser, "Fehler bei Umwandeln der ICustomer ID zu einem ICustomer", e);
        }
    }
}
