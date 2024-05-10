package com.bb.bikebliss.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "equipment_rentals")
@SuppressWarnings("unused")
public class EquipmentRental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer equipmentRentalId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "total_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "rental_status", nullable = false)
    private RentalStatus rentalStatus;

    @ManyToOne
    @JoinColumn(name = "equipment_id", referencedColumnName = "equipmentId", nullable = false)
    private Equipment equipment;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private User user;
    @Column(name = "days")
    private Integer days;

    public EquipmentRental(){}

    public EquipmentRental(Integer equipmentRentalId, LocalDateTime startDate, LocalDateTime endDate, BigDecimal totalPrice, RentalStatus rentalStatus, Equipment equipment, User user, Integer days) {
        this.equipmentRentalId = equipmentRentalId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.rentalStatus = rentalStatus;
        this.equipment = equipment;
        this.user = user;
        this.days = days;
    }

    public Integer getEquipmentRentalId() {
        return equipmentRentalId;
    }

    public void setEquipmentRentalId(Integer equipmentRentalId) {
        this.equipmentRentalId = equipmentRentalId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public RentalStatus getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(RentalStatus rentalStatus) {
        this.rentalStatus = rentalStatus;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
