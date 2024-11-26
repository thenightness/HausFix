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

        System.out.println("Tabellen erfolgreich erstellt!");

    }

    @Override
    public void truncateTables() {
        MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 0;", null);
        MySQL.executeStatement("TRUNCATE TABLE customers;", null);
        MySQL.executeStatement("TRUNCATE TABLE readings;", null);
        MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 1;", null);
        System.out.println("Tabellen erfolgreich geleert!");
    }

    @Override
    public void removeAllTables() {
        MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 0;", null);
        MySQL.executeStatement("DROP TABLE IF EXISTS customers;", null);
        MySQL.executeStatement("DROP TABLE IF EXISTS readings;", null);
        MySQL.executeStatement("SET FOREIGN_KEY_CHECKS = 1;", null);
        System.out.println("Tabellen erfolgreich gelöscht!");
    }

    @Override
    public void closeConnection() {
        MySQL.disconnect();
    }
}