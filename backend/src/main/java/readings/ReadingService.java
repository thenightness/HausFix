package readings;

import customers.Customer;
import customers.CustomerRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import customers.CustomerRepository;
import customers.Customer;
import modules.IReading;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ReadingService {

    public UUID createReading(CreateableReading reading) {
        try {
            // Set customer ID if missing
            if (reading.getCustomerId() == null) {
                reading.setCustomerId(UUID.randomUUID());
            }

            // Create customer if not already in DB
            if (!CustomerRepository.exists(reading.getCustomerId())) {
                throw new BadRequestException("No customer: ");
            }

            // Set reading ID if missing
            if (reading.getId() == null) {
                reading.setId(UUID.randomUUID());
            }

            // create the reading
            ReadingRepository.createReading(reading);
            return reading.getId();
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

    public String updateReading(CreateableReading reading) {
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

    public List<Reading> getFilteredReadings(UUID customerId, LocalDate start, LocalDate end, IReading.KindOfMeter kindOfMeter) {
        try {
            return ReadingRepository.getReadingsFiltered(customerId, start, end, kindOfMeter);
        } catch (SQLException e) {
            throw new InternalServerErrorException("Fehler beim Abrufen der gefilterten Readings", e);
        }
    }
}



