package ro.upb.acs.fleetman.trip.event;

import ro.upb.acs.fleetman.employee.driver.Driver;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManager;
import ro.upb.acs.fleetman.trip.Trip;

public record TripCreatedEvent(Trip trip, Driver driver, FleetManager fleetManager) { }
