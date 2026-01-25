package ro.upb.acs.fleetman.vehicle.car;

import org.springframework.stereotype.Component;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformation;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;

@Component
public class CarMapper {

    public Car toEntity(CreateCarDto request) {
        Car car = new Car();
        car.setVin(request.vin());
        car.setLicensePlate(request.licensePlate());
        car.setMileage(request.mileage());

        var powertrainInfoDto = request.powertrainInformation();
        var powertrainInfo = new PowertrainInformation();
        powertrainInfo.setEngineType(powertrainInfoDto.engineType());
        powertrainInfo.setHorsepower(powertrainInfoDto.horsepower());
        powertrainInfo.setFuelCapacityLiters(powertrainInfoDto.fuelCapacityLiters());
        powertrainInfo.setEngineDisplacementCc(powertrainInfoDto.engineDisplacementCc());
        powertrainInfo.setHasAutomaticTransmission(powertrainInfoDto.hasAutomaticTransmission());

        car.setPowertrainInformation(powertrainInfo);

        car.setNumberOfSeats(request.numberOfSeats());
        car.setNumberOfDoors(request.numberOfDoors());
        car.setIsChildSeatCompatible(request.isChildSeatCompatible());
        car.setHasSunroof(request.hasSunroof());
        car.setCarBodyType(request.carBodyType());

        return car;
    }

    public CarDto toDto(Car car) {
        var powertrainInfo = car.getPowertrainInformation();
        var powertrainInfoDto = new PowertrainInformationDto(
            powertrainInfo.getEngineType(),
            powertrainInfo.getHorsepower(),
            powertrainInfo.getFuelCapacityLiters(),
            powertrainInfo.getEngineDisplacementCc(),
            powertrainInfo.getHasAutomaticTransmission()
        );

        return new CarDto(
            car.getId(),
            car.getVin(),
            car.getLicensePlate(),
            car.getMileage(),
            powertrainInfoDto,
            car.getNumberOfSeats(),
            car.getNumberOfDoors(),
            car.isChildSeatCompatible(),
            car.getHasSunroof(),
            car.getCarBodyType()
        );
    }
}
