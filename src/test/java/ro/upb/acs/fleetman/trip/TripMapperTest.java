package ro.upb.acs.fleetman.trip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ro.upb.acs.fleetman.employee.driver.Driver;
import ro.upb.acs.fleetman.vehicle.base.Vehicle;
import ro.upb.acs.fleetman.vehicle.car.Car;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TripMapper Unit Tests")
class TripMapperTest {

    private TripMapper mapper;
    private Vehicle vehicle;
    private Driver driver;

    @BeforeEach
    void setUp() {
        mapper = new TripMapper();
        vehicle = new Car();
        vehicle.setId(1L);
        driver = new Driver();
        driver.setId(2L);
    }

    @Nested
    @DisplayName("toEntity Tests")
    class ToEntityTests {

        @ParameterizedTest(name = "{index} - {0} -> {1}")
        @CsvSource({
            "CityA,CityB",
            "Start,End",
            "Home,Office"
        })
        void shouldMapCreateTripDtoToTripEntity(String start, String end) {
            CreateTripDto request = new CreateTripDto(
                vehicle.getId(),
                driver.getId(),
                start,
                end,
                LocalDateTime.of(2026, 1, 1, 8, 0),
                LocalDateTime.of(2026, 1, 1, 9, 30),
                120.0,
                TripStatus.COMPLETED,
                "Notes"
            );

            Trip trip = mapper.toEntity(request, vehicle, driver);

            assertNotNull(trip);
            assertEquals(vehicle, trip.getVehicle());
            assertEquals(driver, trip.getDriver());
            assertEquals(start, trip.getStartLocation());
            assertEquals(end, trip.getEndLocation());
            assertEquals(TripStatus.COMPLETED, trip.getStatus());
        }
    }

    @Nested
    @DisplayName("toDto Tests")
    class ToDtoTests {

        @ParameterizedTest(name = "{index} - {0} -> {1}")
        @CsvSource({
            "CityA,CityB",
            "Start,End",
            "Home,Office"
        })
        void shouldMapTripEntityToDto(String start, String end) {
            Trip trip = new Trip();
            trip.setId(5L);
            trip.setVehicle(vehicle);
            trip.setDriver(driver);
            trip.setStartLocation(start);
            trip.setEndLocation(end);
            trip.setStartTime(LocalDateTime.of(2026, 1, 1, 8, 0));
            trip.setEndTime(LocalDateTime.of(2026, 1, 1, 9, 30));
            trip.setDistanceKm(120.0);
            trip.setStatus(TripStatus.COMPLETED);
            trip.setNotes("Notes");

            TripDto dto = mapper.toDto(trip);

            assertNotNull(dto);
            assertEquals(trip.getId(), dto.id());
            assertEquals(vehicle.getId(), dto.vehicleId());
            assertEquals(driver.getId(), dto.driverId());
            assertEquals(start, dto.startLocation());
            assertEquals(end, dto.endLocation());
            assertEquals(TripStatus.COMPLETED, dto.status());
        }
    }
}
