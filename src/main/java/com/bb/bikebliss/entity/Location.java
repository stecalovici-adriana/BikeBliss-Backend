package com.bb.bikebliss.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "locations")
@SuppressWarnings("unused")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer locationId;

    @Column(name = "address", nullable = false)
    private String address;
    @OneToMany(mappedBy = "location")
    private List<BikeModel> bikeModels;

    @OneToMany(mappedBy = "location")
    private List<EquipmentModel> equipmentModels;

    public Location(){}

    public Location(Integer locationId, String address, List<BikeModel> bikeModels, List<EquipmentModel> equipmentModels) {
        this.locationId = locationId;
        this.address = address;
        this.bikeModels = bikeModels;
        this.equipmentModels = equipmentModels;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<BikeModel> getBikeModels() {
        return bikeModels;
    }

    public void setBikeModels(List<BikeModel> bikeModels) {
        this.bikeModels = bikeModels;
    }

    public List<EquipmentModel> getEquipmentModels() {
        return equipmentModels;
    }

    public void setEquipmentModels(List<EquipmentModel> equipmentModels) {
        this.equipmentModels = equipmentModels;
    }
}
