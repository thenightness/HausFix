package readings;

import customers.CustomerRepository;
import customers.CustomerService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class ReadingController {
    private final ReadingService readingService;

    // Konstruktor
    public ReadingController(HttpServer server, ReadingService readingService) {
        this.readingService = readingService;

        // Endpoints registrieren
        server.createContext("/reading/create", this::handleCreateReading);
        //server.createContext("/reading/select", this::handleGetReading);
        //server.createContext("/reading/selectAll", this::handleGetAllReadings);
        server.createContext("/reading/update", this::handleUpdateReading);
        server.createContext("/reading/delete", this::handleDeleteReading);
    }

//  // GET /reading/select
//  private void handleGetReading(HttpExchange exchange) throws IOException {
//      if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
//          sendResponse(exchange, 405, "Method Not Allowed");
//          return;
//      }
//      try {
//          //get requestBody
//          String requestBody = new String(exchange.getRequestBody().readAllBytes());
//
//          //convert to JSON
//          JSONObject json = new JSONObject(requestBody);
//
//          //get UUID from JSON
//          UUID id = UUID.fromString(json.getString("id"));
//
//          // Get Reading by ID
//          Reading reading = readingService.getReading(id);
//
//          JSONObject readingJson = new JSONObject();
//          readingJson.put("id", reading.getId().toString());
//          readingJson.put("customerId", reading.getCustomer().toString());
//          readingJson.put("meterId", reading.getMeterId());
//          readingJson.put("meterCount", reading.getKindOfMeter().toString());
//          readingJson.put("dateOfReading", reading.getDateOfReading().toString());
//          readingJson.put("KindOfMeter", reading.getKindOfMeter().toString());
//          readingJson.put("comment", reading.getComment());
//          readingJson.put("substitute", reading.getSubstitute().toString());
//
//          //Respond
//          sendResponse(exchange, 200, readingJson.toString());
//
//      } catch (Exception e) {
//          e.printStackTrace();
//          sendResponse(exchange, 500, "Interner Serverfehler: " + e.getMessage());
//      }
//  }

//  // GET /reading/selectAll
//  private void handleGetAllReadings(HttpExchange exchange) throws IOException {
//  }
//
 // POST /reading/create
 private void handleCreateReading(HttpExchange exchange) throws IOException {
     if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
         sendResponse(exchange, 405, "Method Not Allowed");
         return;
     }
     try {
         // Lese den Request-Body und konvertiere in JSONObject
         String requestBody = new String(exchange.getRequestBody().readAllBytes());
         JSONObject json = new JSONObject(requestBody);

         // Konvertiere das JSONObject zu einem Reading-Objekt
         Reading reading = convertToReading(json);

         // Übergabe an den ReadingService
         String responseMessage = readingService.createReading(reading);

         // Erfolgreiche Antwort
         sendResponse(exchange, 201, responseMessage);
     } catch (Exception e) {
         e.printStackTrace();
         sendResponse(exchange, 500, "Interner Serverfehler: " + e.getMessage());
     }
 }

    // PUT /reading/update
    private void handleUpdateReading(HttpExchange exchange) throws IOException {
    }

    // DELETE /reading/delete
    private void handleDeleteReading(HttpExchange exchange) {
    }

    // Hilfsmethode: JSONObject zu Reading konvertieren
    private Reading convertToReading(JSONObject json) throws SQLException {
        Reading reading = new Reading();
        reading.setId(UUID.fromString(json.getString("id")));
        reading.setComment(json.getString("comment"));
        reading.setCustomer(CustomerRepository.getCustomer(UUID.fromString(json.getString("customerId"))));
        reading.setDateOfReading(LocalDate.parse(json.getString("dateOfReading"))); // Corrected the field name.
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
