package com.innerspaces.innerspace.models.auth;

import java.sql.Date;

public class ProfileDTO {
    private Date dob;
    private byte[] profilePicture;
    private String bio;


    public ProfileDTO() {
    }

    public ProfileDTO(Date dob, String bio) {
        this.dob = dob;
        this.bio = bio;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
