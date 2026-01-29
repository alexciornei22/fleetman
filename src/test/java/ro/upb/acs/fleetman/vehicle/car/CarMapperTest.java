package ro.upb.acs.fleetman.vehicle.car;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManager;
import ro.upb.acs.fleetman.vehicle.base.EngineType;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformation;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CarMapper Unit Tests")
class CarMapperTest {

    private CarMapper carMapper;

    @BeforeEach
    void setUp() {
        carMapper = new CarMapper();
    }

    @Nested
    @DisplayName("toEntity Tests")
    class ToEntityTests {

        @ParameterizedTest(name = "{index} - {0}")
        @CsvSource({
            "PETROL,5,4,true,true,SEDAN,10000,150,50,1600,true,1",
            "DIESEL,7,5,false,false,SUV,25000,120,60,2000,false,2",
            "ELECTRIC,5,4,true,true,SEDAN,1000,300,0,0,true,5",
            "PETROL,7,4,false,true,HATCHBACK,50000,200,75,2500,true,100",
            "DIESEL,5,2,true,false,COUPE,15000,100,45,1500,false,9999999",
            "ELECTRIC,7,5,false,false,WAGON,0,250,0,0,false,99"
        })
        @DisplayName("Should map CreateCarDto to Car entity")
        void shouldMapCreateCarDtoToCarEntity(String engineType, int seats, int doors,
                                             boolean childSeat, boolean sunroof,
                                             String bodyType, int mileage,
                                             int horsepower, int fuel, int cc, boolean auto,
                                             Long fleetManagerId) {
            PowertrainInformationDto powertrainInfoDto = new PowertrainInformationDto(
                EngineType.valueOf(engineType),
                horsepower,
                fuel,
                cc,
                auto
            );

            CreateCarDto createCarDto = new CreateCarDto(
                "VIN-" + engineType + "-" + bodyType,
                "LP-" + bodyType,
                mileage,
                powertrainInfoDto,
                seats,
                doors,
                childSeat,
                sunroof,
                CarBodyType.valueOf(bodyType),
                fleetManagerId
            );

            Car result = carMapper.toEntity(createCarDto);

            assertNotNull(result);
            assertEquals(createCarDto.vin(), result.getVin());
            assertEquals(createCarDto.licensePlate(), result.getLicensePlate());
            assertEquals(mileage, result.getMileage());
            assertEquals(seats, result.getNumberOfSeats());
            assertEquals(doors, result.getNumberOfDoors());
            assertEquals(childSeat, result.isChildSeatCompatible());
            assertEquals(sunroof, result.getHasSunroof());
            assertEquals(CarBodyType.valueOf(bodyType), result.getCarBodyType());

            PowertrainInformation powertrainInfo = result.getPowertrainInformation();
            assertNotNull(powertrainInfo);
            assertEquals(EngineType.valueOf(engineType), powertrainInfo.getEngineType());
            assertEquals(horsepower, powertrainInfo.getHorsepower());
            assertEquals(fuel, powertrainInfo.getFuelCapacityLiters());
            assertEquals(cc, powertrainInfo.getEngineDisplacementCc());
            assertEquals(auto, powertrainInfo.getHasAutomaticTransmission());

            assertNotNull(result.getFleetManager());
            assertEquals(fleetManagerId, result.getFleetManager().getId());
            assertNull(result.getId());
        }
    }

    @Nested
    @DisplayName("toDto Tests")
    class ToDtoTests {

        @ParameterizedTest(name = "{index} - {0}")
        @CsvSource({
            "PETROL,5,4,true,true,SEDAN,1,150,50,1600,true,1",
            "DIESEL,7,5,false,false,SUV,99,120,60,2000,false,5",
            "ELECTRIC,5,4,true,false,HATCHBACK,50,300,0,0,true,100",
            "PETROL,7,4,false,true,COUPE,9999,200,75,2500,true,2",
            "DIESEL,5,2,true,false,WAGON,9999999,100,45,1500,false,9999999"
        })
        @DisplayName("Should map Car entity to CarDto")
        void shouldMapCarEntityToCarDto(String engineType, int seats, int doors,
                                        boolean childSeat, boolean sunroof,
                                        String bodyType, Long id,
                                        int horsepower, int fuel, int cc, boolean auto,
                                        Long fleetManagerId) {
            Car car = new Car();
            car.setId(id);
            car.setVin("VIN-" + engineType);
            car.setLicensePlate("LP-" + bodyType);
            car.setMileage(10000);
            car.setNumberOfSeats(seats);
            car.setNumberOfDoors(doors);
            car.setIsChildSeatCompatible(childSeat);
            car.setHasSunroof(sunroof);
            car.setCarBodyType(CarBodyType.valueOf(bodyType));

            PowertrainInformation powertrainInfo = new PowertrainInformation();
            powertrainInfo.setEngineType(EngineType.valueOf(engineType));
            powertrainInfo.setHorsepower(horsepower);
            powertrainInfo.setFuelCapacityLiters(fuel);
            powertrainInfo.setEngineDisplacementCc(cc);
            powertrainInfo.setHasAutomaticTransmission(auto);
            car.setPowertrainInformation(powertrainInfo);

            FleetManager fleetManager = new FleetManager();
            fleetManager.setId(fleetManagerId);
            car.setFleetManager(fleetManager);

            CarDto result = carMapper.toDto(car);

            assertNotNull(result);
            assertEquals(id, result.id());
            assertEquals(car.getVin(), result.vin());
            assertEquals(car.getLicensePlate(), result.licensePlate());
            assertEquals(10000, result.mileage());
            assertEquals(seats, result.numberOfSeats());
            assertEquals(doors, result.numberOfDoors());
            assertEquals(childSeat, result.isChildSeatCompatible());
            assertEquals(sunroof, result.hasSunroof());
            assertEquals(CarBodyType.valueOf(bodyType), result.carBodyType());

            PowertrainInformationDto resultPowertrainDto = result.powertrainInformation();
            assertEquals(EngineType.valueOf(engineType), resultPowertrainDto.engineType());
            assertEquals(horsepower, resultPowertrainDto.horsepower());
            assertEquals(fuel, resultPowertrainDto.fuelCapacityLiters());
            assertEquals(cc, resultPowertrainDto.engineDisplacementCc());
            assertEquals(auto, resultPowertrainDto.hasAutomaticTransmission());

            assertEquals(fleetManagerId, result.fleetManagerId());
        }
    }

    @Nested
    @DisplayName("Bidirectional Mapping Tests")
    class BidirectionalMappingTests {

        @ParameterizedTest(name = "{index} - Engine: {0}, BodyType: {1}")
        @CsvSource({
            "PETROL,SEDAN",
            "DIESEL,SUV",
            "ELECTRIC,HATCHBACK",
            "PETROL,COUPE",
            "DIESEL,WAGON"
        })
        @DisplayName("Should correctly map CreateCarDto to Car and back to CarDto bidirectionally")
        void shouldMapBidirectionally(String engineType, String bodyType) {
            PowertrainInformationDto powertrainInfoDto = new PowertrainInformationDto(
                EngineType.valueOf(engineType),
                150,
                50,
                1600,
                true
            );

            CreateCarDto createCarDto = new CreateCarDto(
                "VIN-" + engineType + "-" + bodyType,
                "LP-" + bodyType,
                10000,
                powertrainInfoDto,
                5,
                4,
                true,
                true,
                CarBodyType.valueOf(bodyType),
                1L
            );

            Car mappedCar = carMapper.toEntity(createCarDto);
            CarDto result = carMapper.toDto(mappedCar);

            assertNull(result.id());
            assertEquals(createCarDto.vin(), result.vin());
            assertEquals(createCarDto.licensePlate(), result.licensePlate());
            assertEquals(createCarDto.mileage(), result.mileage());
            assertEquals(createCarDto.numberOfSeats(), result.numberOfSeats());
            assertEquals(createCarDto.numberOfDoors(), result.numberOfDoors());
            assertEquals(createCarDto.isChildSeatCompatible(), result.isChildSeatCompatible());
            assertEquals(createCarDto.hasSunroof(), result.hasSunroof());
            assertEquals(CarBodyType.valueOf(bodyType), result.carBodyType());
            assertEquals(createCarDto.fleetManagerId(), result.fleetManagerId());

            PowertrainInformationDto originalPowertrainDto = createCarDto.powertrainInformation();
            PowertrainInformationDto resultPowertrainDto = result.powertrainInformation();
            assertEquals(originalPowertrainDto.engineType(), resultPowertrainDto.engineType());
            assertEquals(originalPowertrainDto.horsepower(), resultPowertrainDto.horsepower());
            assertEquals(originalPowertrainDto.fuelCapacityLiters(), resultPowertrainDto.fuelCapacityLiters());
            assertEquals(originalPowertrainDto.engineDisplacementCc(), resultPowertrainDto.engineDisplacementCc());
            assertEquals(originalPowertrainDto.hasAutomaticTransmission(), resultPowertrainDto.hasAutomaticTransmission());
        }
    }
}
