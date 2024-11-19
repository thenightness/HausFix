package database;

import java.sql.SQLException;
import java.util.Collections;


public class TestDBConnection extends DatabaseConnection {
        @Override
        public void createAllTables() throws SQLException {
            MySQL.executeStatement("CREATE DATABASE IF NOT EXISTS hausfixtestdb;", Collections.emptyList());
            MySQL.executeStatement("USE hausfixtestdb;", Collections.emptyList());
            MySQL.executeStatement("CREATE TABLE IF NOT EXISTS customers (id CHAR(36) NOT NULL PRIMARY KEY, firstName VARCHAR(255) NOT NULL, lastName VARCHAR(255) NOT NULL, birthDate DATE NOT NULL, gender ENUM('M', 'W', 'D', 'U') NOT NULL);", Collections.emptyList());
            MySQL.executeStatement("CREATE TABLE IF NOT EXISTS readings (id CHAR(36) NOT NULL PRIMARY KEY, customerId CHAR(36), meterId VARCHAR(255) NOT NULL, meterCount DOUBLE NOT NULL, dateOfReading DATE NOT NULL, kindOfMeter ENUM('HEIZUNG', 'STROM', 'WASSER', 'UNBEKANNT') NOT NULL, comment TEXT, substitute BOOLEAN DEFAULT FALSE, FOREIGN KEY (customerId) REFERENCES customers(id) ON DELETE SET NULL);", Collections.emptyList());

            if (MySQL.isConnected()) {
                System.out.println("Test-Tabellen erfolgreich erstellt");
            } else {
                System.out.println("Fehler beim Erstellen der Test-Tabellen; Keine Connection vorhanden");
            }
        }

    }

