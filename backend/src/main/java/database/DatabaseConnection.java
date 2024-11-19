package database;

import modules.IDatabaseConnection;

import java.sql.SQLException;
import java.sql.SQLOutput;

public class DatabaseConnection implements IDatabaseConnection {
    @Override
    public void createAllTables() throws SQLException {
        MySQL.executeStatement("CREATE DATABASE IF NOT EXISTS hausfix;", null);
        MySQL.executeStatement("USE hausfix;", null);
        MySQL.executeStatement("CREATE TABLE IF NOT EXISTS customers (id CHAR(36) NOT NULL PRIMARY KEY, firstName VARCHAR(255) NOT NULL, lastName VARCHAR(255) NOT NULL, birthDate DATE NOT NULL, gender ENUM('M', 'W', 'D', 'U') NOT NULL);", null);
        MySQL.executeStatement("CREATE TABLE IF NOT EXISTS readings (id CHAR(36) NOT NULL PRIMARY KEY, customerId CHAR(36), meterId VARCHAR(255) NOT NULL, meterCount DOUBLE NOT NULL, dateOfReading DATE NOT NULL, kindOfMeter ENUM('HEIZUNG', 'STROM', 'WASSER', 'UNBEKANNT') NOT NULL, comment TEXT, substitute BOOLEAN DEFAULT FALSE, FOREIGN KEY (customerId) REFERENCES customers(id) ON DELETE SET NULL);", null);
        if (MySQL.isConnected()) {
            System.out.println("Tabellen erfolgreich erstellt");
        } else {
            System.out.println("Fehler beim Erstellen der Tabellen; Keine Connection vorhanden");
        }

    }

    @Override
    public void truncateTables() {
        try {
            MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 0;", null);
            MySQL.executeStatement("TRUNCATE TABLE readings;", null);
            MySQL.executeStatement("TRUNCATE TABLE customers;", null);
            MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 1;", null);
            System.out.println("Tabellen erfolgreich trunciert.");
        } catch (SQLException e) {
            System.err.println("Fehler beim truncieren der Tabellen: " + e.getMessage());
        }
    }

    @Override
    public void removeAllTables() {
        try {
            MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 0;", null);
            MySQL.executeStatement("DROP TABLE IF EXISTS readings;", null);
            MySQL.executeStatement("DROP TABLE IF EXISTS customers;", null);
            MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 1;", null);
            System.out.println("Tabellen erfolgreich entfernt.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Entfernen der Tabellen: " + e.getMessage());
        }

    }

    @Override
    public void closeConnection() {
        MySQL.disconnect();
    }
}