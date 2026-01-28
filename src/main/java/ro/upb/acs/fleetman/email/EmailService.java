package ro.upb.acs.fleetman.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ro.upb.acs.fleetman.employee.driver.Driver;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManager;
import ro.upb.acs.fleetman.trip.Trip;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String FROM_EMAIL = "noreply@fleetman.com";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTripCreatedNotification(Trip trip, Driver driver, FleetManager fleetManager) {
        logger.info("Starting to send trip creation emails on thread: {}", Thread.currentThread().getName());

        CompletableFuture.runAsync(() -> sendEmailToDriver(trip, driver));
        CompletableFuture.runAsync(() -> sendEmailToFleetManager(trip, fleetManager));
    }

    private void sendEmailToDriver(Trip trip, Driver driver) {
        try {
            logger.info("Sending email to driver: {} for trip ID: {}", driver.getEmail(), trip.getId());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(driver.getEmail());
            message.setSubject("Trip Assignment: " + trip.getStartLocation() + " → " + trip.getEndLocation());
            message.setText(buildDriverEmailBody(trip, driver));

            mailSender.send(message);
            logger.info("Email sent to driver: {}", driver.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send email to driver for trip ID: {}", trip.getId(), e);
            throw new RuntimeException(e);
        }
    }

    private void sendEmailToFleetManager(Trip trip, FleetManager fleetManager) {
        try {
            logger.info("Sending email to fleet manager: {} for trip ID: {}", fleetManager.getEmail(), trip.getId());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(fleetManager.getEmail());
            message.setSubject("New Trip: " + trip.getVehicle().getLicensePlate());
            message.setText(buildFleetManagerEmailBody(trip, fleetManager));

            mailSender.send(message);
            logger.info("Email sent to fleet manager: {}", fleetManager.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send email to fleet manager for trip ID: {}", trip.getId(), e);
            throw new RuntimeException(e);
        }
    }

    private String buildDriverEmailBody(Trip trip, Driver driver) {
        return String.format("""
            Hi %s,

            You have a new trip assignment:

            From: %s
            To: %s
            Vehicle: %s (%s)
            Start: %s
            Status: %s

            Best regards,
            Fleet Management
            """,
            driver.getFirstName(),
            trip.getStartLocation(),
            trip.getEndLocation(),
            trip.getVehicle().getVin(),
            trip.getVehicle().getLicensePlate(),
            trip.getStartTime().format(DATE_FORMATTER),
            trip.getStatus()
        );
    }

    private String buildFleetManagerEmailBody(Trip trip, FleetManager fleetManager) {
        return String.format("""
            Hi %s,

            A new trip has been created:

            Driver: %s
            Vehicle: %s (%s)
            From: %s
            To: %s
            Start: %s
            Status: %s

            Best regards,
            Fleet Management
            """,
            fleetManager.getFirstName(),
            trip.getDriver().getFirstName() + " " + trip.getDriver().getLastName(),
            trip.getVehicle().getVin(),
            trip.getVehicle().getLicensePlate(),
            trip.getStartLocation(),
            trip.getEndLocation(),
            trip.getStartTime().format(DATE_FORMATTER),
            trip.getStatus()
        );
    }
}
