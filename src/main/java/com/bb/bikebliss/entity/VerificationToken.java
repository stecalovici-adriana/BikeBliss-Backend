package com.bb.bikebliss.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@SuppressWarnings("unused")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token")
    private String token;

    @Column(name = "is_logged_out")
    private boolean loggedOut;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    @Column(name = "used")
    private boolean used;

    public VerificationToken(){}

    public VerificationToken(Integer id, String token, boolean loggedOut, User user, LocalDateTime expiryDate, boolean used) {
        this.id = id;
        this.token = token;
        this.loggedOut = loggedOut;
        this.user = user;
        this.expiryDate = expiryDate;
        this.used = used;
    }
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

    public boolean isLoggedOut() {
        return loggedOut;
    }

    public void setLoggedOut(boolean loggedOut) {
        this.loggedOut = loggedOut;
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

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
