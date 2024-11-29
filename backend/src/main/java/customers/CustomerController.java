package customers;

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
    private void handleGetCustomer(HttpExchange exchange) throws IOException {
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

            // Get Customer by ID
            Customer customer = customerService.getCustomer(id);

            JSONObject customerJson = new JSONObject();
            customerJson.put("id", customer.getId().toString());
            customerJson.put("firstName", customer.getFirstName());
            customerJson.put("lastName", customer.getLastName());
            customerJson.put("birthDate", customer.getBirthDate().toString());
            customerJson.put("gender", customer.getGender().toString());

            //Respond
            sendResponse(exchange, 200, customerJson.toString());

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
            // Lese den Request-Body und konvertiere in JSONObject
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            JSONObject json = new JSONObject(requestBody);

            String id = json.getString("id");

            if (!id.matches("^[0-9a-fA-F-]{36}$")) {
                sendResponse(exchange, 400, "Invalid UUID format");
                return;
            }
            String responseMessage = customerService.deleteCustomer(id);

            sendResponse(exchange, 200, responseMessage);

        } catch (JSONException e) {
            e.printStackTrace();
            sendResponse(exchange, 400, "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
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
