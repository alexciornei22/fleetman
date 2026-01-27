package ro.upb.acs.fleetman.trip;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripDto createTrip(@Validated @RequestBody CreateTripDto createTripDto) {
        return tripService.createTrip(createTripDto);
    }

    @GetMapping
    public PaginatedResponseDto<TripDto> getAllTrips(Pageable pageable) {
        return tripService.getAllTrips(pageable);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public PaginatedResponseDto<TripDto> getTripsForVehicle(@PathVariable Long vehicleId, Pageable pageable) {
        return tripService.getTripsForVehicle(vehicleId, pageable);
    }

    @GetMapping("/driver/{driverId}")
    public PaginatedResponseDto<TripDto> getTripsForDriver(@PathVariable Long driverId, Pageable pageable) {
        return tripService.getTripsForDriver(driverId, pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
    }
}
