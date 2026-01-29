package ro.upb.acs.fleetman.vehicle.car;

import jakarta.validation.constraints.NotNull;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;

import java.io.Serializable;

public record UpdateCarDto(
    @NotNull String vin,
    @NotNull String licensePlate,
    @NotNull Integer mileage,
    @NotNull PowertrainInformationDto powertrainInformation,
    @NotNull Integer numberOfSeats,
    @NotNull Integer numberOfDoors,
    @NotNull Boolean isChildSeatCompatible,
    @NotNull Boolean hasSunroof,
    @NotNull CarBodyType carBodyType
) implements Serializable { }
