package ro.upb.acs.fleetman.vehicle.base;

import org.springframework.stereotype.Component;
import ro.upb.acs.fleetman.vehicle.car.Car;
import ro.upb.acs.fleetman.vehicle.truck.Truck;

@Component
public class VehicleMapper {

    public VehicleDto toDto(Vehicle vehicle) {
        var powertrainInfo = vehicle.getPowertrainInformation();
        var powertrainInfoDto = new PowertrainInformationDto(
            powertrainInfo.getEngineType(),
            powertrainInfo.getHorsepower(),
            powertrainInfo.getFuelCapacityLiters(),
            powertrainInfo.getEngineDisplacementCc(),
            powertrainInfo.getHasAutomaticTransmission()
        );

        String vehicleType = determineVehicleType(vehicle);

        return new VehicleDto(
            vehicle.getId(),
            vehicle.getVin(),
            vehicle.getLicensePlate(),
            vehicle.getMileage(),
            powertrainInfoDto,
            vehicleType
        );
    }

    private String determineVehicleType(Vehicle vehicle) {
        return vehicle.getClass().getSimpleName().toUpperCase();
    }
}
