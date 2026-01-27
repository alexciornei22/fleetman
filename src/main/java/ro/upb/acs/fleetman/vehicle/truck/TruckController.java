package ro.upb.acs.fleetman.vehicle.truck;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;

@RestController
@RequestMapping("/api/trucks")
public class TruckController {

    private final TruckService truckService;

    public TruckController(TruckService truckService) {
        this.truckService = truckService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TruckDto createTruck(@Validated @RequestBody CreateTruckDto createTruckDto) {
        return truckService.createTruck(createTruckDto);
    }

    @GetMapping
    public PaginatedResponseDto<TruckDto> getAllTrucks(Pageable pageable) {
        return truckService.getAllTrucks(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTruck(@PathVariable Long id) {
        truckService.deleteTruck(id);
    }
}
