package ro.upb.acs.fleetman.trip;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.upb.acs.fleetman.exception.BatchQueueFullException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripBatchService Unit Tests")
class TripBatchServiceTest {

    private static final int QUEUE_CAPACITY = 1024;
    private static final long AWAIT_TIMEOUT_MILLIS = 2000;

    @Mock
    private TripService tripService;

    private TripBatchService tripBatchService;

    @BeforeEach
    void setUp() {
        tripBatchService = new TripBatchService(tripService);
        tripBatchService.startConsumer();
    }

    @AfterEach
    void tearDown() {
        tripBatchService.stopConsumer();
    }

    @Test
    @DisplayName("Should successfully queue and process multiple trips")
    void shouldSuccessfullyQueueAndProcessMultipleTrips() throws InterruptedException {
        when(tripService.createTrip(any(CreateTripDto.class))).thenReturn(null);

        tripBatchService.queueTripsForImport(List.of(tripDto(1L), tripDto(2L)));

        awaitProcessed(2);

        assertEquals(2, tripBatchService.getSucceededTripsCount());
        assertEquals(0, tripBatchService.getFailedTripsCount());
        verify(tripService, times(2)).createTrip(any(CreateTripDto.class));
    }

    @Test
    @DisplayName("Should isolate failures and keep processing remaining trips")
    void shouldIsolateFailuresAndKeepProcessing() throws InterruptedException {
        CreateTripDto failing = tripDto(1L);
        CreateTripDto succeeding = tripDto(2L);

        doThrow(new RuntimeException("Simulated failure")).when(tripService).createTrip(failing);
        doReturn(null).when(tripService).createTrip(succeeding);

        tripBatchService.queueTripsForImport(List.of(failing, succeeding));

        awaitProcessed(2);

        assertEquals(1, tripBatchService.getSucceededTripsCount());
        assertEquals(1, tripBatchService.getFailedTripsCount());
        verify(tripService, times(2)).createTrip(any(CreateTripDto.class));
    }

    @Test
    @DisplayName("Should reject the batch when the queue is full")
    void shouldRejectWhenQueueIsFull() {
        CountDownLatch release = new CountDownLatch(1);
        doAnswer(invocation -> {
            release.await();
            return null;
        }).when(tripService).createTrip(any(CreateTripDto.class));

        List<CreateTripDto> overflowingBatch = IntStream.rangeClosed(1, QUEUE_CAPACITY + 2)
            .mapToObj(i -> tripDto((long) i))
            .toList();

        try {
            assertThrows(BatchQueueFullException.class,
                () -> tripBatchService.queueTripsForImport(overflowingBatch));
        } finally {
            release.countDown();
        }
    }

    @Test
    @DisplayName("Should drain queued trips on shutdown")
    void shouldDrainQueuedTripsOnShutdown() throws InterruptedException {
        CountDownLatch firstCallReached = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AtomicInteger calls = new AtomicInteger(0);
        doAnswer(invocation -> {
            if (calls.getAndIncrement() == 0) {
                firstCallReached.countDown();
                release.await();
            }
            return null;
        }).when(tripService).createTrip(any(CreateTripDto.class));

        tripBatchService.queueTripsForImport(List.of(tripDto(1L), tripDto(2L), tripDto(3L)));

        assertTrue(firstCallReached.await(AWAIT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));

        tripBatchService.stopConsumer();

        assertEquals(2, tripBatchService.getSucceededTripsCount());
        assertEquals(1, tripBatchService.getFailedTripsCount());
    }

    private void awaitProcessed(int expectedTotal) throws InterruptedException {
        long deadline = System.currentTimeMillis() + AWAIT_TIMEOUT_MILLIS;
        while (tripBatchService.getSucceededTripsCount() + tripBatchService.getFailedTripsCount() < expectedTotal
            && System.currentTimeMillis() < deadline) {
            Thread.sleep(5);
        }
    }

    private CreateTripDto tripDto(long vehicleId) {
        return new CreateTripDto(
            vehicleId,
            1L,
            "City A",
            "City B",
            LocalDateTime.of(2026, 1, 1, 8, 0),
            LocalDateTime.of(2026, 1, 1, 10, 0),
            150.0,
            TripStatus.SCHEDULED,
            "Notes"
        );
    }
}
