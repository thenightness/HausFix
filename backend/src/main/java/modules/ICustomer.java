package modules;

import java.time.LocalDate;

public interface ICustomer extends IId {

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    Gender getGender();

    void setGender(Gender gender);

    LocalDate getBirthDate();

    void setBirthDate(LocalDate birtDate);

    enum Gender {
        D, // divers
        M, // männlich
        U, // unbekannt
        W  // weiblich
    }
}
