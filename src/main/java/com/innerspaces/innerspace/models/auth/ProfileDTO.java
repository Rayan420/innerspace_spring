package com.innerspaces.innerspace.models.auth;

import java.sql.Date;

public class ProfileDTO {
    private Date dob;
    private String profileImageUrl;
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
