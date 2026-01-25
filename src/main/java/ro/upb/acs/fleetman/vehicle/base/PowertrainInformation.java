package ro.upb.acs.fleetman.vehicle.base;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class PowertrainInformation {

    @Enumerated(EnumType.STRING)
    @Column(name = "engine_type", nullable = false, length = 30)
    private EngineType engineType;

    @Column(name = "horsepower", nullable = false)
    private Integer horsepower;

    @Column(name = "fuel_capacity_liters", nullable = false)
    private Integer fuelCapacityLiters;

    @Column(name = "engine_displacement_cc", nullable = false)
    private Integer engineDisplacementCc;

    @Column(name = "has_automatic_transmission", nullable = false)
    private Boolean hasAutomaticTransmission;

    public EngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }

    public Integer getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(Integer horsepower) {
        this.horsepower = horsepower;
    }

    public Integer getFuelCapacityLiters() {
        return fuelCapacityLiters;
    }

    public void setFuelCapacityLiters(Integer fuelCapacityLiters) {
        this.fuelCapacityLiters = fuelCapacityLiters;
    }

    public Integer getEngineDisplacementCc() {
        return engineDisplacementCc;
    }

    public void setEngineDisplacementCc(Integer engineDisplacementCc) {
        this.engineDisplacementCc = engineDisplacementCc;
    }

    public Boolean getHasAutomaticTransmission() {
        return hasAutomaticTransmission;
    }

    public void setHasAutomaticTransmission(Boolean hasAutomaticTransmission) {
        this.hasAutomaticTransmission = hasAutomaticTransmission;
    }
}
