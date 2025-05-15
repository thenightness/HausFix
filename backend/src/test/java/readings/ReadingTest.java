package readings;

import customers.Customer;
import modules.ICustomer;
import modules.IReading;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReadingTest {

    @Test
    void testAllSettersAndGetters() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Jane");

        LocalDate date = LocalDate.of(2024, 5, 1);

        Reading reading = new Reading();
        reading.setId(id);
        reading.setComment("Test comment");
        reading.setCustomer(customer);
        reading.setDateOfReading(date);
        reading.setKindOfMeter(IReading.KindOfMeter.WASSER);
        reading.setMeterCount(123.45);
        reading.setMeterId("M-123");
        reading.setSubstitute(true);

        assertEquals(id, reading.getId());
        assertEquals("Test comment", reading.getComment());
        assertEquals(customer, reading.getCustomer());
        assertEquals(date, reading.getDateOfReading());
        assertEquals(IReading.KindOfMeter.WASSER, reading.getKindOfMeter());
        assertEquals(123.45, reading.getMeterCount());
        assertEquals("M-123", reading.getMeterId());
        assertTrue(reading.getSubstitute());
    }

    @Test
    void testSetCustomer_withNull() {
        Reading reading = new Reading();
        reading.setCustomer(null);
        assertNull(reading.getCustomer());
    }

    @Test
    void testSetCustomer_withInvalidType_throwsException() {
        Reading reading = new Reading();
        ICustomer wrongTypeCustomer = new ICustomer() {
            public UUID getId() { return null; }
            public void setId(UUID id) {}
            public String getFirstName() { return null; }
            public void setFirstName(String firstName) {}
            public String getLastName() { return null; }
            public void setLastName(String lastName) {}
            public LocalDate getBirthDate() { return null; }
            public void setBirthDate(LocalDate birthDate) {}
            public Gender getGender() { return null; }
            public void setGender(Gender gender) {}
        };

        assertThrows(IllegalArgumentException.class, () -> reading.setCustomer(wrongTypeCustomer));
    }

    @Test
    void testGetSubstituteAsInt_returns1() {
        Reading reading = new Reading();
        reading.setSubstitute(true);
        assertEquals(1, reading.getSubstituteAsInt());
    }

    @Test
    void testGetSubstituteAsInt_returns0ForFalse() {
        Reading reading = new Reading();
        reading.setSubstitute(false);
        assertEquals(0, reading.getSubstituteAsInt());
    }

    @Test
    void testGetSubstituteAsInt_returns0ForNull() {
        Reading reading = new Reading();
        reading.setSubstitute(null);
        assertEquals(0, reading.getSubstituteAsInt());
    }

    @Test
    void testSetSubstituteFromInt_setsTrue() {
        Reading reading = new Reading();
        reading.setSubstituteFromInt(1);
        assertTrue(reading.getSubstitute());
    }

    @Test
    void testSetSubstituteFromInt_setsFalse() {
        Reading reading = new Reading();
        reading.setSubstituteFromInt(0);
        assertFalse(reading.getSubstitute());
    }

    @Test
    void testSetSubstituteFromInt_setsFalseForNull() {
        Reading reading = new Reading();
        reading.setSubstituteFromInt(null);
        assertFalse(reading.getSubstitute());
    }

    @Test
    void testPrintDateOfReading_returnsNull() {
        Reading reading = new Reading();
        assertNull(reading.printDateOfReading());
    }

}
