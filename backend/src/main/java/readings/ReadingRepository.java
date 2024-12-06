package readings;

import customers.CustomerRepository;
import database.MySQL;
import modules.IReading;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReadingRepository {

    public static void createReading(Reading reading) throws SQLException {
        String query = "INSERT INTO readings (id, meterCount, DateOfReading, customerId, kindOfMeter, substitute, comment, meterId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        List<String> parameters = List.of(
                reading.getId().toString(),
                Double.toString(reading.getMeterCount()),
                reading.getDateOfReading().toString(),
                reading.getCustomer().getId().toString(),
                reading.getKindOfMeter().toString(),
                Integer.toString(reading.getSubstitute() ? 1 : 0),
                reading.getComment() != null ? reading.getComment() : "",
                reading.getMeterId()
        );
        MySQL.executeStatement(query, parameters);
    }

    public static Reading getReading(UUID id) throws SQLException {
        String query = "SELECT * FROM readings WHERE id = ?";
        ResultSet rs = MySQL.executeSelect(query);

        if (rs.next()) {
            Reading reading = new Reading();
            reading.setId(UUID.fromString(rs.getString("id")));
            reading.setMeterCount(rs.getDouble("meterCount"));
            reading.setDateOfReading(LocalDate.parse(rs.getString("dateOfReading")));
            reading.setCustomer(CustomerRepository.getCustomer(UUID.fromString(rs.getString("customerId"))));
            reading.setKindOfMeter(IReading.KindOfMeter.valueOf(rs.getString("kindOfMeter")));
            reading.setSubstitute(rs.getBoolean("substitute"));
            reading.setComment(rs.getString("comment") != null ? rs.getString("comment") : "");
            return reading;
        }
        return null; // Gibt null zurück, wenn kein Reading gefunden wird
    }

    // Update - Aktualisiere ein Reading
    public static Boolean updateReading(Reading reading) throws SQLException {
        String query = "UPDATE readings SET meterCount = ?, dateOfReading = ?, customerId = ?, kindOfMeter = ?, substitute = ?, comment = ?, meterId = ? WHERE id = ?";

        List<String> parameters = List.of(
                Double.toString(reading.getMeterCount()),
                reading.getDateOfReading().toString(),
                reading.getCustomer().getId().toString(),
                reading.getKindOfMeter().toString(),
                Integer.toString(reading.getSubstitute() ? 1 : 0),
                reading.getComment() != null ? reading.getComment() : "",
                reading.getMeterId(),
                reading.getId().toString()
        );
        // Execute the query with the parameters
        return MySQL.executeStatement(query, parameters) > 0;
    }

    // Delete - Lösche ein Reading aus der Datenbank
    public static boolean deleteReading(UUID id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Reading ID cannot be null");
        }
        String query = "DELETE FROM readings WHERE id = ?";

        return MySQL.executeStatement(query, List.of(id.toString())) > 0;
    }

    // Get All - Hole alle Readings
    public static List<Reading> getAllReadings() throws SQLException {
        String query = "SELECT * FROM readings";
        ResultSet rs = MySQL.executeSelect(query);  // Assuming MySQL.executeSelect(query) returns a ResultSet.
        List<Reading> readings = new ArrayList<>();

        while (rs.next()) {
            Reading reading = new Reading();
            reading.setId(UUID.fromString(rs.getString("id")));
            reading.setMeterCount(rs.getDouble("meterCount"));
            reading.setDateOfReading(rs.getDate("date").toLocalDate());
            reading.setCustomer(CustomerRepository.getCustomer(UUID.fromString(rs.getString("customerId"))));
            reading.setKindOfMeter(IReading.KindOfMeter.valueOf(rs.getString("kindOfMeter")));
            reading.setSubstitute(rs.getBoolean("substitute"));
            reading.setComment(rs.getString("comment"));
            readings.add(reading);
        }
        return readings;
    }
}