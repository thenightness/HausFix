package customers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerWrapper {
    @JsonProperty("customer")
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
