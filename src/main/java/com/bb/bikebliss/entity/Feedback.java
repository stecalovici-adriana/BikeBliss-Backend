package com.bb.bikebliss.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@SuppressWarnings("unused")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedbackId;

    @Column(name = "feedback_text", columnDefinition = "TEXT", nullable = false)
    private String feedbackText;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "feedback_date")
    private LocalDateTime feedbackDate;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "rental_id", referencedColumnName = "rentalId")
    private Rental rental;
    @ManyToOne
    @JoinColumn(name = "equipment_rental_id", referencedColumnName = "equipmentRentalId")
    private EquipmentRental equipmentRental;

    public Feedback(){}

    public Feedback(Integer feedbackId, String feedbackText, Integer rating, LocalDateTime feedbackDate, User user, Rental rental, EquipmentRental equipmentRental) {
        this.feedbackId = feedbackId;
        this.feedbackText = feedbackText;
        this.rating = rating;
        this.feedbackDate = feedbackDate;
        this.user = user;
        this.rental = rental;
        this.equipmentRental = equipmentRental;
    }

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(LocalDateTime feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public EquipmentRental getEquipmentRental() {
        return equipmentRental;
    }

    public void setEquipmentRental(EquipmentRental equipmentRental) {
        this.equipmentRental = equipmentRental;
    }
}

