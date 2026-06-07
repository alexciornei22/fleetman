package ro.upb.acs.fleetman.report;

import org.springframework.stereotype.Service;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManagerRepository;
import ro.upb.acs.fleetman.exception.ResourceNotFoundException;
import ro.upb.acs.fleetman.trip.Trip;
import ro.upb.acs.fleetman.trip.TripRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final TripRepository tripRepository;
    private final FleetManagerRepository fleetManagerRepository;
    private final ExecutorService executorService;

    public ReportService(TripRepository tripRepository, FleetManagerRepository fleetManagerRepository) {
        this.tripRepository = tripRepository;
        this.fleetManagerRepository = fleetManagerRepository;
        this.executorService = Executors.newFixedThreadPool(4);
    }

    @jakarta.annotation.PreDestroy
    public void stopExecutor() {
        executorService.shutdownNow();
    }

    public ReportDto generateReportForFleetManager(Long fleetManagerId) {
        if (!fleetManagerRepository.existsById(fleetManagerId)) {
            throw new ResourceNotFoundException("FleetManager", fleetManagerId.toString());
        }

        List<Trip> trips = tripRepository.findByVehicleFleetManagerId(fleetManagerId);

        try {
            Callable<Double> totalDistanceTask = () -> trips.stream()
                    .filter(trip -> trip.getDistanceKm() != null)
                    .mapToDouble(Trip::getDistanceKm)
                    .sum();

            Callable<Integer> totalTripsTask = trips::size;

            Callable<Map<String, Long>> tripsPerVehicleTask = () -> trips.stream()
                    .collect(Collectors.groupingBy(
                            trip -> trip.getVehicle().getVin(),
                            Collectors.counting()
                    ));

            Callable<Map<String, Double>> distancePerDriverTask = () -> trips.stream()
                    .filter(trip -> trip.getDistanceKm() != null)
                    .collect(Collectors.groupingBy(
                            trip -> trip.getDriver().getFirstName() + " " + trip.getDriver().getLastName(),
                            Collectors.summingDouble(Trip::getDistanceKm)
                    ));

            Future<Double> totalDistanceFuture = executorService.submit(totalDistanceTask);
            Future<Integer> totalTripsFuture = executorService.submit(totalTripsTask);
            Future<Map<String, Long>> tripsPerVehicleFuture = executorService.submit(tripsPerVehicleTask);
            Future<Map<String, Double>> distancePerDriverFuture = executorService.submit(distancePerDriverTask);

            return new ReportDto(
                    totalDistanceFuture.get(),
                    totalTripsFuture.get(),
                    tripsPerVehicleFuture.get(),
                    distancePerDriverFuture.get()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate report concurrently", e);
        }
    }
}
