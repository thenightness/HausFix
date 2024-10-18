package database;

import java.sql.*;
import java.util.List;

public class MySQL {
    private static MySQL instance;

    private Connection connection;
    private final String ip;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public static void init(String ip, int port, String database, String username, String password) {
        instance = new MySQL(ip, port, database, username, password);
        instance.connect();
    }

    private MySQL(String ip, int port, String database, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    private void connect() {
        try {
            if(connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + database, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected() {
        if(instance.connection != null) {
            try {
                if(!instance.connection.isClosed()) {
                    return true;
                }
            } catch (SQLException ignored) {}
        }
        return false;
    }

    public static void disconnect() {
        try {
            instance.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Erstellt Statement und führt es aus
    public static void executeStatement(String statement, List<String> values) {
        try {
            PreparedStatement p = instance.connection.prepareStatement(statement);
            if(values != null)
                for(int i = 0; i < values.size(); i++) {
                    p.setString(i + 1, values.get(i));
                }
            p.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet executeSelect(String query, List<Object> parameters) throws SQLException {
        PreparedStatement p = instance.connection.prepareStatement(query);
        for (int i = 0; i < parameters.size(); i++) {
            p.setObject(i + 1, parameters.get(i));
        }
        return p.executeQuery(); // Gib das ResultSet zurück, ohne es sofort zu schließen
    }


    public static ResultSet executeSelect(String query){
        try {
            PreparedStatement p = instance.connection.prepareStatement(query);
             return p.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void executeSelectPrint(String query){
        try {
            PreparedStatement p = instance.connection.prepareStatement(query);
            ResultSet rs = p.executeQuery();

            while(rs.next()) {
                for(int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    System.out.print(rs.getString(i + 1));
                    System.out.print("\t| ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}