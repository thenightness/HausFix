package readings;

import customers.CustomerRepository;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exceptions.CustomerNotFoundException;
import exceptions.ReadingNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.ResponseUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

public class ReadingController {
    private final ReadingService readingService;

    // Konstruktor
    public ReadingController(HttpServer server, ReadingService readingService) {
        this.readingService = readingService;

        // Endpoints registrieren
        server.createContext("/reading/create", exchange -> handleRequest(exchange, this::handleCreateReading));
        server.createContext("/reading/select", exchange -> handleRequest(exchange, this::handleGetReading));
        server.createContext("/reading/selectAll", exchange -> handleRequest(exchange, this::handleGetAllReadings));
        server.createContext("/reading/update", exchange -> handleRequest(exchange, this::handleUpdateReading));
        server.createContext("/reading/delete", exchange -> handleRequest(exchange, this::handleDeleteReading));
        server.createContext("/readings", exchange -> handleRequest(exchange, this::handleGetFilteredReadings));
    }

    @FunctionalInterface
    public interface ControllerAction {
        void execute(HttpExchange exchange) throws Exception;
    }
    private void handleRequest(HttpExchange exchange, ReadingController.ControllerAction action) throws IOException {
        try {
            action.execute(exchange);
        } catch (CustomerNotFoundException e) {
            ResponseUtil.sendResponse(exchange, 404, "Customer not found.");
        } catch (ReadingNotFoundException e) {
            ResponseUtil.sendResponse(exchange, 404, "Reading not found.");
        } catch (DateTimeParseException e) {
            ResponseUtil.sendResponse(exchange, 400, "Invalid date format. Use yyyy-MM-dd.");
        } catch (Exception e) {
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

    // Filtered: GET /readings
    public void handleGetFilteredReadings(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            ResponseUtil.sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            String query = exchange.getRequestURI().getQuery();
            JSONObject params = parseQueryParams(query);

            String customerUuid = params.optString("customer", null);
            String startDate = params.optString("start", null);
            String endDate = params.optString("end", null);
            String kindOfMeter = params.optString("kindOfMeter", null);

            LocalDate start = startDate != null ? parseDate(startDate) : null;
            LocalDate end = endDate != null ? parseDate(endDate) : LocalDate.now();

            List<Reading> readings = readingService.getFilteredReadings(customerUuid, start, end, kindOfMeter);

            JSONArray readingsArray = new JSONArray();
            for (Reading reading : readings) {
                readingsArray.put(convertToJson(reading));
            }

            JSONObject responseJson = new JSONObject();
            responseJson.put("readings", readingsArray);

            ResponseUtil.sendResponse(exchange, 200, responseJson.toString());
        } catch (DateTimeParseException e) {
            ResponseUtil.sendResponse(exchange, 400, "Invalid date format. Use yyyy-MM-dd.");
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private JSONObject parseQueryParams(String query) {
        JSONObject params = new JSONObject();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }

    private LocalDate parseDate(String date) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

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

}
