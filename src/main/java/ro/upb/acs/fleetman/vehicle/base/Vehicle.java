package ro.upb.acs.fleetman.vehicle.base;

import jakarta.persistence.*;
import ro.upb.acs.fleetman.trip.Trip;

import java.util.List;

@Entity
@Table(name = "vehicle")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Vehicle {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "vin", unique = true, nullable = false)
    private String vin;

    @Column(name = "license_plate", unique = true)
    private String licensePlate;

    @Column(name = "mileage")
    private Integer mileage;

    @Embedded
    private PowertrainInformation powertrainInformation;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trip> trips;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public PowertrainInformation getPowertrainInformation() {
        return powertrainInformation;
    }

    public void setPowertrainInformation(PowertrainInformation powertrainInformation) {
        this.powertrainInformation = powertrainInformation;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }
}
