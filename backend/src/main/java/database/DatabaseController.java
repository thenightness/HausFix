package database;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import readings.Reading;

import java.io.IOException;

import static util.ResponseUtil.sendResponse;
@Path("setupDB")
public class DatabaseController {

    @DELETE
    public String handleSetupDB() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.removeAllTables();
        databaseConnection.createAllTables();
        return "DB erfolgreich zurückgesetzt!";
    }
}
