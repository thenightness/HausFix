package modules;

import java.sql.SQLException;
import java.util.Properties;

public interface IDatabaseConnection {
    public IDatabaseConnection openConnection(Properties properties);

    public void createAllTables();

    public void truncateTables();

    public void removeAllTables();

    public void closeConnection();
}
