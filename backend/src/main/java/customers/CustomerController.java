package customers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exceptions.CustomerNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.ResponseUtil;

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

    @FunctionalInterface
    public interface ControllerAction {
        void execute(HttpExchange exchange) throws Exception;
    }
    private void handleRequest(HttpExchange exchange, ControllerAction action) throws IOException {
        try {
            action.execute(exchange);
        } catch (JSONException e) {
            // 400 Bad Request falls JSON-Format fehlerhaft
            ResponseUtil.sendResponse(exchange, 400, "JSON-Format fehlerhaft: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // 400 Bad Request für fehlerhafte Anfragen
            ResponseUtil.sendResponse(exchange, 400, e.getMessage());
        } catch (CustomerNotFoundException e) {
            // 404 Not Found für fehlende Kunden
            ResponseUtil.sendResponse(exchange, 404, e.getMessage());
        } catch (Exception e) {
            // 500 Internal Server Error für alles andere
            e.printStackTrace();
            ResponseUtil.sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }



    // GET /customer/select
    void handleGetCustomer(HttpExchange exchange) throws IOException {
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
            UUID customerId = UUID.fromString(id);

            Customer customer = customerService.getCustomer(customerId);

            // zum JSON konvertieren
            JSONObject customerJson = new JSONObject();
            customerJson.put("id", customer.getId().toString());
            customerJson.put("firstName", customer.getFirstName());
            customerJson.put("lastName", customer.getLastName());
            customerJson.put("birthDate", customer.getBirthDate().toString());
            customerJson.put("gender", customer.getGender().name());

            ResponseUtil.sendResponse(ex, 200, customerJson.toString());
        });
    }




    // GET /customer/selectAll
    private void handleGetAllCustomers(HttpExchange exchange) throws IOException {
        handleRequest(exchange, ex -> {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                ResponseUtil.sendResponse(ex, 405, "Methode nicht erlaubt");
                return;
            }

            List<Customer> customers = customerService.getAllCustomers();

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

            ResponseUtil.sendResponse(ex, 200, jsonResponse.toString());
        });
    }

    // POST /customer/create
    private void handleCreateCustomer(HttpExchange exchange) throws IOException {
        handleRequest(exchange, ex -> {
            if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
                ResponseUtil.sendResponse(ex, 405, "Methode nicht erlaubt");
                return;
            }

                String requestBody = new String(ex.getRequestBody().readAllBytes());
                JSONObject json = new JSONObject(requestBody);

                Customer customer = convertToCustomer(json);

                String responseMessage = customerService.createCustomer(customer);

                ResponseUtil.sendResponse(ex, 201, responseMessage);


        });
    }



    // PUT /customer/update
    private void handleUpdateCustomer(HttpExchange exchange) throws IOException {
        handleRequest(exchange, ex -> {
            if (!"PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Methode nicht erlaubt");
            }

                String requestBody = new String(ex.getRequestBody().readAllBytes());
                JSONObject json = new JSONObject(requestBody);

                Customer customer = convertToCustomer(json);

                String responseMessage = customerService.updateCustomer(customer);

            ResponseUtil.sendResponse(ex, 200, responseMessage);

        });
}

    // DELETE /customer/delete
    private void handleDeleteCustomer(HttpExchange exchange) throws IOException {
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
            UUID customerId = UUID.fromString(id);

            String responseMessage = customerService.deleteCustomer(customerId.toString());

            ResponseUtil.sendResponse(ex, 200, responseMessage);
        });
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
