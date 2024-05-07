package com.innerspaces.innerspace.models.auth;

public class Refresh {
    String Token;

    public Refresh(String token) {
        Token = token;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
