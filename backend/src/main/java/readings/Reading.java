package readings;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import customers.Customer;
import modules.ICustomer;
import modules.IReading;

import java.time.LocalDate;
import java.util.UUID;

public class Reading implements IReading {

    private UUID id;
    private String comment;
    @JsonProperty("customer")
    @JsonDeserialize(using = CustomerDeserializer.class)
    private Customer customer;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfReading;
    private KindOfMeter kindOfMeter;
    private Double meterCount;
    private String meterId;
    private Boolean substitute;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public ICustomer getCustomer() {
        return this.customer;
    }

    @Override
    public void setCustomer(ICustomer customer) {
        if (customer == null) {
            this.customer = null;
        } else if (customer instanceof Customer) {
            this.customer = (Customer) customer;
        } else {
            throw new IllegalArgumentException("Expected Customer instance");
        }
    }


    @Override
    public LocalDate getDateOfReading() {
        return dateOfReading;
    }

    @Override
    public void setDateOfReading(LocalDate dateOfReading) {
        this.dateOfReading = dateOfReading;
    }

    @Override
    public KindOfMeter getKindOfMeter() {
        return kindOfMeter;
    }

    @Override
    public void setKindOfMeter(KindOfMeter kindOfMeter) {
        this.kindOfMeter = kindOfMeter;
    }

    @Override
    public Double getMeterCount() {
        return meterCount;
    }

    @Override
    public void setMeterCount(Double meterCount) {
        this.meterCount = meterCount;
    }

    @Override
    public String getMeterId() {
        return meterId;
    }

    @Override
    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    @Override
    public Boolean getSubstitute() {
        return substitute;
    }

    @Override
    public void setSubstitute(Boolean substitute) {
        this.substitute = substitute;
    }

    public Integer getSubstituteAsInt() {
        if (substitute == null) {
            return 0;
        }
        return substitute ? 1 : 0;
    }

    public void setSubstituteFromInt(Integer substituteInt) {
        if (substituteInt == null) {
            this.substitute = false;
        } else {
            this.substitute = substituteInt == 1;
        }
    }

    @Override
    public String printDateOfReading() {
        return null;
    }
}
