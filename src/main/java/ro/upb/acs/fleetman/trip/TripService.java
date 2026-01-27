package ro.upb.acs.fleetman.trip;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.common.PaginationMapper;
import ro.upb.acs.fleetman.employee.driver.Driver;
import ro.upb.acs.fleetman.employee.driver.DriverRepository;
import ro.upb.acs.fleetman.exception.InvalidResourceReferenceException;
import ro.upb.acs.fleetman.exception.ResourceNotFoundException;
import ro.upb.acs.fleetman.vehicle.base.Vehicle;
import ro.upb.acs.fleetman.vehicle.base.VehicleRepository;

@Service
public class TripService {

    private final TripMapper tripMapper;
    private final PaginationMapper paginationMapper;
    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public TripService(TripMapper tripMapper, PaginationMapper paginationMapper, TripRepository tripRepository,
                       VehicleRepository vehicleRepository, DriverRepository driverRepository) {
        this.tripMapper = tripMapper;
        this.paginationMapper = paginationMapper;
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    public TripDto createTrip(CreateTripDto createTripDto) {
        Vehicle vehicle = vehicleRepository.findById(createTripDto.vehicleId())
            .orElseThrow(() -> new InvalidResourceReferenceException("Vehicle", createTripDto.vehicleId().toString()));

        Driver driver = driverRepository.findById(createTripDto.driverId())
            .orElseThrow(() -> new InvalidResourceReferenceException("Driver", createTripDto.driverId().toString()));

        var trip = tripMapper.toEntity(createTripDto, vehicle, driver);
        return tripMapper.toDto(tripRepository.save(trip));
    }

    public PaginatedResponseDto<TripDto> getAllTrips(Pageable pageable) {
        Page<TripDto> page = tripRepository.findAll(pageable).map(tripMapper::toDto);
        return paginationMapper.toPaginatedResponse(page);
    }

    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
    }

    public PaginatedResponseDto<TripDto> getTripsForVehicle(Long vehicleId, Pageable pageable) {
        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", vehicleId.toString()));

        Page<TripDto> page = tripRepository.findByVehicleId(vehicleId, pageable).map(tripMapper::toDto);
        return paginationMapper.toPaginatedResponse(page);
    }

    public PaginatedResponseDto<TripDto> getTripsForDriver(Long driverId, Pageable pageable) {
        driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", driverId.toString()));

        Page<TripDto> page = tripRepository.findByDriverId(driverId, pageable).map(tripMapper::toDto);
        return paginationMapper.toPaginatedResponse(page);
    }
}
