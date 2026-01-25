package ro.upb.acs.fleetman.vehicle.car;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ro.upb.acs.fleetman.exception.FieldConflictException;

@Service
public class CarService {

    private final CarMapper carMapper;
    private final CarRepository carRepository;

    private static final String VIN_UNIQUE_INDEX = "uc_vehicle_vin";
    private static final String LICENSE_PLATE_UNIQUE_INDEX = "uc_vehicle_license_plate";

    private static final String VIN_CONFLICT_ERROR_FIELD = "vin";
    private static final String VIN_CONFLICT_ERROR_MESSAGE = "A car with the provided VIN already exists.";
    private static final String LICENSE_PLATE_CONFLICT_ERROR_FIELD = "licensePlate";
    private static final String LICENSE_PLATE_CONFLICT_ERROR_MESSAGE = "A car with the provided license plate already exists.";

    public CarService(CarMapper carMapper, CarRepository carRepository) {
        this.carMapper = carMapper;
        this.carRepository = carRepository;
    }

    public CarDto createCar(CreateCarDto createCarDto) {
        var car = carMapper.toEntity(createCarDto);

        try {
            return carMapper.toDto(carRepository.save(car));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(VIN_UNIQUE_INDEX)) {
                throw new FieldConflictException(VIN_CONFLICT_ERROR_FIELD, VIN_CONFLICT_ERROR_MESSAGE);
            }
            if (e.getMessage().contains(LICENSE_PLATE_UNIQUE_INDEX)) {
                throw new FieldConflictException(LICENSE_PLATE_CONFLICT_ERROR_FIELD, LICENSE_PLATE_CONFLICT_ERROR_MESSAGE);
            }
            throw new RuntimeException(e);
        }
    }
}
