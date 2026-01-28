package ro.upb.acs.fleetman.vehicle.car;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;

import java.io.Serializable;

public record CreateCarDto(
    @NotNull String vin,
    String licensePlate,
    @Min(0) Integer mileage,
    @Valid @NotNull PowertrainInformationDto powertrainInformation,
    @Min(1) Integer numberOfSeats,
    @Min(1) Integer numberOfDoors,
    @NotNull Boolean isChildSeatCompatible,
    @NotNull Boolean hasSunroof,
    @NotNull CarBodyType carBodyType,
    @NotNull Long fleetManagerId
) implements Serializable { }
