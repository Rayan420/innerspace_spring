package com.innerspaces.innerspace.models.auth;

import com.innerspaces.innerspace.entities.ApplicationUser;
import java.util.Map;
import java.util.Set;

public class LoginResponseDTO {
    private ApplicationUser user;
    private Set<LightweightUserDTO> following;
    private Set<LightweightUserDTO> followers;
    private Map<String, String> tokens;

    public LoginResponseDTO() {
        super();
    }

    public LoginResponseDTO(ApplicationUser user, Map<String, String> tokens,
                            Set<LightweightUserDTO> following, Set<LightweightUserDTO> followers) {
        this.user = user;
        this.tokens = tokens;
        this.following = following;
        this.followers = followers;
    }

    // Getters and Setters
    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public Set<LightweightUserDTO> getFollowing() {
        return following;
    }

    public void setFollowing(Set<LightweightUserDTO> following) {
        this.following = following;
    }

    public Set<LightweightUserDTO> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<LightweightUserDTO> followers) {
        this.followers = followers;
    }

    public Map<String, String> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, String> tokens) {
        this.tokens = tokens;
    }
}
