import MariaDbConnection.MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, SQLException {
        System.out.println("Hello World!");

        //Initialisierung der Verbindung zur Datenbank
        MySQL.init("mariadb", 3306, System.getenv("MYSQL_DATABASE"),System.getenv("MYSQL_USER"),System.getenv("MYSQL_PASSWORD"));

        MySQL.executeStatement("CREATE TABLE IF NOT EXISTS users (`key` INT NOT NULL AUTO_INCREMENT, `name` VARCHAR(255), `surname` VARCHAR(255), PRIMARY KEY (`key`));", null);
        MySQL.executeStatement("INSERT INTO users (`name`, `surname`) Values (?, ?)", List.of("jEff", "Besus"));

        //Temporär damit Backend-Container nicht unnötig neugestartet wird
        while(true){
            Thread.sleep(1000);
        }
    }
}
