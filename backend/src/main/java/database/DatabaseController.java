package database;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;

public class DatabaseController {
    // Konstruktor
    public DatabaseController(HttpServer server) {
        // Endpoint registrieren
        server.createContext("/setupDB", this::handleSetupDB);
    }

    // DELETE /setupDB
    private void handleSetupDB(HttpExchange exchange) throws IOException {
        if (!"DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            databaseConnection.removeAllTables();
            databaseConnection.createAllTables();

            // Erfolgreiche Antwort
            sendResponse(exchange, 200, "DB erfolgreich zurückgesetzt!");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Interner Serverfehler: " + e.getMessage());
        }
    }
    // Hilfsmethode: HTTP-Antwort senden
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
