package database;

import modules.IDatabaseConnection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection implements IDatabaseConnection {
    @Override
    public void createAllTables() {
        MySQL.executeStatement("CREATE TABLE IF NOT EXISTS customers.sql (id CHAR(36) NOT NULL PRIMARY KEY, firstName VARCHAR(255) NOT NULL, lastName VARCHAR(255) NOT NULL, birthDate DATE NOT NULL, gender ENUM('M', 'W', 'D', 'U') NOT NULL);", null);
        MySQL.executeStatement("CREATE TABLE IF NOT EXISTS readings (id CHAR(36) NOT NULL PRIMARY KEY, customerId CHAR(36), meterId VARCHAR(255) NOT NULL, meterCount DOUBLE NOT NULL, dateOfReading DATE NOT NULL, kindOfMeter ENUM('HEIZUNG', 'STROM', 'WASSER', 'UNBEKANNT') NOT NULL, comment TEXT, substitute BOOLEAN DEFAULT FALSE, FOREIGN KEY (customerId) REFERENCES customers.sql(id) ON DELETE SET NULL);", null);
        System.out.println("Tabellen erfolgreich erstellt!");
    }

    @Override
    public void truncateTables() {
        MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 0;", null);
        MySQL.executeStatement("TRUNCATE TABLE customers.sql;", null);
        MySQL.executeStatement("TRUNCATE TABLE readings;", null);
        MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 1;", null);
        System.out.println("Tabellen erfolgreich geleert!");
    }

    @Override
    public void removeAllTables() {
        MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 0;", null);
        MySQL.executeStatement("DROP TABLE IF EXISTS customers.sql;", null);
        MySQL.executeStatement("DROP TABLE IF EXISTS readings;", null);
        MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 1;", null);
        System.out.println("Tabellen erfolgreich gelöscht!");
    }

    @Override
    public void closeConnection() {
        MySQL.disconnect();
    }


    // Ausführen von SQL-Befehlen aus einer Datei
    public void executeSqlFile(String relativePath) {
        try {
            // Lade die SQL-Datei über den Klassenpfad
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(relativePath);
            if (inputStream == null) {
                throw new RuntimeException("SQL-Datei nicht gefunden: " + relativePath);
            }

            // Lies den Inhalt der Datei
            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // Führe die SQL-Befehle aus
            Connection connection = MySQL.getInstance().getConnection();
            Statement statement = connection.createStatement();
            statement.execute(sql);

            System.out.println("SQL-Datei erfolgreich ausgeführt: " + relativePath);
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der SQL-Datei: " + relativePath + " - " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL-Fehler: " + e.getMessage());
        }
    }

}