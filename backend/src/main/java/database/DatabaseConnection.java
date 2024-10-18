package database;

import modules.IDatabaseConnection;

public class DatabaseConnection implements IDatabaseConnection {
    @Override
    public void createAllTables() {
        MySQL.executeStatement("CREATE TABLE IF NOT EXISTS customers (id CHAR(36) NOT NULL PRIMARY KEY, firstName VARCHAR(255) NOT NULL, lastName VARCHAR(255) NOT NULL, birthDate DATE NOT NULL, gender ENUM('M', 'W', 'D', 'U') NOT NULL);", null);
        MySQL.executeStatement("CREATE TABLE IF NOT EXISTS readings (id CHAR(36) NOT NULL PRIMARY KEY, customerId CHAR(36), meterId VARCHAR(255) NOT NULL, meterCount DOUBLE NOT NULL, dateOfReading DATE NOT NULL, kindOfMeter ENUM('HEIZUNG', 'STROM', 'WASSER', 'UNBEKANNT') NOT NULL, comment TEXT, substitute BOOLEAN DEFAULT FALSE, FOREIGN KEY (customerId) REFERENCES customers(id) ON DELETE SET NULL);", null);
        System.out.println("Tabellen erfolgreich erstellt");
    }

    @Override
    public void truncateTables() {

    }

    @Override
    public void removeAllTables() {

    }

    @Override
    public void closeConnection() {
        MySQL.disconnect();
    }
}