package readings;

import customers.Customer;
import customers.CustomerRepository;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import customers.CustomerRepository;
import customers.Customer;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ReadingService {

    public void createReading(Reading reading) {
        try {
            // Prüfen ob customer vorhanden
            if (reading.getCustomer() == null) {
                throw new IllegalArgumentException("Customer must not be null");
            }
            // ID setzen falls nicht vorhanden
            if (reading.getCustomer().getId() == null) {
                reading.getCustomer().setId(UUID.randomUUID());
            }
            // Prüfen ob der customer bereits existiert, wenn nicht, neuen customer in DB einfügen
            if (!CustomerRepository.exists(reading.getCustomer().getId())) {
                CustomerRepository.createCustomer((Customer) reading.getCustomer());
            }
            // ReadingID setzen
            if (reading.getId() == null) {
                reading.setId(UUID.randomUUID());
            }
            // 5. Reading speichern
            ReadingRepository.createReading(reading);

        } catch (SQLException e) {
            throw new InternalServerErrorException("Failed to create reading: ", e);
        }
    }


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
}



