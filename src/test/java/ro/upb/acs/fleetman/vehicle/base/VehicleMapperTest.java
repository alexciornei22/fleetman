package ro.upb.acs.fleetman.vehicle.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManager;
import ro.upb.acs.fleetman.vehicle.car.Car;
import ro.upb.acs.fleetman.vehicle.car.CarBodyType;
import ro.upb.acs.fleetman.vehicle.truck.Truck;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VehicleMapper Unit Tests")
class VehicleMapperTest {

    private VehicleMapper vehicleMapper;

    @BeforeEach
    void setUp() {
        vehicleMapper = new VehicleMapper();
    }

    @Nested
    @DisplayName("toDto Tests")
    class ToDtoTests {

        @ParameterizedTest(name = "{index} - Car ID: {0}")
        @CsvSource({
            "1,PETROL,150,50,2000,true",
            "2,DIESEL,180,60,2500,false",
            "99,ELECTRIC,200,0,0,true",
            "9999,PETROL,120,45,1600,true",
            "123456,DIESEL,160,55,2200,false"
        })
        @DisplayName("Should map Car entity to VehicleDto")
        void shouldMapCarEntityToVehicleDto(Long id, String engineType, int horsepower,
                                           int fuelCapacity, int displacement, boolean automatic) {
            Car car = new Car();
            car.setId(id);
            car.setVin("CAR-VIN-" + id);
            car.setLicensePlate("CAR-LP-" + id);
            car.setMileage(10000 + id.intValue() * 100);
            car.setNumberOfSeats(5);
            car.setNumberOfDoors(4);
            car.setIsChildSeatCompatible(true);
            car.setHasSunroof(false);
            car.setCarBodyType(CarBodyType.SEDAN);

            PowertrainInformation powertrainInfo = new PowertrainInformation();
            powertrainInfo.setEngineType(EngineType.valueOf(engineType));
            powertrainInfo.setHorsepower(horsepower);
            powertrainInfo.setFuelCapacityLiters(fuelCapacity);
            powertrainInfo.setEngineDisplacementCc(displacement);
            powertrainInfo.setHasAutomaticTransmission(automatic);
            car.setPowertrainInformation(powertrainInfo);

            FleetManager fleetManager = new FleetManager();
            fleetManager.setId(1L);
            car.setFleetManager(fleetManager);

            VehicleDto result = vehicleMapper.toDto(car);

            assertNotNull(result);
            assertEquals(id, result.id());
            assertEquals("CAR-VIN-" + id, result.vin());
            assertEquals("CAR-LP-" + id, result.licensePlate());
            assertEquals(10000 + id.intValue() * 100, result.mileage());
            assertEquals("CAR", result.vehicleType());

            PowertrainInformationDto powertrainDto = result.powertrainInformation();
            assertNotNull(powertrainDto);
            assertEquals(EngineType.valueOf(engineType), powertrainDto.engineType());
            assertEquals(horsepower, powertrainDto.horsepower());
            assertEquals(fuelCapacity, powertrainDto.fuelCapacityLiters());
            assertEquals(displacement, powertrainDto.engineDisplacementCc());
            assertEquals(automatic, powertrainDto.hasAutomaticTransmission());
        }

        @ParameterizedTest(name = "{index} - Truck ID: {0}")
        @CsvSource({
            "1,DIESEL,300,150,5000,false",
            "2,DIESEL,350,200,6000,true",
            "50,PETROL,280,120,4500,false",
            "100,DIESEL,400,180,7000,false",
            "999999,DIESEL,320,160,5500,true"
        })
        @DisplayName("Should map Truck entity to VehicleDto")
        void shouldMapTruckEntityToVehicleDto(Long id, String engineType, int horsepower,
                                             int fuelCapacity, int displacement, boolean automatic) {
            Truck truck = new Truck();
            truck.setId(id);
            truck.setVin("TRUCK-VIN-" + id);
            truck.setLicensePlate("TRUCK-LP-" + id);
            truck.setMileage(20000 + id.intValue() * 100);
            truck.setMaxLoadKg(15000);
            truck.setNumberOfAxles(4);
            truck.setHasRefrigerationUnit(true);

            PowertrainInformation powertrainInfo = new PowertrainInformation();
            powertrainInfo.setEngineType(EngineType.valueOf(engineType));
            powertrainInfo.setHorsepower(horsepower);
            powertrainInfo.setFuelCapacityLiters(fuelCapacity);
            powertrainInfo.setEngineDisplacementCc(displacement);
            powertrainInfo.setHasAutomaticTransmission(automatic);
            truck.setPowertrainInformation(powertrainInfo);

            FleetManager fleetManager = new FleetManager();
            fleetManager.setId(2L);
            truck.setFleetManager(fleetManager);

            VehicleDto result = vehicleMapper.toDto(truck);

            assertNotNull(result);
            assertEquals(id, result.id());
            assertEquals("TRUCK-VIN-" + id, result.vin());
            assertEquals("TRUCK-LP-" + id, result.licensePlate());
            assertEquals(20000 + id.intValue() * 100, result.mileage());
            assertEquals("TRUCK", result.vehicleType());

            PowertrainInformationDto powertrainDto = result.powertrainInformation();
            assertNotNull(powertrainDto);
            assertEquals(EngineType.valueOf(engineType), powertrainDto.engineType());
            assertEquals(horsepower, powertrainDto.horsepower());
            assertEquals(fuelCapacity, powertrainDto.fuelCapacityLiters());
            assertEquals(displacement, powertrainDto.engineDisplacementCc());
            assertEquals(automatic, powertrainDto.hasAutomaticTransmission());
        }
    }
}
