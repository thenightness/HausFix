package readings;

import customers.CustomerRepository;
import database.MySQL;
import jakarta.ws.rs.NotFoundException;
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
        if (id == null) {
            throw new IllegalArgumentException("Reading ID cannot be null");
        }
        String query = "SELECT * FROM readings WHERE id = ?";
        ResultSet rs = MySQL.executeSelect(query, List.of(id.toString()));

        if (rs == null) {
            throw new RuntimeException("Failed to execute query or no result returned.");
        }

        if (rs.next()) {
            return mapResultSetToReading(rs);
        }

        return null;
    }

    // Update - Aktualisiere ein Reading
    public static void updateReading(Reading reading) throws SQLException {
        String query = "UPDATE readings SET meterCount = ?, dateOfReading = ?, customerId = ?, kindOfMeter = ?, substitute = ?, comment = ?, meterId = ? WHERE id = ?";

        int rowsAffected = MySQL.executeStatement(query, List.of(
                Double.toString(reading.getMeterCount()),
                reading.getDateOfReading().toString(),
                reading.getCustomer().getId().toString(),
                reading.getKindOfMeter().toString(),
                Integer.toString(reading.getSubstitute() ? 1 : 0),
                reading.getComment() != null ? reading.getComment() : "",
                reading.getMeterId(),
                reading.getId().toString()
        ));
        if (rowsAffected == 0) {
            throw new NotFoundException("Kein Reading mit ID: " + reading.getId());
        }
    }

    // Delete - Lösche ein Reading aus der Datenbank
    public static boolean deleteReading(UUID id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Reading ID cannot be null");
        }
        String query = "DELETE FROM readings WHERE id = ?";

        return MySQL.executeStatement(query, List.of(id.toString())) > 0;
    }

    public static List<Reading> getReadingsWithNullCustomer() throws SQLException {
        String query = "SELECT * FROM readings WHERE customerId IS NULL";
        ResultSet rs = MySQL.executeSelect(query, List.of());

        List<Reading> readings = new ArrayList<>();
        while (rs != null && rs.next()) {
            readings.add(mapResultSetToReading(rs));
        }
        return readings;
    }



    private static Reading mapResultSetToReading(ResultSet rs) throws SQLException {
        Reading reading = new Reading();
        reading.setId(UUID.fromString(rs.getString("id")));
        reading.setMeterCount(rs.getDouble("meterCount"));
        reading.setDateOfReading(LocalDate.parse(rs.getString("dateOfReading")));
        String customerIdStr = rs.getString("customerId");
        if (customerIdStr != null && !customerIdStr.isEmpty()) {
            reading.setCustomer(CustomerRepository.getCustomer(UUID.fromString(customerIdStr)));
        } else {
            reading.setCustomer(null); // Readings werden trotzdem rausgelesen auch wenn die customer ID null ist
        }
        reading.setKindOfMeter(IReading.KindOfMeter.valueOf(rs.getString("kindOfMeter")));
        reading.setSubstitute(rs.getBoolean("substitute"));
        reading.setComment(rs.getString("comment") != null ? rs.getString("comment") : "");
        reading.setMeterId(rs.getString("meterId"));
        return reading;
    }
}