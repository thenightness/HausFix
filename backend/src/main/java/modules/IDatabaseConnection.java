package modules;

import java.sql.SQLException;

public interface IDatabaseConnection {
    public void createAllTables() throws SQLException;

    public void truncateTables();

    public void removeAllTables();

    public void closeConnection();
}
