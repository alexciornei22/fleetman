package ro.upb.acs.fleetman.vehicle.car;

import jakarta.persistence.*;
import ro.upb.acs.fleetman.vehicle.base.Vehicle;

@Entity
@Table(name = "car")
public class Car extends Vehicle {

    @Column(name = "number_of_seats", nullable = false)
    private Integer numberOfSeats;

    @Column(name = "number_of_doors", nullable = false)
    private Integer numberOfDoors;

    @Column(name = "is_child_seat_compatible", nullable = false)
    private Boolean isChildSeatCompatible;

    @Column(name = "has_sunroof", nullable = false)
    private Boolean hasSunroof;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_body_type", nullable = false, length = 30)
    private CarBodyType carBodyType;

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Integer getNumberOfDoors() {
        return numberOfDoors;
    }

    public void setNumberOfDoors(Integer numberOfDoors) {
        this.numberOfDoors = numberOfDoors;
    }

    public Boolean isChildSeatCompatible() {
        return isChildSeatCompatible;
    }

    public void setIsChildSeatCompatible(Boolean childSeatCompatible) {
        isChildSeatCompatible = childSeatCompatible;
    }

    public Boolean getHasSunroof() {
        return hasSunroof;
    }

    public void setHasSunroof(Boolean hasSunroof) {
        this.hasSunroof = hasSunroof;
    }

    public CarBodyType getCarBodyType() {
        return carBodyType;
    }

    public void setCarBodyType(CarBodyType carBodyType) {
        this.carBodyType = carBodyType;
    }
}
