package ro.upb.acs.fleetman.vehicle.truck;

import org.springframework.stereotype.Component;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformation;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;

@Component
public class TruckMapper {

    public Truck toEntity(CreateTruckDto request) {
        Truck truck = new Truck();
        truck.setVin(request.vin());
        truck.setLicensePlate(request.licensePlate());
        truck.setMileage(request.mileage());

        var powertrainInfoDto = request.powertrainInformation();
        var powertrainInfo = new PowertrainInformation();
        powertrainInfo.setEngineType(powertrainInfoDto.engineType());
        powertrainInfo.setHorsepower(powertrainInfoDto.horsepower());
        powertrainInfo.setFuelCapacityLiters(powertrainInfoDto.fuelCapacityLiters());
        powertrainInfo.setEngineDisplacementCc(powertrainInfoDto.engineDisplacementCc());
        powertrainInfo.setHasAutomaticTransmission(powertrainInfoDto.hasAutomaticTransmission());

        truck.setPowertrainInformation(powertrainInfo);

        truck.setMaxLoadKg(request.maxLoadKg());
        truck.setNumberOfAxles(request.numberOfAxles());
        truck.setHasRefrigerationUnit(request.hasRefrigerationUnit());

        return truck;
    }

    public TruckDto toDto(Truck truck) {
        var powertrainInfo = truck.getPowertrainInformation();
        var powertrainInfoDto = new PowertrainInformationDto(
            powertrainInfo.getEngineType(),
            powertrainInfo.getHorsepower(),
            powertrainInfo.getFuelCapacityLiters(),
            powertrainInfo.getEngineDisplacementCc(),
            powertrainInfo.getHasAutomaticTransmission()
        );

        return new TruckDto(
            truck.getId(),
            truck.getVin(),
            truck.getLicensePlate(),
            truck.getMileage(),
            powertrainInfoDto,
            truck.getMaxLoadKg(),
            truck.getNumberOfAxles(),
            truck.getHasRefrigerationUnit()
        );
    }
}
