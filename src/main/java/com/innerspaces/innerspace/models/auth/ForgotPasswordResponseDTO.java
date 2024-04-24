package com.innerspaces.innerspace.models.auth;

public class ForgotPasswordResponseDTO {
    private String token;

    private String message;

    public ForgotPasswordResponseDTO() {
    }


    public ForgotPasswordResponseDTO(String message) {
        this.message = message;
    }

    public ForgotPasswordResponseDTO( String token, String message) {
        this.token = token;
        this.message = message;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
