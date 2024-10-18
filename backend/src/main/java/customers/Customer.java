package customers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import database.MySQL;
import modules.ICustomer;

public class Customer implements ICustomer {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public static void updateCustomer(UUID id, String newName, String newSurname) {
        String query = "UPDATE users SET name = ?, surname = ? WHERE id = ?";
        MySQL.executeStatement(query, List.of(newName, newSurname, id.toString()));
    }

    public static void deleteCustomer(UUID id) {
        String query = "DELETE FROM users WHERE id = ?";
        MySQL.executeStatement(query, List.of(id.toString()));
    }
}
