package readings;

import modules.ICustomer;
import modules.IReading;

import java.time.LocalDate;
import java.util.UUID;

public class Reading implements IReading {

    @Override
    public UUID getId() {
        System.out.println("it works");
        return null;
    }


    @Override
    public void setId(UUID id) {

    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public ICustomer getCustomer() {
        return null;
    }

    @Override
    public LocalDate getDateOfReading() {
        return null;
    }

    @Override
    public KindOfMeter getKindOfMeter() {
        return null;
    }

    @Override
    public Double getMeterCount() {
        return null;
    }

    @Override
    public String getMeterId() {
        return null;
    }

    @Override
    public Boolean getSubstitute() {
        return null;
    }

    @Override
    public String printDateOfReading() {
        return null;
    }

    @Override
    public void setComment(String comment) {

    }

    @Override
    public void setCustomer(ICustomer customer) {

    }

    @Override
    public void setDateOfReading(LocalDate dateOfReading) {

    }

    @Override
    public void setKindOfMeter(KindOfMeter kindOfMeter) {

    }

    @Override
    public void setMeterCount(Double meterCount) {

    }

    @Override
    public void setMeterId(String meterId) {

    }

    @Override
    public void setSubstitute(Boolean substitute) {

    }
}
