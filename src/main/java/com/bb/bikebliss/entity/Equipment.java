package com.bb.bikebliss.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "equipments")
@SuppressWarnings("unused")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer equipmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_status", nullable = false)
    private EquipmentStatus equipmentStatus;

    @ManyToOne
    @JoinColumn(name = "equipment_model_id", referencedColumnName = "equipmentModelId", nullable = false)
    private EquipmentModel equipmentModel;

    @OneToMany(mappedBy = "equipment")
    private List<EquipmentRental> equipmentRentals;

    public Equipment() {}

    public Equipment(Integer equipmentId, EquipmentStatus equipmentStatus, EquipmentModel equipmentModel, List<EquipmentRental> equipmentRentals) {
        this.equipmentId = equipmentId;
        this.equipmentStatus = equipmentStatus;
        this.equipmentModel = equipmentModel;
        this.equipmentRentals = equipmentRentals;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public EquipmentStatus getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(EquipmentStatus equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public EquipmentModel getEquipmentModel() {
        return equipmentModel;
    }

    public void setEquipmentModel(EquipmentModel equipmentModel) {
        this.equipmentModel = equipmentModel;
    }

    public List<EquipmentRental> getEquipmentRentals() {
        return equipmentRentals;
    }

    public void setEquipmentRentals(List<EquipmentRental> equipmentRentals) {
        this.equipmentRentals = equipmentRentals;
    }
}
