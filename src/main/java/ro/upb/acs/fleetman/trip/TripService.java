package ro.upb.acs.fleetman.trip;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.common.PaginationMapper;
import ro.upb.acs.fleetman.employee.driver.Driver;
import ro.upb.acs.fleetman.employee.driver.DriverRepository;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManager;
import ro.upb.acs.fleetman.exception.InvalidResourceReferenceException;
import ro.upb.acs.fleetman.exception.ResourceNotFoundException;
import ro.upb.acs.fleetman.trip.event.TripCreatedEvent;
import ro.upb.acs.fleetman.vehicle.base.Vehicle;
import ro.upb.acs.fleetman.vehicle.base.VehicleRepository;

@Service
public class TripService {

    private final TripMapper tripMapper;
    private final PaginationMapper paginationMapper;
    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TripService(TripMapper tripMapper, PaginationMapper paginationMapper, TripRepository tripRepository,
                       VehicleRepository vehicleRepository, DriverRepository driverRepository, ApplicationEventPublisher eventPublisher) {
        this.tripMapper = tripMapper;
        this.paginationMapper = paginationMapper;
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.eventPublisher = eventPublisher;
    }

    public TripDto createTrip(CreateTripDto createTripDto) {
        Vehicle vehicle = vehicleRepository.findById(createTripDto.vehicleId())
            .orElseThrow(() -> new InvalidResourceReferenceException("Vehicle", createTripDto.vehicleId().toString()));

        Driver driver = driverRepository.findById(createTripDto.driverId())
            .orElseThrow(() -> new InvalidResourceReferenceException("Driver", createTripDto.driverId().toString()));

        var trip = tripMapper.toEntity(createTripDto, vehicle, driver);
        Trip newTrip = tripRepository.save(trip);

        FleetManager fleetManager = vehicle.getFleetManager();
        eventPublisher.publishEvent(new TripCreatedEvent(newTrip, driver, fleetManager));

        return tripMapper.toDto(newTrip);
    }

    public TripDto updateTrip(Long id, UpdateTripDto updateTripDto) {
        Trip trip = tripRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Trip", id.toString()));

        Vehicle vehicle = vehicleRepository.findById(updateTripDto.vehicleId())
            .orElseThrow(() -> new InvalidResourceReferenceException("Vehicle", updateTripDto.vehicleId().toString()));

        Driver driver = driverRepository.findById(updateTripDto.driverId())
            .orElseThrow(() -> new InvalidResourceReferenceException("Driver", updateTripDto.driverId().toString()));

        tripMapper.updateEntity(updateTripDto, trip, vehicle, driver);
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
