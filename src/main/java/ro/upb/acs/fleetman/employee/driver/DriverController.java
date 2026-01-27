package ro.upb.acs.fleetman.employee.driver;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverDto createDriver(@Validated @RequestBody CreateDriverDto createDriverDto) {
        return driverService.createDriver(createDriverDto);
    }

    @GetMapping
    public PaginatedResponseDto<DriverDto> getAllDrivers(Pageable pageable) {
        return driverService.getAllDrivers(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
    }
}
