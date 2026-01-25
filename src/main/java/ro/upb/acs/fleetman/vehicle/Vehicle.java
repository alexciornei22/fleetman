package ro.upb.acs.fleetman.vehicle;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Vehicle {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "vin", unique = true, nullable = false)
    private String vin;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "mileage")
    private Integer mileage;

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
}
