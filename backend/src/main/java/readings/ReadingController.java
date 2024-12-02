package readings;

import customers.CustomerService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ReadingController {
    private final ReadingService readingService;

    // Konstruktor
    public ReadingController(HttpServer server, ReadingService readingService) {
        this.readingService = readingService;

        // Endpoints registrieren
        server.createContext("/reading/create", this::handleCreateReading);
        server.createContext("/reading/select", this::handleGetReading);
        server.createContext("/reading/selectAll", this::handleGetAllReadings);
        server.createContext("/reading/update", this::handleUpdateReading);
        server.createContext("/reading/delete", this::handleDeleteReading);
    }

    // GET /reading/select
    private void handleGetReading(HttpExchange exchange) throws IOException {}

    // GET /reading/selectAll
    private void handleGetAllReadings(HttpExchange exchange) throws IOException {}

    // POST /reading/create
    private void handleCreateReading(HttpExchange exchange) throws IOException {}

    // PUT /reading/update
    private void handleUpdateReading(HttpExchange exchange) throws IOException {}

    // DELETE /reading/delete
    private void handleDeleteReading(HttpExchange exchange) {}

    // Hilfsmethode: JSONObject zu Reading konvertieren
    private Reading convertToReading(JSONObject json) {
        Reading reading = new Reading();
        CustomerService customer = new CustomerService();
        reading.setId(UUID.fromString(json.getString("id")));
        reading.setComment(json.getString("comment"));
        reading.setCustomer(customer.getCustomer(UUID.fromString(json.getString("customerId"))));
        reading.setDateOfReading(LocalDate.parse(json.getString("birthDate")));
        reading.setKindOfMeter(Reading.KindOfMeter.valueOf(json.getString("kindOfMeter").toUpperCase()));
        reading.setMeterCount(json.getDouble("meterCount"));
        reading.setMeterId(json.getString("meterId"));
        reading.setSubstitute(json.getBoolean("substitute"));
        return reading;
    }

    // Hilfsmethode: HTTP-Antwort senden
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
