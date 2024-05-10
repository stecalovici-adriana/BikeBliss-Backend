package com.bb.bikebliss.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bike_models")
@SuppressWarnings("unused")
public class BikeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer modelId;

    @Column(name = "bike_model", nullable = false)
    private String bikeModel;

    @Column(name = "price_per_day", precision = 5, scale = 2, nullable = false)
    private BigDecimal pricePerDay;

    @Column(name = "bike_description", columnDefinition = "TEXT", nullable = false)
    private String bikeDescription;

    @Column(name = "image_url", nullable = false)
    private String imageURL;
    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "locationId")
    private Location location;
    @OneToMany(mappedBy = "bikeModel")
    @JsonManagedReference
    private List<Bike> bikes;

    public BikeModel() {}

    public BikeModel(Integer modelId, String bikeModel, BigDecimal pricePerDay, String bikeDescription, String imageURL, Location location, List<Bike> bikes) {
        this.modelId = modelId;
        this.bikeModel = bikeModel;
        this.pricePerDay = pricePerDay;
        this.bikeDescription = bikeDescription;
        this.imageURL = imageURL;
        this.location = location;
        this.bikes = bikes;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getBikeModel() {
        return bikeModel;
    }

    public void setBikeModel(String bikeModel) {
        this.bikeModel = bikeModel;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getBikeDescription() {
        return bikeDescription;
    }

    public void setBikeDescription(String bikeDescription) {
        this.bikeDescription = bikeDescription;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Bike> getBikes() {
        return bikes;
    }

    public void setBikes(List<Bike> bikes) {
        this.bikes = bikes;
    }
}