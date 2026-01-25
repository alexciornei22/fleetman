package ro.upb.acs.fleetman.vehicle.base;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record PowertrainInformationDto(
    @NotNull EngineType engineType,
    @NotNull Integer horsepower,
    @NotNull Integer fuelCapacityLiters,
    @NotNull Integer engineDisplacementCc,
    @NotNull Boolean hasAutomaticTransmission
) implements Serializable { }
