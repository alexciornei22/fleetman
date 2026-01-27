package ro.upb.acs.fleetman.trip;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CreateTripDto(
    @NotNull Long vehicleId,
    @NotNull Long driverId,
    @NotNull String startLocation,
    @NotNull String endLocation,
    @NotNull LocalDateTime startTime,
    LocalDateTime endTime,
    @Min(0) Double distanceKm,
    @NotNull TripStatus status,
    String notes
) implements Serializable { }
