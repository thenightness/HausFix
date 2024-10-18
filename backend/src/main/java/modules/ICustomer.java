package modules;

import java.time.LocalDate;

public interface ICustomer extends IId {

   enum Gender {
      D, // divers
      M, // männlich
      U, // unbekannt
      W; // weiblich
   }

   String getFirstName();
   String getLastName();
   Gender getGender();
   LocalDate getBirthDate();


   void setFirstName(String firstName);

   void setLastName(String lastName);

   void setBirthDate(LocalDate birtDate);

   void setGender(Gender gender);
}
