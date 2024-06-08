package com.bb.bikebliss.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "equipment_models")
@SuppressWarnings("unused")
public class EquipmentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer equipmentModelId;

    @Column(name = "equipment_model", nullable = false)
    private String equipmentModel;

    @Column(name = "equipment_description", columnDefinition = "TEXT", nullable = false)
    private String equipmentDescription;

    @Column(name = "price_per_day", precision = 5, scale = 2, nullable = false)
    private BigDecimal pricePerDay;

    @Column(name = "image_url", nullable = false)
    private String imageURL;
    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "locationId")
    private Location location;
    @OneToMany(mappedBy = "equipmentModel")
    @JsonManagedReference
    private List<Equipment> equipment;

    public EquipmentModel(){}

    public EquipmentModel(Integer equipmentModelId, String equipmentModel, String equipmentDescription, BigDecimal pricePerDay, String imageURL, Location location, List<Equipment> equipment) {
        this.equipmentModelId = equipmentModelId;
        this.equipmentModel = equipmentModel;
        this.equipmentDescription = equipmentDescription;
        this.pricePerDay = pricePerDay;
        this.imageURL = imageURL;
        this.location = location;
        this.equipment = equipment;
    }

    public Integer getEquipmentModelId() {
        return equipmentModelId;
    }

    public void setEquipmentModelId(Integer equipmentModelId) {
        this.equipmentModelId = equipmentModelId;
    }

    public String getEquipmentModel() {
        return equipmentModel;
    }

    public void setEquipmentModel(String equipmentModel) {
        this.equipmentModel = equipmentModel;
    }

    public String getEquipmentDescription() {
        return equipmentDescription;
    }

    public void setEquipmentDescription(String equipmentDescription) {
        this.equipmentDescription = equipmentDescription;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
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

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }
}

