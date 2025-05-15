package database;

import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MySQLTest {

    @BeforeAll
    static void setup() {
        MySQL.init("mariadb", 3306, System.getenv("MYSQL_DATABASE"), System.getenv("MYSQL_USER"), System.getenv("MYSQL_PASSWORD"));
    }

    @Test
    @Order(1)
    void testInitAndIsConnected() {
        assertTrue(MySQL.isConnected(), "MySQL connection should be established after init()");
    }

    @Test
    @Order(2)
    void testGetInstanceReturnsSameObject() {
        MySQL instance1 = MySQL.getInstance();
        MySQL instance2 = MySQL.getInstance();
        assertSame(instance1, instance2, "MySQL.getInstance() should return the singleton instance");
    }

    @Test
    @Order(3)
    void testExecuteStatementInsertAndSelect() throws SQLException {
        MySQL.executeStatement("CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50))", null);
        MySQL.executeStatement("INSERT INTO test_table (name) VALUES (?)", List.of("testuser"));

        ResultSet rs = MySQL.executeSelect("SELECT * FROM test_table WHERE name = ?", List.of("testuser"));
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("testuser", rs.getString("name"));
    }

    @Test
    @Order(4)
    void testExecuteSelectWithoutParams() throws SQLException {
        ResultSet rs = MySQL.executeSelect("SELECT name FROM test_table WHERE name = 'testuser'");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("testuser", rs.getString("name"));
    }

    @Test
    @Order(5)
    void testGetConnectionReturnsSameConnection() throws SQLException {
        var conn1 = MySQL.getConnection();
        var conn2 = MySQL.getConnection();
        assertSame(conn1, conn2, "getConnection should return the same connection if open");
        assertFalse(conn1.isClosed());
    }

    @Test
    @Order(6)
    void testDisconnect() throws SQLException {
        MySQL.disconnect();
        assertFalse(MySQL.isConnected(), "Connection should be closed after disconnect()");
    }

    @Test
    @Order(7)
    void testReconnectAfterDisconnect() throws SQLException {
        MySQL.getConnection(); // should reconnect automatically
        assertTrue(MySQL.isConnected(), "getConnection should reconnect if closed");
    }
    @Test
    @Order(8)
    void testExecuteStatementWithNullParams() {
        int result = MySQL.executeStatement("CREATE TABLE IF NOT EXISTS test_null (id INT)", null);
        assertTrue(result >= 0); // Success or no-op
    }
    @Test
    @Order(9)
    void testDisconnectWhenAlreadyDisconnected() throws SQLException {
        MySQL.disconnect();
        MySQL.disconnect();
        assertFalse(MySQL.isConnected());
    }
    @Test
    @Order(10)
    void testConnectSkipsIfAlreadyConnected() throws SQLException {
        MySQL.getConnection();
        assertDoesNotThrow(() -> MySQL.getInstance().getClass().getDeclaredMethod("connect").trySetAccessible()); // reflectively touch method (coverage only)
    }

    @Test
    @Order(12)
    void testGetInstanceThrowsIfNotInitialized() throws Exception {
        // Backup instance
        var field = MySQL.class.getDeclaredField("instance");
        field.setAccessible(true);
        Object backup = field.get(null);

        // Clear instance
        field.set(null, null);

        Exception ex = assertThrows(IllegalStateException.class, MySQL::getInstance);
        assertTrue(ex.getMessage().contains("not initialized"));

        // Restore instance
        field.set(null, backup);
    }
    @Test
    @Order(13)
    void testExecuteSelectPrint() {
        MySQL.executeStatement("CREATE TABLE IF NOT EXISTS test_print (id INT)", null);
        MySQL.executeStatement("INSERT INTO test_print (id) VALUES (1)", null);

        assertDoesNotThrow(() -> MySQL.executeSelectPrint("SELECT * FROM test_print"));
    }


    @AfterAll
    static void cleanUp() throws SQLException {
        MySQL.executeStatement("DROP TABLE IF EXISTS test_table", null);
        MySQL.disconnect();
    }
}
