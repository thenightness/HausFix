package readings;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ReadingService {

    public Reading getReading(UUID id) {
        if (id == null) {
            throw new NotFoundException("Reading ID cannot be null");
        }

        try {
            Reading reading = ReadingRepository.getReading(id);
            if (reading == null) {
                throw new NotFoundException("Reading with ID " + id + " not found");
            }
            return reading;
        } catch (SQLException e) {
            throw new InternalServerErrorException("Error fetching reading with ID " + id, e);
        }
    }

    public void createReading(Reading reading) {
        try {
            if (reading.getId() == null) {
                reading.setId(UUID.randomUUID());
            }
            ReadingRepository.createReading(reading);
        } catch (SQLException e) {
            throw new InternalServerErrorException("Failed to create reading: ", e);
        }
    }

    public String updateReading(Reading reading) {
        if (reading.getId() == null) {
            throw new NotFoundException("Reading ID darf nicht null sein");
        }
        try {
            ReadingRepository.updateReading(reading);
            return "Reading mit ID " + reading.getId() + " erfolgreich geupdatet";
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException("Fehler beim Updaten vom Reading: ", e);
        }
    }

    public String deleteReading(UUID id) {
        try {

            boolean isDeleted = ReadingRepository.deleteReading(id);

            // Return success if deleted, otherwise throw an exception
            if (isDeleted) {
                return "Reading mit ID " + id + " erfolgreich gelöscht.";
            } else {
                throw new NotFoundException("Reading mit ID " + id + " wurde nicht gefunden.");
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Failed to delete reading with ID: " + id, e);
        }
    }

    public List<Reading> getFilteredReadings(String customerUuid, LocalDate start, LocalDate end, String kindOfMeter) throws SQLException {
        if (customerUuid == null && start == null && end == null && kindOfMeter == null) {
            // Falls keine Filter gesetzt sind, hole alle Readings
            return readingRepository.getAllReadings();
        } else {
            // Falls Filter gesetzt sind, verwende die neue Methode
            return readingRepository.findReadingsByFilters(customerUuid, start, end, kindOfMeter);
        }
    }

}



