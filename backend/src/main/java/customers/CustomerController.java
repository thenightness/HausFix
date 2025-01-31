package customers;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class CustomerController {
    private final CustomerService customerService;

    // Konstruktor
    public CustomerController(HttpServer server, CustomerService customerService) {
        this.customerService = customerService;

        // Endpoints registrieren
        server.createContext("/customer/create", this::handleCreateCustomer);
        server.createContext("/customer/select", this::handleGetCustomer);
        server.createContext("/customer/selectAll", this::handleGetAllCustomers);
        server.createContext("/customer/update", this::handleUpdateCustomer);
        server.createContext("/customer/delete", this::handleDeleteCustomer);
    }

    // GET /customer/select
    void handleGetCustomer(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            // Extrahiere die ID aus der Query
            String query = exchange.getRequestURI().getQuery();
            if (query == null || !query.contains("id=")) {
                sendResponse(exchange, 400, "Fehlender Parameter: id");
                return;
            }

            String id = query.split("id=")[1];
            UUID customerId = UUID.fromString(id);

            // Rufe den User über den Service ab
            Customer customer = customerService.getCustomer(customerId);
            if (customer == null) {
                sendResponse(exchange, 404, "Kunde nicht gefunden");
                return;
            }

            // Userdaten in JSON konvertieren
            JSONObject customerJson = new JSONObject();
            customerJson.put("id", customer.getId().toString());
            customerJson.put("firstName", customer.getFirstName());
            customerJson.put("lastName", customer.getLastName());
            customerJson.put("birthDate", customer.getBirthDate().toString());
            customerJson.put("gender", customer.getGender().name());

            CustomerWrapper customerWrapper = new CustomerWrapper();
            customerWrapper.setCustomer(customer);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            String jsonOutput = objectMapper.writeValueAsString(customerWrapper);

            System.out.println(customerJson.toString());
            sendResponse(exchange, 200,jsonOutput);
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Ungültige UUID: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Interner Serverfehler: " + e.getMessage());
        }
    }




    // GET /customer/selectAll
    private void handleGetAllCustomers(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }
        try {
            // Rufe alle Kunden aus dem Service ab
            List<Customer> customers = customerService.getAllCustomers();

            // Konvertiere die Kundenliste in JSON
            JSONArray jsonResponse = new JSONArray();
            for (Customer customer : customers) {
                JSONObject customerJson = new JSONObject();
                customerJson.put("id", customer.getId().toString());
                customerJson.put("firstName", customer.getFirstName());
                customerJson.put("lastName", customer.getLastName());
                customerJson.put("birthDate", customer.getBirthDate().toString());
                customerJson.put("gender", customer.getGender().toString());
                jsonResponse.put(customerJson);
            }

            // Sende die JSON-Antwort zurück
            sendResponse(exchange, 200, jsonResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Interner Serverfehler: " + e.getMessage());
        }
    }

    // POST /customer/create
    private void handleCreateCustomer(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }
        try {
            // Lese den Request-Body und konvertiere in JSONObject
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            JSONObject json = new JSONObject(requestBody);

            // Konvertiere das JSONObject zu einem Customer-Objekt
            Customer customer = convertToCustomer(json);

            // Übergabe an den CustomerService
            String responseMessage = customerService.createCustomer(customer);

            // Erfolgreiche Antwort
            sendResponse(exchange, 201, responseMessage);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Interner Serverfehler: " + e.getMessage());
        }
    }

    // PUT /customer/update
    private void handleUpdateCustomer(HttpExchange exchange) throws IOException {
        if (!"PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
        try {
            // Lese den Request-Body und konvertiere in JSONObject
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            JSONObject json = new JSONObject(requestBody);

            // Konvertiere das JSONObject zu einem Customer-Objekt
            Customer customer = convertToCustomer(json);

            // Übergabe an den CustomerService
            String responseMessage = customerService.updateCustomer(customer);

            // Erfolgreiche Antwort
            sendResponse(exchange, 200, responseMessage);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Interner Serverfehler: " + e.getMessage());
        }
    }

    // DELETE /customer/delete
    private void handleDeleteCustomer(HttpExchange exchange) throws IOException {
        if (!"DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            // Lese die ID aus den Query-Parametern
            String query = exchange.getRequestURI().getQuery();
            if (query == null || !query.contains("id=")) {
                sendResponse(exchange, 400, "Fehlender Parameter: id");
                return;
            }

            String id = query.split("id=")[1];

            // Konvertiere den String in eine UUID
            UUID customerId = UUID.fromString(id);

            // Übergabe an den CustomerService
            String responseMessage = customerService.deleteCustomer(String.valueOf(customerId));

            // Erfolgreiche Antwort
            sendResponse(exchange, 200, responseMessage);
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Ungültige UUID: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Interner Serverfehler: " + e.getMessage());
        }
    }


    // Hilfsmethode: JSONObject zu Customer konvertieren
    private Customer convertToCustomer(JSONObject json) {
        Customer customer = new Customer();
        customer.setId(UUID.fromString(json.getString("id")));
        customer.setFirstName(json.getString("firstName"));
        customer.setLastName(json.getString("lastName"));
        customer.setBirthDate(LocalDate.parse(json.getString("birthDate"))); // Konvertiere String zu LocalDate
        customer.setGender(Customer.Gender.valueOf(json.getString("gender").toUpperCase())); // Konvertiere String zu Enum
        return customer;
    }

    // Hilfsmethode: HTTP-Antwort senden
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
