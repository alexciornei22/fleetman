package ro.upb.acs.fleetman.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.upb.acs.fleetman.employee.driver.Driver;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManagerRepository;
import ro.upb.acs.fleetman.exception.ResourceNotFoundException;
import ro.upb.acs.fleetman.trip.Trip;
import ro.upb.acs.fleetman.trip.TripRepository;
import ro.upb.acs.fleetman.vehicle.car.Car;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private FleetManagerRepository fleetManagerRepository;

    @InjectMocks
    private ReportService reportService;

    @Nested
    @DisplayName("generateReportForFleetManager Tests")
    class GenerateReportForFleetManagerTests {

        @Test
        @DisplayName("Should generate report successfully")
        void shouldGenerateReportSuccessfully() {
            Driver driver = new Driver();
            driver.setFirstName("John");
            driver.setLastName("Doe");

            Car vehicle = new Car();
            vehicle.setLicensePlate("B123ABC");
            vehicle.setVin("VIN123");

            Trip trip1 = new Trip();
            trip1.setDriver(driver);
            trip1.setVehicle(vehicle);
            trip1.setDistanceKm(100.0);

            Trip trip2 = new Trip();
            trip2.setDriver(driver);
            trip2.setVehicle(vehicle);
            trip2.setDistanceKm(150.0);

            when(fleetManagerRepository.existsById(1L)).thenReturn(true);
            when(tripRepository.findByVehicleFleetManagerId(1L)).thenReturn(List.of(trip1, trip2));

            ReportDto report = reportService.generateReportForFleetManager(1L);

            assertNotNull(report);
            assertEquals(250.0, report.totalDistance());
            assertEquals(2, report.totalTrips());
            assertEquals(2, report.tripsPerVehicle().get("VIN123"));
            assertEquals(250.0, report.distancePerDriver().get("John Doe"));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when fleet manager does not exist")
        void shouldThrowResourceNotFoundException() {
            when(fleetManagerRepository.existsById(1L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> reportService.generateReportForFleetManager(1L));
        }
    }
}
