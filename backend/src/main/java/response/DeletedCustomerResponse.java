package response;

import customers.Customer;
import readings.Reading;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeletedCustomerResponse {

    @JsonProperty("deletedCustomer")
    private Customer deletedCustomer;

    @JsonProperty("readings")
    private List<Reading> orphanedReadings;

    public DeletedCustomerResponse(Customer deletedCustomer, List<Reading> orphanedReadings) {
        this.deletedCustomer = deletedCustomer;
        this.orphanedReadings = orphanedReadings;
    }

    public Customer getDeletedCustomer() {
        return deletedCustomer;
    }

    public List<Reading> getOrphanedReadings() {
        return orphanedReadings;
    }
}

