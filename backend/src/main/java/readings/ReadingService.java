package readings;

import customers.Customer;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ReadingService {
    private final ReadingRepository readingRepository;

    public ReadingService() {
        this.readingRepository = new ReadingRepository();
    }

    public String updateReading(Reading reading) {
        try{
            if (reading.getId() == null) {
                throw new RuntimeException("Customer ID not found");
            }
            readingRepository.updateReading(reading);
            return "Reading mit ID " + reading.getId() + " erfolgreich geupdated";
        }
        catch (Exception e){
            throw new RuntimeException("Failed to update customer: ", e);
        }
    }

}



