package readings;

import customers.CustomerRepository;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exceptions.ReadingNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.ResponseUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ReadingControllerss {
    private final ReadingService readingService;

    // Konstruktor
    public ReadingControllerss(HttpServer server, ReadingService readingService) {
        this.readingService = readingService;

        // Endpoints registrieren
        server.createContext("/reading/create", this::handleCreateReading);
        server.createContext("/reading/select", this::handleGetReading);
        server.createContext("/reading/selectAll", this::handleGetAllReadings);
        server.createContext("/reading/update", this::handleUpdateReading);
        server.createContext("/reading/delete", this::handleDeleteReading);
    }

    @FunctionalInterface
    public interface ControllerAction {
        void execute(HttpExchange exchange) throws Exception;
    }
    private void handleRequest(HttpExchange exchange, ReadingControllerss.ControllerAction action) throws IOException {
        try {
            action.execute(exchange);
        } catch (JSONException e) {
            // 400 Bad Request falls JSON-Format fehlerhaft
            ResponseUtil.sendResponse(exchange, 400, "JSON-Format fehlerhaft: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // 400 Bad Request für fehlerhafte Anfragen
            ResponseUtil.sendResponse(exchange, 400, e.getMessage());
        } catch (ReadingNotFoundException e) {
            // 404 Not Found für fehlende Readings
            ResponseUtil.sendResponse(exchange, 404, e.getMessage());
        } catch (Exception e) {
            // 500 Internal Server Error für alles andere
            e.printStackTrace();
            ResponseUtil.sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

  // GET /reading/select
  private void handleGetReading(HttpExchange exchange) throws IOException {
      handleRequest(exchange, ex -> {
          if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
              ResponseUtil.sendResponse(ex, 405, "Methode nicht erlaubt");
              return;
          }

          //uuid vom path holen
          String path = ex.getRequestURI().getPath();
          String[] pathSegments = path.split("/");
          if (pathSegments.length < 4) {
              throw new IllegalArgumentException("Fehlender Parameter: id");
          }

          String id = pathSegments[3];
          UUID readingId = UUID.fromString(id);

          Reading reading = readingService.getReading(readingId);

          JSONObject readingJson = new JSONObject();
          readingJson.put("id", reading.getId().toString());
          readingJson.put("customerId", reading.getCustomer().toString());
          readingJson.put("meterId", reading.getMeterId());
          readingJson.put("meterCount", reading.getMeterCount().toString());
          readingJson.put("dateOfReading", reading.getDateOfReading().toString());
          readingJson.put("KindOfMeter", reading.getKindOfMeter().toString());
          readingJson.put("comment", reading.getComment());
          readingJson.put("substitute", reading.getSubstitute().toString());

          //Respond
          ResponseUtil.sendResponse(ex, 200, readingJson.toString());


      });
  }

    private void handleGetAllReadings(HttpExchange exchange) throws IOException {
        handleRequest(exchange, ex -> {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                ResponseUtil.sendResponse(ex, 405, "Methode nicht erlaubt");
                return;
            }

            List<Reading> readings = readingService.getAllReadings();

            JSONArray jsonResponse = new JSONArray();
            for (Reading reading : readings) {
                JSONObject readingJson = new JSONObject();
                readingJson.put("id", reading.getId().toString());
                readingJson.put("customerId", reading.getCustomer().getId().toString());
                readingJson.put("meterId", reading.getMeterId());
                readingJson.put("meterCount", reading.getMeterCount());
                readingJson.put("dateOfReading", reading.getDateOfReading().toString());
                readingJson.put("kindOfMeter", reading.getKindOfMeter().toString());
                readingJson.put("comment", reading.getComment() != null ? reading.getComment() : "");
                readingJson.put("substitute", reading.getSubstitute() != null ? reading.getSubstitute().toString() : "");

                jsonResponse.put(readingJson);
            }


            ResponseUtil.sendResponse(ex, 200, jsonResponse.toString());
        });
    }


    // POST /reading/create
 private void handleCreateReading(HttpExchange exchange) throws IOException {
     handleRequest(exchange, ex -> {
         if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
             ResponseUtil.sendResponse(ex, 405, "Methode nicht erlaubt");
             return;
         }

         String requestBody = new String(ex.getRequestBody().readAllBytes());
         JSONObject json = new JSONObject(requestBody);

         Reading reading = convertToReading(json);

         String responseMessage = readingService.createReading(reading);

         ResponseUtil.sendResponse(ex, 201, responseMessage);


     });
 }

    // PUT /reading/update
    private void handleUpdateReading(HttpExchange exchange) throws IOException {
        handleRequest(exchange, ex -> {
            if (!"PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Methode nicht erlaubt");
            }
            String responseMessage = null;

            String requestBody = new String(ex.getRequestBody().readAllBytes());
            JSONObject json = new JSONObject(requestBody);

            Reading reading = convertToReading(json);

            responseMessage = readingService.updateReading(reading);

            ResponseUtil.sendResponse(ex, 200, responseMessage);

        });
    }
    // DELETE /reading/delete
    private void handleDeleteReading(HttpExchange exchange) throws IOException {
        handleRequest(exchange, ex -> {
            if (!"DELETE".equalsIgnoreCase(ex.getRequestMethod())) {
                ResponseUtil.sendResponse(ex, 405, "Methode nicht erlaubt");
                return;
            }

            String path = ex.getRequestURI().getPath();
            String[] pathSegments = path.split("/");
            if (pathSegments.length < 4) {
                throw new IllegalArgumentException("Fehlender Parameter: id");
            }

            String id = pathSegments[3];
            UUID readingId = UUID.fromString(id);

            String responseMessage = readingService.deleteReading(readingId.toString());

            ResponseUtil.sendResponse(ex, 200, responseMessage);
        });
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
