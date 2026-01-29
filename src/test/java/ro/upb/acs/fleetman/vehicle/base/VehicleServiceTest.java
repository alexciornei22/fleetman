package ro.upb.acs.fleetman.vehicle.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.common.PaginationMapper;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManager;
import ro.upb.acs.fleetman.vehicle.car.Car;
import ro.upb.acs.fleetman.vehicle.car.CarBodyType;
import ro.upb.acs.fleetman.vehicle.truck.Truck;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Unit Tests")
class VehicleServiceTest {

    @Mock
    private VehicleMapper vehicleMapper;

    @Mock
    private PaginationMapper paginationMapper;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Car car;
    private Truck truck;
    private VehicleDto carDto;
    private VehicleDto truckDto;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setVin("CAR-VIN-123");
        car.setLicensePlate("CAR-LP-123");
        car.setMileage(15000);
        car.setNumberOfSeats(5);
        car.setNumberOfDoors(4);
        car.setIsChildSeatCompatible(true);
        car.setHasSunroof(false);
        car.setCarBodyType(CarBodyType.SEDAN);

        PowertrainInformation carPowertrainInfo = new PowertrainInformation();
        carPowertrainInfo.setEngineType(EngineType.PETROL);
        carPowertrainInfo.setHorsepower(150);
        carPowertrainInfo.setFuelCapacityLiters(50);
        carPowertrainInfo.setEngineDisplacementCc(2000);
        carPowertrainInfo.setHasAutomaticTransmission(true);
        car.setPowertrainInformation(carPowertrainInfo);

        FleetManager carFleetManager = new FleetManager();
        carFleetManager.setId(1L);
        car.setFleetManager(carFleetManager);

        truck = new Truck();
        truck.setId(2L);
        truck.setVin("TRUCK-VIN-456");
        truck.setLicensePlate("TRUCK-LP-456");
        truck.setMileage(30000);
        truck.setMaxLoadKg(12000);
        truck.setNumberOfAxles(3);
        truck.setHasRefrigerationUnit(true);

        PowertrainInformation truckPowertrainInfo = new PowertrainInformation();
        truckPowertrainInfo.setEngineType(EngineType.DIESEL);
        truckPowertrainInfo.setHorsepower(300);
        truckPowertrainInfo.setFuelCapacityLiters(150);
        truckPowertrainInfo.setEngineDisplacementCc(5000);
        truckPowertrainInfo.setHasAutomaticTransmission(false);
        truck.setPowertrainInformation(truckPowertrainInfo);

        FleetManager truckFleetManager = new FleetManager();
        truckFleetManager.setId(2L);
        truck.setFleetManager(truckFleetManager);

        carDto = new VehicleDto(
            1L,
            "CAR-VIN-123",
            "CAR-LP-123",
            15000,
            new PowertrainInformationDto(
                EngineType.PETROL,
                150,
                50,
                2000,
                true
            ),
            "CAR"
        );

        truckDto = new VehicleDto(
            2L,
            "TRUCK-VIN-456",
            "TRUCK-LP-456",
            30000,
            new PowertrainInformationDto(
                EngineType.DIESEL,
                300,
                150,
                5000,
                false
            ),
            "TRUCK"
        );
    }

    @Nested
    @DisplayName("getAllVehicles Tests")
    class GetAllVehiclesTests {

        @Test
        @DisplayName("Should return paginated list of vehicles")
        void shouldReturnPaginatedListOfVehicles() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Vehicle> vehicles = List.of(car, truck);
            Page<Vehicle> vehiclePage = new PageImpl<>(vehicles, pageable, vehicles.size());

            PaginatedResponseDto<VehicleDto> expectedResponse = new PaginatedResponseDto<>(
                List.of(carDto, truckDto),
                0,
                10,
                2,
                1,
                false,
                false
            );

            when(vehicleRepository.findAll(pageable)).thenReturn(vehiclePage);
            when(vehicleMapper.toDto(car)).thenReturn(carDto);
            when(vehicleMapper.toDto(truck)).thenReturn(truckDto);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(expectedResponse);

            PaginatedResponseDto<VehicleDto> result = vehicleService.getAllVehicles(pageable);

            assertNotNull(result);
            assertEquals(2, result.content().size());
            assertEquals(carDto, result.content().get(0));
            assertEquals(truckDto, result.content().get(1));
            assertEquals(0, result.pageNumber());
            assertEquals(10, result.pageSize());
            assertEquals(2, result.totalElements());
            assertEquals(1, result.totalPages());

            verify(vehicleRepository, times(1)).findAll(pageable);
            verify(vehicleMapper, times(1)).toDto(car);
            verify(vehicleMapper, times(1)).toDto(truck);
            verify(paginationMapper, times(1)).toPaginatedResponse(any(Page.class));
        }

        @Test
        @DisplayName("Should return empty paginated list when no vehicles exist")
        void shouldReturnEmptyPaginatedListWhenNoVehiclesExist() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Vehicle> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            PaginatedResponseDto<VehicleDto> emptyResponse = new PaginatedResponseDto<>(
                List.of(),
                0,
                10,
                0,
                0,
                false,
                false
            );

            when(vehicleRepository.findAll(pageable)).thenReturn(emptyPage);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(emptyResponse);

            PaginatedResponseDto<VehicleDto> result = vehicleService.getAllVehicles(pageable);

            assertNotNull(result);
            assertTrue(result.content().isEmpty());
            assertEquals(0, result.totalElements());

            verify(vehicleRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(any(Page.class));
            verify(vehicleMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should handle pagination parameters correctly")
        void shouldHandlePaginationParametersCorrectly() {
            Pageable pageable = PageRequest.of(2, 5);
            List<Vehicle> vehicles = List.of(car);
            Page<Vehicle> vehiclePage = new PageImpl<>(vehicles, pageable, 15);

            PaginatedResponseDto<VehicleDto> expectedResponse = new PaginatedResponseDto<>(
                List.of(carDto),
                2,
                5,
                15,
                3,
                false,
                true
            );

            when(vehicleRepository.findAll(pageable)).thenReturn(vehiclePage);
            when(vehicleMapper.toDto(car)).thenReturn(carDto);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(expectedResponse);

            PaginatedResponseDto<VehicleDto> result = vehicleService.getAllVehicles(pageable);

            assertNotNull(result);
            assertEquals(2, result.pageNumber());
            assertEquals(5, result.pageSize());
            assertEquals(15, result.totalElements());
            assertEquals(3, result.totalPages());

            verify(vehicleRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(any(Page.class));
        }
    }

    @Nested
    @DisplayName("deleteVehicle Tests")
    class DeleteVehicleTests {

        @Test
        @DisplayName("Should delete vehicle by id")
        void shouldDeleteVehicleById() {
            Long vehicleId = 1L;
            doNothing().when(vehicleRepository).deleteById(vehicleId);

            vehicleService.deleteVehicle(vehicleId);

            verify(vehicleRepository, times(1)).deleteById(vehicleId);
        }

        @Test
        @DisplayName("Should handle deletion of non-existent vehicle")
        void shouldHandleDeletionOfNonExistentVehicle() {
            Long vehicleId = 999L;
            doNothing().when(vehicleRepository).deleteById(vehicleId);

            vehicleService.deleteVehicle(vehicleId);

            verify(vehicleRepository, times(1)).deleteById(vehicleId);
        }

        @Test
        @DisplayName("Should call repository delete method with correct id")
        void shouldCallRepositoryDeleteMethodWithCorrectId() {
            Long vehicleId = 42L;

            vehicleService.deleteVehicle(vehicleId);

            verify(vehicleRepository, times(1)).deleteById(eq(42L));
            verifyNoMoreInteractions(vehicleRepository);
        }
    }
}
