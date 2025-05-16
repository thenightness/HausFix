package readings;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import modules.IReading;

import java.time.LocalDate;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateableReading {

    private UUID id;
    private String comment;
    private UUID customerId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfReading;
    private IReading.KindOfMeter kindOfMeter;
    private Double meterCount;
    private String meterId;
    private Boolean substitute;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customer) {
        this.customerId = customer;
    }

    public LocalDate getDateOfReading() {
        return dateOfReading;
    }

    public void setDateOfReading(LocalDate dateOfReading) {
        this.dateOfReading = dateOfReading;
    }

    public IReading.KindOfMeter getKindOfMeter() {
        return kindOfMeter;
    }

    public void setKindOfMeter(IReading.KindOfMeter kindOfMeter) {
        this.kindOfMeter = kindOfMeter;
    }

    public Double getMeterCount() {
        return meterCount;
    }

    public void setMeterCount(Double meterCount) {
        this.meterCount = meterCount;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public Boolean getSubstitute() {
        return substitute;
    }

    public void setSubstitute(Boolean substitute) {
        this.substitute = substitute;
    }

    public String printDateOfReading() {
        return null;
    }
}
