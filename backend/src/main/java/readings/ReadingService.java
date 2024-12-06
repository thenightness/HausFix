package readings;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ReadingService {
    private final ReadingRepository readingRepository;

    public ReadingService() {
        this.readingRepository = new ReadingRepository();
    }

    public Reading getReading(UUID id) {
        try {
            return ReadingRepository.getReading(id);
        } catch (SQLException e) {
            throw new RuntimeException("reading not found", e);
        }
    }

    public List<Reading> getAllReadings() {
        try {
            return ReadingRepository.getAllReadings();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch readings: ", e);
        }
    }

    public String createReading(Reading reading) {
        try {
            if (reading.getId() == null) {
                reading.setId(UUID.randomUUID());
            }
            readingRepository.createReading(reading);
            return "Reading mit ID " + reading.getId() + " erfolgreich erstellt";
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create reading: ", e);
        }
    }

    public String deleteReading(String id) {
        try {
            UUID uuid = UUID.fromString(id);

            boolean isDeleted = readingRepository.deleteReading(uuid);

            if (isDeleted) {
                return "Reading mit ID " + id + " erfolgreich gelöscht.";
            } else {
                return "Reading mit ID " + id + " wurde nicht gefunden.";
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid UUID format for ID: " + id, e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete reading with ID: " + id, e);
        }
    }
}



