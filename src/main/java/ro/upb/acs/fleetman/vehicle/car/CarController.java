package ro.upb.acs.fleetman.vehicle.car;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.config.LogResponse;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LogResponse
    public CarDto createCar(@Validated @RequestBody CreateCarDto createCarDto) {
        return carService.createCar(createCarDto);
    }

    @PutMapping("/{id}")
    @LogResponse
    public CarDto updateCar(@PathVariable Long id, @Validated @RequestBody UpdateCarDto updateCarDto) {
        return carService.updateCar(id, updateCarDto);
    }

    @GetMapping
    @LogResponse
    public PaginatedResponseDto<CarDto> getAllCars(Pageable pageable) {
        return carService.getAllCars(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @LogResponse
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
