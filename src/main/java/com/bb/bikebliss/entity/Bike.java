package com.bb.bikebliss.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "bikes")
@SuppressWarnings("unused")
public class Bike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bikeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "bike_status", nullable = false)
    private BikeStatus bikeStatus;

    @ManyToOne
    @JoinColumn(name = "model_id", referencedColumnName = "modelId", nullable = false)
    @JsonBackReference
    private BikeModel bikeModel;

    @OneToMany(mappedBy = "bike")
    private List<Rental> rentals;

    public Bike() {}

    public Bike(Integer bikeId, BikeStatus bikeStatus, BikeModel bikeModel, List<Rental> rentals) {
        this.bikeId = bikeId;
        this.bikeStatus = bikeStatus;
        this.bikeModel = bikeModel;
        this.rentals = rentals;
    }

    public Integer getBikeId() {
        return bikeId;
    }

    public void setBikeId(Integer bikeId) {
        this.bikeId = bikeId;
    }

    public BikeStatus getBikeStatus() {
        return bikeStatus;
    }

    public void setBikeStatus(BikeStatus bikeStatus) {
        this.bikeStatus = bikeStatus;
    }

    public BikeModel getBikeModel() {
        return bikeModel;
    }

    public void setBikeModel(BikeModel bikeModel) {
        this.bikeModel = bikeModel;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }
}


