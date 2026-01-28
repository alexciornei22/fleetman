package ro.upb.acs.fleetman.employee.fleetmanager;

import jakarta.persistence.*;
import ro.upb.acs.fleetman.employee.Employee;
import ro.upb.acs.fleetman.vehicle.base.Vehicle;

import java.util.List;

@Entity
@Table(name = "fleet_manager")
public class FleetManager extends Employee {

    @OneToMany(mappedBy = "fleetManager", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> managedVehicles;

    public List<Vehicle> getManagedVehicles() {
        return managedVehicles;
    }

    public void setManagedVehicles(List<Vehicle> managedVehicles) {
        this.managedVehicles = managedVehicles;
    }

    public Integer getVehicleCount() {
        return managedVehicles != null ? managedVehicles.size() : 0;
    }
}
