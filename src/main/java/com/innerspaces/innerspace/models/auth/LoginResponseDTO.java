package com.innerspaces.innerspace.models.auth;

import com.innerspaces.innerspace.entities.ApplicationUser;

import java.util.Map;

public class LoginResponseDTO {
    private ApplicationUser user;
    private Map<String, String> tokens;
    public LoginResponseDTO() {
        super();
    }

    public LoginResponseDTO(ApplicationUser user, Map<String, String> tokens) {
        this.user = user;
        this.tokens = tokens;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public Map<String, String> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, String> tokens) {
        this.tokens = tokens;
    }
}
