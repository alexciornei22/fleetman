package ro.upb.acs.fleetman.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ro.upb.acs.fleetman.employee.driver.Driver;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManager;
import ro.upb.acs.fleetman.trip.Trip;
import ro.upb.acs.fleetman.trip.TripStatus;
import ro.upb.acs.fleetman.vehicle.base.Vehicle;
import ro.upb.acs.fleetman.vehicle.car.Car;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("EmailService Unit Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;
    private Vehicle vehicle;
    private Driver driver;
    private FleetManager fleetManager;
    private Trip trip;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        Executor immediateExecutor = Runnable::run;
        emailService = new EmailService(mailSender, immediateExecutor);

        vehicle = new Car();
        vehicle.setId(1L);
        vehicle.setVin("VIN-123");
        vehicle.setLicensePlate("LP-123");

        driver = new Driver();
        driver.setId(2L);
        driver.setEmail("john.doe@example.com");

        fleetManager = new FleetManager();
        fleetManager.setId(3L);
        fleetManager.setEmail("fleet.manager@example.com");

        trip = new Trip();
        trip.setId(5L);
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setStartLocation("City A");
        trip.setEndLocation("City B");
        trip.setStartTime(LocalDateTime.of(2026, 1, 1, 10, 0));
    }

    @ParameterizedTest(name = "status={2}, driver={0} {1}")
    @CsvSource({
        "John,Doe,SCHEDULED",
        "Jane,Smith,IN_PROGRESS",
        "Alex,Brown,COMPLETED"
    })
    @DisplayName("Should send driver and fleet manager emails for each status and names")
    void shouldSendDriverAndFleetManagerEmails(String driverFirstName, String driverLastName, String status) {
        driver.setFirstName(driverFirstName);
        driver.setLastName(driverLastName);
        driver.setEmail(driverFirstName.toLowerCase() + "." + driverLastName.toLowerCase() + "@example.com");
        fleetManager.setFirstName("Admin");
        fleetManager.setLastName("Control");
        fleetManager.setEmail("fleet.manager@example.com");
        trip.setStatus(TripStatus.valueOf(status));

        emailService.sendTripCreatedNotification(trip, driver, fleetManager);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(2)).send(captor.capture());

        SimpleMailMessage driverMessage = captor.getAllValues().stream()
            .filter(message -> Arrays.asList(message.getTo()).contains(driver.getEmail()))
            .findFirst()
            .orElseThrow();
        assertEquals("Trip Assignment: City A → City B", driverMessage.getSubject());
        assertTrue(driverMessage.getText().contains("You have a new trip assignment"));
        assertTrue(driverMessage.getText().contains("Vehicle: VIN-123 (LP-123)"));
        assertTrue(driverMessage.getText().contains("Status: " + status));

        SimpleMailMessage managerMessage = captor.getAllValues().stream()
            .filter(message -> Arrays.asList(message.getTo()).contains(fleetManager.getEmail()))
            .findFirst()
            .orElseThrow();
        assertEquals("New Trip: LP-123", managerMessage.getSubject());
        assertTrue(managerMessage.getText().contains("A new trip has been created"));
        assertTrue(managerMessage.getText().contains("Driver: " + driverFirstName + " " + driverLastName));
        assertTrue(managerMessage.getText().contains("Vehicle: VIN-123 (LP-123)"));
    }
}
