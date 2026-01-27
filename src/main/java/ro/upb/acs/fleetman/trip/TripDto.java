package ro.upb.acs.fleetman.trip;

import java.io.Serializable;
import java.time.LocalDateTime;

public record TripDto(
    Long id,
    Long vehicleId,
    Long driverId,
    String startLocation,
    String endLocation,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Double distanceKm,
    TripStatus status,
    String notes
) implements Serializable { }
