package com.innerspaces.innerspace.models.auth;

import com.innerspaces.innerspace.entities.ApplicationUser;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class LoginResponseDTO {
    private ApplicationUser user;

    private Set<ApplicationUser> following;

    private Set<ApplicationUser> followers;
    private Map<String, String> tokens;
    public LoginResponseDTO() {
        super();
    }

    public LoginResponseDTO(ApplicationUser user, Map<String, String> tokens,
                            Set<ApplicationUser> following, Set<ApplicationUser> followers) {
        this.user = user;
        this.tokens = tokens;
        this.following = following;
        this.followers = followers;
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
