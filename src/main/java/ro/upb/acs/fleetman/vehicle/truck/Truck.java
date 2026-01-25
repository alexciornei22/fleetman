package ro.upb.acs.fleetman.vehicle.truck;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ro.upb.acs.fleetman.vehicle.base.Vehicle;

@Entity
@Table(name = "truck")
public class Truck extends Vehicle {

    @Column(name = "max_load_kg", nullable = false)
    private Integer maxLoadKg;

    @Column(name = "number_of_axles", nullable = false)
    private Integer numberOfAxles;

    @Column(name = "has_refrigeration_unit", nullable = false)
    private Boolean hasRefrigerationUnit;

    public Integer getMaxLoadKg() {
        return maxLoadKg;
    }

    public void setMaxLoadKg(Integer maxLoadKg) {
        this.maxLoadKg = maxLoadKg;
    }

    public Integer getNumberOfAxles() {
        return numberOfAxles;
    }

    public void setNumberOfAxles(Integer numberOfAxles) {
        this.numberOfAxles = numberOfAxles;
    }

    public Boolean getHasRefrigerationUnit() {
        return hasRefrigerationUnit;
    }

    public void setHasRefrigerationUnit(Boolean hasRefrigerationUnit) {
        this.hasRefrigerationUnit = hasRefrigerationUnit;
    }
}
