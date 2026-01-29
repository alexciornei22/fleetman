package ro.upb.acs.fleetman.trip;

import org.springframework.stereotype.Component;
import ro.upb.acs.fleetman.employee.driver.Driver;
import ro.upb.acs.fleetman.vehicle.base.Vehicle;

@Component
public class TripMapper {

    public Trip toEntity(CreateTripDto request, Vehicle vehicle, Driver driver) {
        Trip trip = new Trip();
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setStartLocation(request.startLocation());
        trip.setEndLocation(request.endLocation());
        trip.setStartTime(request.startTime());
        trip.setEndTime(request.endTime());
        trip.setDistanceKm(request.distanceKm());
        trip.setStatus(request.status());
        trip.setNotes(request.notes());
        return trip;
    }

    public void updateEntity(UpdateTripDto request, Trip trip, Vehicle vehicle, Driver driver) {
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setStartLocation(request.startLocation());
        trip.setEndLocation(request.endLocation());
        trip.setStartTime(request.startTime());
        trip.setEndTime(request.endTime());
        trip.setDistanceKm(request.distanceKm());
        trip.setStatus(request.status());
        trip.setNotes(request.notes());
    }

    public TripDto toDto(Trip trip) {
        return new TripDto(
            trip.getId(),
            trip.getVehicle().getId(),
            trip.getDriver().getId(),
            trip.getStartLocation(),
            trip.getEndLocation(),
            trip.getStartTime(),
            trip.getEndTime(),
            trip.getDistanceKm(),
            trip.getStatus(),
            trip.getNotes()
        );
    }
}
