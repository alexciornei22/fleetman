package ro.upb.acs.fleetman.trip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ro.upb.acs.fleetman.vehicle.car.Car;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripService Unit Tests")
class TripServiceTest {

    @Mock
    private TripMapper tripMapper;

    @Mock
    private PaginationMapper paginationMapper;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TripService tripService;

    private Vehicle vehicle;
    private Driver driver;
    private FleetManager fleetManager;
    private TripDto tripDto;
    private Trip trip;

    @BeforeEach
    void setUp() {
        vehicle = new Car();
        vehicle.setId(1L);
        fleetManager = new FleetManager();
        fleetManager.setId(10L);
        vehicle.setFleetManager(fleetManager);

        driver = new Driver();
        driver.setId(2L);

        trip = new Trip();
        trip.setId(3L);
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setStartLocation("A");
        trip.setEndLocation("B");
        trip.setStartTime(LocalDateTime.of(2026, 1, 1, 8, 0));
        trip.setEndTime(LocalDateTime.of(2026, 1, 1, 9, 0));
        trip.setDistanceKm(120.0);
        trip.setStatus(TripStatus.COMPLETED);

        tripDto = new TripDto(
            3L,
            vehicle.getId(),
            driver.getId(),
            trip.getStartLocation(),
            trip.getEndLocation(),
            trip.getStartTime(),
            trip.getEndTime(),
            trip.getDistanceKm(),
            trip.getStatus(),
            "Notes"
        );
    }

    @Nested
    @DisplayName("createTrip Tests")
    class CreateTripTests {

        @Test
        @DisplayName("Should create trip when vehicle and driver exist")
        void shouldCreateTripSuccessfully() {
            CreateTripDto request = createTripDto();

            when(vehicleRepository.findById(request.vehicleId())).thenReturn(Optional.of(vehicle));
            when(driverRepository.findById(request.driverId())).thenReturn(Optional.of(driver));
            when(tripMapper.toEntity(request, vehicle, driver)).thenReturn(trip);
            when(tripRepository.save(trip)).thenReturn(trip);
            when(tripMapper.toDto(trip)).thenReturn(tripDto);

            TripDto result = tripService.createTrip(request);

            assertEquals(tripDto, result);

            ArgumentCaptor<TripCreatedEvent> captor = ArgumentCaptor.forClass(TripCreatedEvent.class);
            verify(eventPublisher).publishEvent(captor.capture());
            TripCreatedEvent event = captor.getValue();
            assertEquals(trip, event.trip());
            assertEquals(driver, event.driver());
            assertEquals(fleetManager, event.fleetManager());
        }

        @Test
        @DisplayName("Should throw when vehicle missing")
        void shouldThrowWhenVehicleMissing() {
            CreateTripDto request = createTripDto();
            when(vehicleRepository.findById(request.vehicleId())).thenReturn(Optional.empty());

            InvalidResourceReferenceException exception = assertThrows(
                InvalidResourceReferenceException.class,
                () -> tripService.createTrip(request)
            );

            assertEquals("Vehicle", exception.getResourceType());
        }

        @Test
        @DisplayName("Should throw when driver missing")
        void shouldThrowWhenDriverMissing() {
            CreateTripDto request = createTripDto();
            when(vehicleRepository.findById(request.vehicleId())).thenReturn(Optional.of(vehicle));
            when(driverRepository.findById(request.driverId())).thenReturn(Optional.empty());

            InvalidResourceReferenceException exception = assertThrows(
                InvalidResourceReferenceException.class,
                () -> tripService.createTrip(request)
            );

            assertEquals("Driver", exception.getResourceType());
        }
    }

    @Nested
    @DisplayName("getAllTrips Tests")
    class GetAllTripsTests {

        @Test
        @DisplayName("Should return paginated trips")
        void shouldReturnPaginatedTrips() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Trip> page = new PageImpl<>(List.of(trip), pageable, 1);
            PaginatedResponseDto<TripDto> expected = new PaginatedResponseDto<>(
                List.of(tripDto),
                0,
                10,
                1,
                1,
                false,
                false
            );

            when(tripRepository.findAll(pageable)).thenReturn(page);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(expected);

            PaginatedResponseDto<TripDto> result = tripService.getAllTrips(pageable);

            assertEquals(expected, result);
        }
    }

    @Nested
    @DisplayName("getTripsForVehicle Tests")
    class GetTripsForVehicleTests {

        @Test
        @DisplayName("Should return trips for vehicle")
        void shouldReturnTripsForVehicle() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Trip> page = new PageImpl<>(List.of(trip), pageable, 1);
            PaginatedResponseDto<TripDto> expected = new PaginatedResponseDto<>(
                List.of(tripDto),
                0,
                10,
                1,
                1,
                false,
                false
            );

            when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
            when(tripRepository.findByVehicleId(1L, pageable)).thenReturn(page);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(expected);

            PaginatedResponseDto<TripDto> result = tripService.getTripsForVehicle(1L, pageable);
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should throw when vehicle missing")
        void shouldThrowWhenVehicleMissing() {
            Pageable pageable = PageRequest.of(0, 10);
            when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> tripService.getTripsForVehicle(1L, pageable));
        }
    }

    @Nested
    @DisplayName("getTripsForDriver Tests")
    class GetTripsForDriverTests {

        @Test
        @DisplayName("Should return trips for driver")
        void shouldReturnTripsForDriver() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Trip> page = new PageImpl<>(List.of(trip), pageable, 1);
            PaginatedResponseDto<TripDto> expected = new PaginatedResponseDto<>(
                List.of(tripDto),
                0,
                10,
                1,
                1,
                false,
                false
            );

            when(driverRepository.findById(2L)).thenReturn(Optional.of(driver));
            when(tripRepository.findByDriverId(2L, pageable)).thenReturn(page);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(expected);

            PaginatedResponseDto<TripDto> result = tripService.getTripsForDriver(2L, pageable);
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should throw when driver missing")
        void shouldThrowWhenDriverMissing() {
            Pageable pageable = PageRequest.of(0, 10);
            when(driverRepository.findById(2L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> tripService.getTripsForDriver(2L, pageable));
        }
    }

    @Nested
    @DisplayName("deleteTrip Tests")
    class DeleteTripTests {

        @Test
        @DisplayName("Should delete trip by id")
        void shouldDeleteTripById() {
            Long id = 3L;
            doNothing().when(tripRepository).deleteById(id);

            tripService.deleteTrip(id);

            verify(tripRepository).deleteById(id);
        }
    }

    @Nested
    @DisplayName("updateTrip Tests")
    class UpdateTripTests {

        @Test
        @DisplayName("Should update trip successfully")
        void shouldUpdateTripSuccessfully() {
            UpdateTripDto request = updateTripDto();
            when(tripRepository.findById(3L)).thenReturn(Optional.of(trip));
            when(vehicleRepository.findById(request.vehicleId())).thenReturn(Optional.of(vehicle));
            when(driverRepository.findById(request.driverId())).thenReturn(Optional.of(driver));
            doNothing().when(tripMapper).updateEntity(request, trip, vehicle, driver);
            when(tripRepository.save(trip)).thenReturn(trip);
            when(tripMapper.toDto(trip)).thenReturn(tripDto);

            TripDto result = tripService.updateTrip(3L, request);

            assertEquals(tripDto, result);
            verify(tripRepository).findById(3L);
            verify(vehicleRepository).findById(request.vehicleId());
            verify(driverRepository).findById(request.driverId());
            verify(tripMapper).updateEntity(request, trip, vehicle, driver);
            verify(tripRepository).save(trip);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when trip missing")
        void shouldThrowResourceNotFoundWhenTripMissing() {
            when(tripRepository.findById(3L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> tripService.updateTrip(3L, updateTripDto()));

            verify(tripRepository).findById(3L);
            verify(vehicleRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Should throw InvalidResourceReferenceException when vehicle missing")
        void shouldThrowInvalidResourceReferenceWhenVehicleMissing() {
            UpdateTripDto request = updateTripDto();
            when(tripRepository.findById(3L)).thenReturn(Optional.of(trip));
            when(vehicleRepository.findById(request.vehicleId())).thenReturn(Optional.empty());

            assertThrows(InvalidResourceReferenceException.class, () -> tripService.updateTrip(3L, request));

            verify(vehicleRepository).findById(request.vehicleId());
            verify(driverRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Should throw InvalidResourceReferenceException when driver missing")
        void shouldThrowInvalidResourceReferenceWhenDriverMissing() {
            UpdateTripDto request = updateTripDto();
            when(tripRepository.findById(3L)).thenReturn(Optional.of(trip));
            when(vehicleRepository.findById(request.vehicleId())).thenReturn(Optional.of(vehicle));
            when(driverRepository.findById(request.driverId())).thenReturn(Optional.empty());

            assertThrows(InvalidResourceReferenceException.class, () -> tripService.updateTrip(3L, request));

            verify(driverRepository).findById(request.driverId());
            verify(tripRepository, never()).save(any());
        }
    }

    private CreateTripDto createTripDto() {
        return new CreateTripDto(
            vehicle.getId(),
            driver.getId(),
            "A",
            "B",
            LocalDateTime.of(2026, 1, 1, 8, 0),
            LocalDateTime.of(2026, 1, 1, 9, 0),
            120.0,
            TripStatus.COMPLETED,
            "Notes"
        );
    }

    private UpdateTripDto updateTripDto() {
        return new UpdateTripDto(
            vehicle.getId(),
            driver.getId(),
            "A",
            "C",
            LocalDateTime.of(2026, 1, 2, 8, 0),
            LocalDateTime.of(2026, 1, 2, 12, 0),
            200.0,
            TripStatus.IN_PROGRESS,
            "Updated notes"
        );
    }
}
