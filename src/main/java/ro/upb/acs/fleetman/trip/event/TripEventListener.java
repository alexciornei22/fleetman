package ro.upb.acs.fleetman.trip.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ro.upb.acs.fleetman.email.EmailService;

@Component
public class TripEventListener {

    private static final Logger logger = LoggerFactory.getLogger(TripEventListener.class);

    private final EmailService emailService;

    public TripEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void handleTripCreatedEvent(TripCreatedEvent event) {
        logger.info("Handling TripCreatedEvent for trip ID: {} on thread: {}",
            event.trip().getId(),
            Thread.currentThread().getName());
        emailService.sendTripCreatedNotification(event.trip(), event.driver(), event.fleetManager());
    }
}
