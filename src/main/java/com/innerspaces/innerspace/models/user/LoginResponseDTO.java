package com.innerspaces.innerspace.models.user;

public class LoginResponseDTO {
    private ApplicationUser user;
    private String accessToken;
    private String refreshToken;
    public LoginResponseDTO() {
        super();
    }

    public LoginResponseDTO(ApplicationUser user, String accessToken, String refreshToken) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
