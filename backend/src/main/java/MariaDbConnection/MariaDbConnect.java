package MariaDbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDbConnect {
    private static final String URL = "jdbc:mariadb://localhost:3306/mariadb-HausFix";
    private static final String USER = "dylaan7";
    private static final String PASSWORD = "S8nu9";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
