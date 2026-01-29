package ro.upb.acs.fleetman.vehicle.truck;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.config.LogResponse;

@RestController
@RequestMapping("/api/trucks")
public class TruckController {

    private final TruckService truckService;

    public TruckController(TruckService truckService) {
        this.truckService = truckService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LogResponse
    public TruckDto createTruck(@Validated @RequestBody CreateTruckDto createTruckDto) {
        return truckService.createTruck(createTruckDto);
    }

    @PutMapping("/{id}")
    @LogResponse
    public TruckDto updateTruck(@PathVariable Long id, @Validated @RequestBody UpdateTruckDto updateTruckDto) {
        return truckService.updateTruck(id, updateTruckDto);
    }

    @GetMapping
    @LogResponse
    public PaginatedResponseDto<TruckDto> getAllTrucks(Pageable pageable) {
        return truckService.getAllTrucks(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @LogResponse
    public void deleteTruck(@PathVariable Long id) {
        truckService.deleteTruck(id);
    }
}
