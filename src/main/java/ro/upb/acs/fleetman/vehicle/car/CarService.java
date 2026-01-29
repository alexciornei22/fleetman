package ro.upb.acs.fleetman.vehicle.car;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.common.PaginationMapper;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManagerRepository;
import ro.upb.acs.fleetman.exception.FieldConflictException;
import ro.upb.acs.fleetman.exception.InvalidResourceReferenceException;
import ro.upb.acs.fleetman.exception.ResourceNotFoundException;

@Service
public class CarService {

    private final CarMapper carMapper;
    private final PaginationMapper paginationMapper;
    private final CarRepository carRepository;
    private final FleetManagerRepository fleetManagerRepository;

    private static final String VIN_UNIQUE_INDEX = "uc_vehicle_vin";
    private static final String LICENSE_PLATE_UNIQUE_INDEX = "uc_vehicle_license_plate";

    private static final String VIN_CONFLICT_ERROR_FIELD = "vin";
    private static final String VIN_CONFLICT_ERROR_MESSAGE = "A car with the provided VIN already exists.";
    private static final String LICENSE_PLATE_CONFLICT_ERROR_FIELD = "licensePlate";
    private static final String LICENSE_PLATE_CONFLICT_ERROR_MESSAGE = "A car with the provided license plate already exists.";

    public CarService(CarMapper carMapper, PaginationMapper paginationMapper, CarRepository carRepository, FleetManagerRepository fleetManagerRepository) {
        this.carMapper = carMapper;
        this.paginationMapper = paginationMapper;
        this.carRepository = carRepository;
        this.fleetManagerRepository = fleetManagerRepository;
    }

    public CarDto createCar(CreateCarDto createCarDto) {
        if (!fleetManagerRepository.existsById(createCarDto.fleetManagerId())) {
            throw new InvalidResourceReferenceException("FleetManager", createCarDto.fleetManagerId().toString());
        }

        var car = carMapper.toEntity(createCarDto);

        try {
            return carMapper.toDto(carRepository.save(car));
        } catch (DataIntegrityViolationException e) {
            throw handleUniqueConstraint(e);
        }
    }

    public CarDto updateCar(Long id, UpdateCarDto updateCarDto) {
        Car car = carRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Car", id.toString()));

        carMapper.toEntity(updateCarDto, car);

        try {
            return carMapper.toDto(carRepository.save(car));
        } catch (DataIntegrityViolationException e) {
            throw handleUniqueConstraint(e);
        }
    }

    public PaginatedResponseDto<CarDto> getAllCars(Pageable pageable) {
        Page<CarDto> page = carRepository.findAll(pageable).map(carMapper::toDto);
        return paginationMapper.toPaginatedResponse(page);
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    private FieldConflictException handleUniqueConstraint(DataIntegrityViolationException e) {
        if (e.getMessage().contains(VIN_UNIQUE_INDEX)) {
            return new FieldConflictException(VIN_CONFLICT_ERROR_FIELD, VIN_CONFLICT_ERROR_MESSAGE);
        }
        if (e.getMessage().contains(LICENSE_PLATE_UNIQUE_INDEX)) {
            return new FieldConflictException(LICENSE_PLATE_CONFLICT_ERROR_FIELD, LICENSE_PLATE_CONFLICT_ERROR_MESSAGE);
        }
        throw new RuntimeException(e);
    }
}
