package readings;

import exceptions.ReadingNotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ReadingService {
    private final ReadingRepository readingRepository;

    public ReadingService() {
        this.readingRepository = new ReadingRepository();
    }

    public Reading getReading(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Reading ID cannot be null");
        }

        try {
            Reading reading = readingRepository.getReading(id);
            if (reading == null) {
                throw new ReadingNotFoundException("Reading with ID " + id + " not found");
            }
            return reading;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reading with ID " + id, e);
        }
    }

    public List<Reading> getAllReadings() {
        try {
            return readingRepository.getAllReadings();
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

    public String updateReading(Reading reading) {
        if (reading.getId() == null) {
            throw new IllegalArgumentException("Reading ID darf nicht null sein");
        }
        try {
            readingRepository.updateReading(reading);
            return "Reading mit ID " + reading.getId() + " erfolgreich geupdatet";
        } catch (ReadingNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Updaten vom Reading: ", e);
        }
    }

    public String deleteReading(String id) {
        try {
            // Validate and convert the UUID
            UUID uuid = UUID.fromString(id);

            boolean isDeleted = readingRepository.deleteReading(uuid);

            // Return success if deleted, otherwise throw an exception
            if (isDeleted) {
                return "Reading mit ID " + id + " erfolgreich gelöscht.";
            } else {
                throw new ReadingNotFoundException("Reading mit ID " + id + " wurde nicht gefunden.");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for ID: " + id, e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete reading with ID: " + id, e);
        }
    }
}



