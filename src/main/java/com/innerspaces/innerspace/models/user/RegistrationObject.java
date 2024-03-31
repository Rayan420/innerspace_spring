package com.innerspaces.innerspace.models.user;
import java.sql.Date;
import java.time.LocalDate;

public class RegistrationObject {
    private String email;
    private String firstName;
    private String lastName;
     private Date dob;

    public RegistrationObject() {
        super();
    }

    public RegistrationObject(String email, String firstName, String lastName, Date dob, LocalDate dateJoined) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }




    @Override
    public String toString() {
        return "RegistrationObject{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob=" + dob +
                '}';
    }
}
