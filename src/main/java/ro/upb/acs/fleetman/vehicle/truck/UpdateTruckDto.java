package ro.upb.acs.fleetman.vehicle.truck;

import jakarta.validation.constraints.NotNull;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;

import java.io.Serializable;

public record UpdateTruckDto(
    @NotNull String vin,
    String licensePlate,
    @NotNull Integer mileage,
    @NotNull PowertrainInformationDto powertrainInformation,
    Integer maxLoadKg,
    Integer numberOfAxles,
    @NotNull Boolean hasRefrigerationUnit
) implements Serializable { }
