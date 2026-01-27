package ro.upb.acs.fleetman.vehicle.base;

import java.io.Serializable;

public record VehicleDto(
    Long id,
    String vin,
    String licensePlate,
    Integer mileage,
    PowertrainInformationDto powertrainInformation,
    String vehicleType
) implements Serializable { }
