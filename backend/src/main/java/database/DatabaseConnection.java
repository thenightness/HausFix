package database;

import modules.IDatabaseConnection;

public class DatabaseConnection implements IDatabaseConnection {
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
        MySQL.disconnect();
    }
}