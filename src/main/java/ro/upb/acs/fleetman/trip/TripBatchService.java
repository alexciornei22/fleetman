package ro.upb.acs.fleetman.trip;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ro.upb.acs.fleetman.exception.BatchQueueFullException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TripBatchService {

    private static final Logger logger = LoggerFactory.getLogger(TripBatchService.class);

    private static final int QUEUE_CAPACITY = 1024;
    private static final long OFFER_TIMEOUT_MILLIS = 500;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 30;

    private final TripService tripService;

    private final BlockingQueue<CreateTripDto> tripQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    private final AtomicInteger succeededTripsCount = new AtomicInteger(0);

    private final AtomicInteger failedTripsCount = new AtomicInteger(0);

    private final ExecutorService consumerExecutor = Executors.newSingleThreadExecutor(consumerThreadFactory());

    public TripBatchService(TripService tripService) {
        this.tripService = tripService;
    }

    @PostConstruct
    public void startConsumer() {
        consumerExecutor.execute(this::consumeLoop);
    }

    @PreDestroy
    public void stopConsumer() {
        consumerExecutor.shutdownNow();
        try {
            if (!consumerExecutor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                logger.warn("Trip batch consumer did not terminate within {} seconds.", SHUTDOWN_TIMEOUT_SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void queueTripsForImport(List<CreateTripDto> trips) {
        logger.info("Queueing {} trips for batch import...", trips.size());
        for (CreateTripDto trip : trips) {
            try {
                if (!tripQueue.offer(trip, OFFER_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                    throw new BatchQueueFullException(QUEUE_CAPACITY);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while queueing trips for batch import", e);
            }
        }
    }

    public int getSucceededTripsCount() {
        return succeededTripsCount.get();
    }

    public int getFailedTripsCount() {
        return failedTripsCount.get();
    }

    private void consumeLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                processTrip(tripQueue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        drainRemaining();
        logger.info("Trip batch consumer stopped.");
    }

    private void drainRemaining() {
        Thread.interrupted();
        List<CreateTripDto> remaining = new ArrayList<>();
        tripQueue.drainTo(remaining);
        if (!remaining.isEmpty()) {
            logger.info("Draining {} remaining trips before shutdown.", remaining.size());
            remaining.forEach(this::processTrip);
        }
    }

    private void processTrip(CreateTripDto tripDto) {
        try {
            tripService.createTrip(tripDto);
            int count = succeededTripsCount.incrementAndGet();
            logger.info("Successfully imported trip from batch. Total succeeded: {}", count);
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            int failures = failedTripsCount.incrementAndGet();
            logger.error("Failed to process batched trip. Total failed: {}", failures, e);
        }
    }

    private static ThreadFactory consumerThreadFactory() {
        return runnable -> {
            Thread thread = new Thread(runnable, "trip-batch-consumer");
            thread.setDaemon(true);
            return thread;
        };
    }
}
