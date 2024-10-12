package database;

import modules.IDatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection implements IDatabaseConnection {
    private Connection connection;

    private String url;
    private String username;
    private String password;

    public DatabaseConnection(){}

    @Override
    public IDatabaseConnection openConnection(Properties properties) {
        String whoami = System.getProperty("user.name");
        this.url = properties.getProperty(whoami + ".db.url");
        this.username = properties.getProperty(whoami + ".db.user");
        this.password = properties.getProperty(whoami + ".db.pw");

        this.connect();
        return this;
    }

    private void connect() {
        try {
            if(connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void createAllTables() {

    }

    @Override
    public void truncateTables() {

    }

    @Override
    public void removeAllTables() {

    }

    @Override
    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
