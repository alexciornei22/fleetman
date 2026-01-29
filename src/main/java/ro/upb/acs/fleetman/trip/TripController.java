package ro.upb.acs.fleetman.trip;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.config.LogResponse;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LogResponse
    public TripDto createTrip(@Validated @RequestBody CreateTripDto createTripDto) {
        return tripService.createTrip(createTripDto);
    }

    @PutMapping("/{id}")
    @LogResponse
    public TripDto updateTrip(@PathVariable Long id, @Validated @RequestBody UpdateTripDto updateTripDto) {
        return tripService.updateTrip(id, updateTripDto);
    }

    @GetMapping
    @LogResponse
    public PaginatedResponseDto<TripDto> getAllTrips(Pageable pageable) {
        return tripService.getAllTrips(pageable);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @LogResponse
    public PaginatedResponseDto<TripDto> getTripsForVehicle(@PathVariable Long vehicleId, Pageable pageable) {
        return tripService.getTripsForVehicle(vehicleId, pageable);
    }

    @GetMapping("/driver/{driverId}")
    @LogResponse
    public PaginatedResponseDto<TripDto> getTripsForDriver(@PathVariable Long driverId, Pageable pageable) {
        return tripService.getTripsForDriver(driverId, pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @LogResponse
    public void deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
    }
}
