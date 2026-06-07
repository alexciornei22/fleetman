package ro.upb.acs.fleetman.report;

import java.io.Serializable;
import java.util.Map;

public record ReportDto(
    Double totalDistance,
    Integer totalTrips,
    Map<String, Long> tripsPerVehicle,
    Map<String, Double> distancePerDriver
) implements Serializable { }
