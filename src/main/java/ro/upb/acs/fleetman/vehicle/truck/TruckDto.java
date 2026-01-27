package ro.upb.acs.fleetman.vehicle.truck;

import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;

import java.io.Serializable;

public record TruckDto(
    Long id,
    String vin,
    String licensePlate,
    Integer mileage,
    PowertrainInformationDto powertrainInformation,
    Integer maxLoadKg,
    Integer numberOfAxles,
    Boolean hasRefrigerationUnit
) implements Serializable { }
