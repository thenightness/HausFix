import customers.Customer;
import customers.CustomerRepository;
import database.DatabaseConnection;
import database.MySQL;
import modules.ICustomer;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, SQLException, IOException {
        System.out.println("Hello World!");

        //Initialisierung der Verbindung zur Datenbank

        MySQL.init("mariadb", 3306, System.getenv("MYSQL_DATABASE"),System.getenv("MYSQL_USER"),System.getenv("MYSQL_PASSWORD"));

        System.out.println("Connection Successful!");

        //MySQL.executeStatement("DROP TABLE IF EXISTS users", null);
        MySQL.executeStatement("CREATE TABLE IF NOT EXISTS users (`key` INT NOT NULL AUTO_INCREMENT, `name` VARCHAR(255), `surname` VARCHAR(255), PRIMARY KEY (`key`));", null);
        MySQL.executeStatement("INSERT INTO users (`name`, `surname`) Values (?, ?)", List.of("jEff", "Besus"));

        //Debugging Abfrage
        MySQL.executeSelectPrint("SELECT * FROM users");

        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.createAllTables();


        // CREATE
        System.out.println("Erstelle einen neuen Kunden:");
        Customer newCustomer = new Customer();
        newCustomer.setId(UUID.randomUUID());
        newCustomer.setFirstName("Test");
        newCustomer.setLastName("Tester");
        newCustomer.setBirthDate(LocalDate.of(2024, 10, 1));
        newCustomer.setGender(ICustomer.Gender.M);
        CustomerRepository.createCustomer(newCustomer);
        System.out.println("Kunde erstellt: " + newCustomer);

        /*// READ
        try {
            // Abruf der UUID des Kunden
            UUID customerId = UUID.fromString("8d9b77f9-d17c-4a46-9fae-1e38aa6060e8");

            // Kunde aus der Datenbank abrufen
            Customer customer = CustomerRepository.getCustomer(customerId);

            // Überprüfe, ob der Kunde gefunden wurde
            if (customer != null) {
                System.out.println("Kunde gefunden: " + customer);
            } else {
                System.out.println("Kunde mit der ID " + customerId + " wurde nicht gefunden.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        // READ ALL
                System.out.println("Hole alle Kunden aus der Datenbank:");
                // Aufruf der Methode
                List<Customer> allCustomers = CustomerRepository.getAllCustomers();
                for (Customer customer : allCustomers) {
                    System.out.println(customer);
                }

            /*try {
                // UUID des zu aktualisierenden Kunden (Beispiel-UUID eines existierenden Kunden)
                UUID customerId = UUID.fromString("8d9b77f9-d17c-4a46-9fae-1e38aa6060e8");

                // Hole den Kunden aus der Datenbank
                Customer customer = CustomerRepository.getCustomer(customerId);

                if (customer != null) {
                    // Zeige den aktuellen Kunden an
                    System.out.println("Aktueller Kunde: " + customer);

                    // UPDATE
                    System.out.println("Aktualisiere den Kunden...");
                    customer.setFirstName("NeuerVorname");
                    customer.setLastName("NeuerNachname");
                    customer.setBirthDate(LocalDate.of(1985, 5, 20));
                    customer.setGender(ICustomer.Gender.W);

                    // Aktualisierung
                    CustomerRepository.updateCustomer(customer);


                    System.out.println("Kunde nach dem Update: " + CustomerRepository.getCustomer(customer.getId()));
                } else {
                    System.out.println("Kunde nicht gefunden!");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            /*// DELETE
            try {
            UUID customerIdToDelete = UUID.fromString("39a1df33-a513-4985-bba5-7f615956f8ba");

            // Lösche Kunden aus Datenbank
            CustomerRepository.deleteCustomer(customerIdToDelete);
            System.out.println("Kunde mit der ID " + customerIdToDelete + " wurde gelöscht.");
            } catch (SQLException e) {
            e.printStackTrace();
            } catch (Exception e) {
            e.printStackTrace();
            }*/


        //Temporär damit Backend-Container nicht unnötig neugestartet wird
        while(true){
            Thread.sleep(1000);
        }
    }
}
