package modules;

public interface IDatabaseConnection {
    public void createAllTables();

    public void truncateTables();

    public void removeAllTables();

    public void closeConnection();
}
