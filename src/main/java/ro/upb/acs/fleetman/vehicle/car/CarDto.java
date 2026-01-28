package ro.upb.acs.fleetman.vehicle.car;

import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;

import java.io.Serializable;

public record CarDto(
    Long id,
    String vin,
    String licensePlate,
    Integer mileage,
    PowertrainInformationDto powertrainInformation,
    Integer numberOfSeats,
    Integer numberOfDoors,
    Boolean isChildSeatCompatible,
    Boolean hasSunroof,
    CarBodyType carBodyType,
    Long fleetManagerId
) implements Serializable { }