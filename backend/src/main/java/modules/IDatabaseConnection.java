package modules;

public interface IDatabaseConnection {
    void createAllTables();

    void truncateTables();

    void removeAllTables();

    void closeConnection();
}
