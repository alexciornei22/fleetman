package ro.upb.acs.fleetman.employee.fleetmanager;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;

@RestController
@RequestMapping("/api/fleet-managers")
public class FleetManagerController {

    private final FleetManagerService fleetManagerService;

    public FleetManagerController(FleetManagerService fleetManagerService) {
        this.fleetManagerService = fleetManagerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FleetManagerDto createFleetManager(@Validated @RequestBody CreateFleetManagerDto createFleetManagerDto) {
        return fleetManagerService.createFleetManager(createFleetManagerDto);
    }

    @GetMapping
    public PaginatedResponseDto<FleetManagerDto> getAllFleetManagers(Pageable pageable) {
        return fleetManagerService.getAllFleetManagers(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFleetManager(@PathVariable Long id) {
        fleetManagerService.deleteFleetManager(id);
    }
}
