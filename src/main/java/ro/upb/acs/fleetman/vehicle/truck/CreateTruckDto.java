package ro.upb.acs.fleetman.vehicle.truck;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;

import java.io.Serializable;

public record CreateTruckDto(
    @NotNull String vin,
    String licensePlate,
    @Min(0) Integer mileage,
    @Valid @NotNull PowertrainInformationDto powertrainInformation,
    @Min(1) Integer maxLoadKg,
    @Min(2) Integer numberOfAxles,
    @NotNull Boolean hasRefrigerationUnit,
    @NotNull Long fleetManagerId
) implements Serializable { }
