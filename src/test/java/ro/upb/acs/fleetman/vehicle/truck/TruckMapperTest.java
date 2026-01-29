package ro.upb.acs.fleetman.vehicle.truck;

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

@DisplayName("TruckMapper Unit Tests")
class TruckMapperTest {

    private TruckMapper truckMapper;

    @BeforeEach
    void setUp() {
        truckMapper = new TruckMapper();
    }

    @Nested
    @DisplayName("toEntity Tests")
    class ToEntityTests {

        @ParameterizedTest(name = "{index} - {0}")
        @CsvSource({
            "DIESEL,10000,3,true,25000,300,150,5000,false,1",
            "PETROL,15000,4,false,30000,250,120,4000,true,2",
            "ELECTRIC,8000,2,true,5000,400,0,0,true,5",
            "DIESEL,20000,5,false,50000,350,200,6000,false,100",
            "PETROL,12000,3,true,18000,280,100,3500,true,9999999"
        })
        @DisplayName("Should map CreateTruckDto to Truck entity")
        void shouldMapCreateTruckDtoToTruckEntity(String engineType, int maxLoad, int axles,
                                                 boolean refrigeration, int mileage,
                                                 int horsepower, int fuel, int cc, boolean auto,
                                                 Long fleetManagerId) {
            PowertrainInformationDto powertrainInfoDto = new PowertrainInformationDto(
                EngineType.valueOf(engineType),
                horsepower,
                fuel,
                cc,
                auto
            );

            CreateTruckDto createTruckDto = new CreateTruckDto(
                "VIN-" + engineType + "-" + maxLoad,
                "LP-TRUCK-" + axles,
                mileage,
                powertrainInfoDto,
                maxLoad,
                axles,
                refrigeration,
                fleetManagerId
            );

            Truck result = truckMapper.toEntity(createTruckDto);

            assertNotNull(result);
            assertEquals(createTruckDto.vin(), result.getVin());
            assertEquals(createTruckDto.licensePlate(), result.getLicensePlate());
            assertEquals(mileage, result.getMileage());
            assertEquals(maxLoad, result.getMaxLoadKg());
            assertEquals(axles, result.getNumberOfAxles());
            assertEquals(refrigeration, result.getHasRefrigerationUnit());

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
            "DIESEL,10000,3,true,1,300,150,5000,false,1",
            "PETROL,15000,4,false,99,250,120,4000,true,5",
            "ELECTRIC,8000,2,true,50,400,0,0,true,100",
            "DIESEL,20000,5,false,9999,350,200,6000,false,2",
            "PETROL,12000,3,true,9999999,280,100,3500,true,9999999"
        })
        @DisplayName("Should map Truck entity to TruckDto")
        void shouldMapTruckEntityToTruckDto(String engineType, int maxLoad, int axles,
                                           boolean refrigeration, Long id,
                                           int horsepower, int fuel, int cc, boolean auto,
                                           Long fleetManagerId) {
            Truck truck = new Truck();
            truck.setId(id);
            truck.setVin("VIN-" + engineType);
            truck.setLicensePlate("LP-TRUCK-" + axles);
            truck.setMileage(25000);
            truck.setMaxLoadKg(maxLoad);
            truck.setNumberOfAxles(axles);
            truck.setHasRefrigerationUnit(refrigeration);

            PowertrainInformation powertrainInfo = new PowertrainInformation();
            powertrainInfo.setEngineType(EngineType.valueOf(engineType));
            powertrainInfo.setHorsepower(horsepower);
            powertrainInfo.setFuelCapacityLiters(fuel);
            powertrainInfo.setEngineDisplacementCc(cc);
            powertrainInfo.setHasAutomaticTransmission(auto);
            truck.setPowertrainInformation(powertrainInfo);

            FleetManager fleetManager = new FleetManager();
            fleetManager.setId(fleetManagerId);
            truck.setFleetManager(fleetManager);

            TruckDto result = truckMapper.toDto(truck);

            assertNotNull(result);
            assertEquals(id, result.id());
            assertEquals(truck.getVin(), result.vin());
            assertEquals(truck.getLicensePlate(), result.licensePlate());
            assertEquals(25000, result.mileage());
            assertEquals(maxLoad, result.maxLoadKg());
            assertEquals(axles, result.numberOfAxles());
            assertEquals(refrigeration, result.hasRefrigerationUnit());

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

        @ParameterizedTest(name = "{index} - Engine: {0}, MaxLoad: {1}")
        @CsvSource({
            "DIESEL,10000,3,true",
            "PETROL,15000,4,false",
            "ELECTRIC,8000,2,true",
            "DIESEL,20000,5,false",
            "PETROL,12000,3,true"
        })
        @DisplayName("Should correctly map CreateTruckDto to Truck and back to TruckDto bidirectionally")
        void shouldMapBidirectionally(String engineType, int maxLoad, int axles, boolean refrigeration) {
            PowertrainInformationDto powertrainInfoDto = new PowertrainInformationDto(
                EngineType.valueOf(engineType),
                300,
                150,
                5000,
                false
            );

            CreateTruckDto createTruckDto = new CreateTruckDto(
                "VIN-" + engineType + "-" + maxLoad,
                "LP-TRUCK-" + axles,
                25000,
                powertrainInfoDto,
                maxLoad,
                axles,
                refrigeration,
                1L
            );

            Truck mappedTruck = truckMapper.toEntity(createTruckDto);
            TruckDto result = truckMapper.toDto(mappedTruck);

            assertNull(result.id());
            assertEquals(createTruckDto.vin(), result.vin());
            assertEquals(createTruckDto.licensePlate(), result.licensePlate());
            assertEquals(createTruckDto.mileage(), result.mileage());
            assertEquals(createTruckDto.maxLoadKg(), result.maxLoadKg());
            assertEquals(createTruckDto.numberOfAxles(), result.numberOfAxles());
            assertEquals(createTruckDto.hasRefrigerationUnit(), result.hasRefrigerationUnit());
            assertEquals(createTruckDto.fleetManagerId(), result.fleetManagerId());

            PowertrainInformationDto originalPowertrainDto = createTruckDto.powertrainInformation();
            PowertrainInformationDto resultPowertrainDto = result.powertrainInformation();
            assertEquals(originalPowertrainDto.engineType(), resultPowertrainDto.engineType());
            assertEquals(originalPowertrainDto.horsepower(), resultPowertrainDto.horsepower());
            assertEquals(originalPowertrainDto.fuelCapacityLiters(), resultPowertrainDto.fuelCapacityLiters());
            assertEquals(originalPowertrainDto.engineDisplacementCc(), resultPowertrainDto.engineDisplacementCc());
            assertEquals(originalPowertrainDto.hasAutomaticTransmission(), resultPowertrainDto.hasAutomaticTransmission());
        }
    }
}
