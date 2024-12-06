package readings;

import customers.Customer;
import customers.CustomerRepository;
import customers.CustomerService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
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
  private void handleGetReading(HttpExchange exchange) throws IOException {
      if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
          sendResponse(exchange, 405, "Method Not Allowed");
          return;
      }
      try {
          //get requestBody
          String requestBody = new String(exchange.getRequestBody().readAllBytes());

          //convert to JSON
          JSONObject json = new JSONObject(requestBody);

          //get UUID from JSON
          UUID id = UUID.fromString(json.getString("id"));

          // Get Reading by ID
          Reading reading = readingService.getReading(id);

          JSONObject readingJson = new JSONObject();
          readingJson.put("id", reading.getId().toString());
          readingJson.put("customerId", reading.getCustomer().toString());
          readingJson.put("meterId", reading.getMeterId());
          readingJson.put("meterCount", reading.getKindOfMeter().toString());
          readingJson.put("dateOfReading", reading.getDateOfReading().toString());
          readingJson.put("KindOfMeter", reading.getKindOfMeter().toString());
          readingJson.put("comment", reading.getComment());
          readingJson.put("substitute", reading.getSubstitute().toString());

          //Respond
          sendResponse(exchange, 200, readingJson.toString());

      } catch (Exception e) {
          e.printStackTrace();
          sendResponse(exchange, 500, "Interner Serverfehler: " + e.getMessage());
      }
  }

    private void handleGetAllReadings(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            // Rufe alle Readings aus dem Service ab
            List<Reading> readings = readingService.getAllReadings();

            // Konvertiere die Reading-Liste in JSON
            JSONArray jsonResponse = new JSONArray();
            for (Reading reading : readings) {
                jsonResponse.put(convertToJson(reading));
            }

            // Sende die JSON-Antwort zurück
            sendResponse(exchange, 200, jsonResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    // Hilfsmethode: Reading in JSON konvertieren
    private JSONObject convertToJson(Reading reading) {
        JSONObject readingJson = new JSONObject();
        try {
            readingJson.put("id", reading.getId().toString());
            readingJson.put("customerId", reading.getCustomer().getId().toString());
            readingJson.put("meterId", reading.getMeterId());
            readingJson.put("meterCount", reading.getMeterCount());
            readingJson.put("dateOfReading", reading.getDateOfReading().toString());
            readingJson.put("kindOfMeter", reading.getKindOfMeter().toString());
            readingJson.put("comment", reading.getComment() != null ? reading.getComment() : "");
            readingJson.put("substitute", reading.getSubstitute());
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to convert Reading to JSON", e);
        }
        return readingJson;
    }



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
        if (!"PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
        try {
            // Lese den Request-Body und konvertiere in JSONObject
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            JSONObject json = new JSONObject(requestBody);
            // Convert JSON to Reading
            Reading reading = convertToReading(json);
            //Übergabe an Reading Service
            String responseMessage = readingService.updateReading(reading);
            //Erfolgreiche Antwort
            sendResponse(exchange, 200, responseMessage);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Interner Serverfehler: " + e.getMessage());
        }
    }

    // DELETE /reading/delete
    private void handleDeleteReading(HttpExchange exchange) throws IOException {
        if (!"DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }
        try {
            // Lese den Request-Body und konvertiere in JSONObject
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            JSONObject json = new JSONObject(requestBody);

            String id = json.getString("id");

            if (!id.matches("^[0-9a-fA-F-]{36}$")) {
                sendResponse(exchange, 400, "Invalid UUID format");
                return;
            }
            String responseMessage = readingService.deleteReading(id);

            sendResponse(exchange, 200, responseMessage);

        } catch (JSONException e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    // Hilfsmethode: JSONObject zu Reading konvertieren
    private Reading convertToReading(JSONObject json) throws SQLException {
        Reading reading = new Reading();
        reading.setId(UUID.fromString(json.getString("id")));
        reading.setComment(json.getString("comment"));
        reading.setCustomer(CustomerRepository.getCustomer(UUID.fromString(json.getString("customerId"))));
        reading.setDateOfReading(LocalDate.parse(json.getString("dateOfReading")));
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
