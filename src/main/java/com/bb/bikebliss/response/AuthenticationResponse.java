package com.bb.bikebliss.response;

public class AuthenticationResponse {
    private String token;
    private String message;
    private boolean isVerified;

    public AuthenticationResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public AuthenticationResponse(String token, String message, boolean isVerified) {
        this.token = token;
        this.message = message;
        this.isVerified = isVerified;
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

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}