package com.bb.bikebliss.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@SuppressWarnings("unused")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDateTime expiryDate;

    public VerificationToken(){}
    public VerificationToken(Integer id, String token, User user, LocalDateTime expiryDate) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
