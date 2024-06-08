package com.bb.bikebliss.response;


public class AuthenticationResponse {
    private String token;
    private String message;
    private boolean isVerified;
    private String userRole;

    public AuthenticationResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }
    public AuthenticationResponse(String token, String message, String userRole) {
        this.token = token;
        this.message = message;
        this.userRole = userRole;
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

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}