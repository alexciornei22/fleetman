package ro.upb.acs.fleetman.vehicle.car;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.common.PaginationMapper;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManager;
import ro.upb.acs.fleetman.employee.fleetmanager.FleetManagerRepository;
import ro.upb.acs.fleetman.exception.FieldConflictException;
import ro.upb.acs.fleetman.exception.InvalidResourceReferenceException;
import ro.upb.acs.fleetman.vehicle.base.EngineType;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformation;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarService Unit Tests")
class CarServiceTest {

    @Mock
    private CarMapper carMapper;

    @Mock
    private PaginationMapper paginationMapper;

    @Mock
    private CarRepository carRepository;

    @Mock
    private FleetManagerRepository fleetManagerRepository;

    @InjectMocks
    private CarService carService;

    private CreateCarDto createCarDto;
    private Car car;
    private CarDto carDto;

    @BeforeEach
    void setUp() {
        PowertrainInformationDto powertrainInfoDto = new PowertrainInformationDto(
            EngineType.PETROL,
            150,
            50,
            1600,
            true
        );

        createCarDto = new CreateCarDto(
            "1HGCM82633A123456",
            "B-123-ABC",
            10000,
            powertrainInfoDto,
            5,
            4,
            true,
            true,
            CarBodyType.SEDAN,
            1L
        );

        car = new Car();
        car.setId(1L);
        car.setVin(createCarDto.vin());
        car.setLicensePlate(createCarDto.licensePlate());
        car.setMileage(createCarDto.mileage());
        car.setNumberOfSeats(createCarDto.numberOfSeats());
        car.setNumberOfDoors(createCarDto.numberOfDoors());
        car.setIsChildSeatCompatible(createCarDto.isChildSeatCompatible());
        car.setHasSunroof(createCarDto.hasSunroof());
        car.setCarBodyType(createCarDto.carBodyType());

        PowertrainInformation powertrainInfo = new PowertrainInformation();
        powertrainInfo.setEngineType(EngineType.PETROL);
        powertrainInfo.setHorsepower(150);
        powertrainInfo.setFuelCapacityLiters(50);
        powertrainInfo.setEngineDisplacementCc(1600);
        powertrainInfo.setHasAutomaticTransmission(true);
        car.setPowertrainInformation(powertrainInfo);

        FleetManager fleetManager = new FleetManager();
        fleetManager.setId(1L);
        car.setFleetManager(fleetManager);

        carDto = new CarDto(
            1L,
            createCarDto.vin(),
            createCarDto.licensePlate(),
            createCarDto.mileage(),
            powertrainInfoDto,
            createCarDto.numberOfSeats(),
            createCarDto.numberOfDoors(),
            createCarDto.isChildSeatCompatible(),
            createCarDto.hasSunroof(),
            createCarDto.carBodyType(),
            1L
        );
    }

    @Nested
    @DisplayName("createCar Tests")
    class CreateCarTests {

        @Test
        @DisplayName("Should create car successfully when fleet manager exists")
        void shouldCreateCarSuccessfully() {
            when(fleetManagerRepository.existsById(createCarDto.fleetManagerId())).thenReturn(true);
            when(carMapper.toEntity(createCarDto)).thenReturn(car);
            when(carRepository.save(car)).thenReturn(car);
            when(carMapper.toDto(car)).thenReturn(carDto);

            CarDto result = carService.createCar(createCarDto);

            assertNotNull(result);
            assertEquals(carDto.id(), result.id());
            assertEquals(carDto.vin(), result.vin());
            assertEquals(carDto.licensePlate(), result.licensePlate());
            assertEquals(carDto.mileage(), result.mileage());
            assertEquals(carDto.numberOfSeats(), result.numberOfSeats());
            assertEquals(carDto.numberOfDoors(), result.numberOfDoors());
            assertEquals(carDto.carBodyType(), result.carBodyType());

            verify(fleetManagerRepository, times(1)).existsById(createCarDto.fleetManagerId());
            verify(carMapper, times(1)).toEntity(createCarDto);
            verify(carRepository, times(1)).save(car);
            verify(carMapper, times(1)).toDto(car);
        }

        @Test
        @DisplayName("Should throw InvalidResourceReferenceException when fleet manager does not exist")
        void shouldThrowExceptionWhenFleetManagerDoesNotExist() {
            when(fleetManagerRepository.existsById(createCarDto.fleetManagerId())).thenReturn(false);

            InvalidResourceReferenceException exception = assertThrows(
                InvalidResourceReferenceException.class,
                () -> carService.createCar(createCarDto)
            );

            assertEquals("FleetManager", exception.getResourceType());
            assertEquals("1", exception.getResourceId());
            assertTrue(exception.getMessage().contains("FleetManager not found with id: 1"));

            verify(fleetManagerRepository, times(1)).existsById(createCarDto.fleetManagerId());
            verify(carMapper, never()).toEntity(any());
            verify(carRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw FieldConflictException when VIN already exists")
        void shouldThrowExceptionWhenVinAlreadyExists() {
            when(fleetManagerRepository.existsById(createCarDto.fleetManagerId())).thenReturn(true);
            when(carMapper.toEntity(createCarDto)).thenReturn(car);
            when(carRepository.save(car)).thenThrow(
                new DataIntegrityViolationException("Unique index or primary key violation: \"uc_vehicle_vin\"")
            );

            FieldConflictException exception = assertThrows(
                FieldConflictException.class,
                () -> carService.createCar(createCarDto)
            );

            assertEquals("vin", exception.getErrorField());
            assertEquals("A car with the provided VIN already exists.", exception.getMessage());

            verify(fleetManagerRepository, times(1)).existsById(createCarDto.fleetManagerId());
            verify(carMapper, times(1)).toEntity(createCarDto);
            verify(carRepository, times(1)).save(car);
            verify(carMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should throw FieldConflictException when license plate already exists")
        void shouldThrowExceptionWhenLicensePlateAlreadyExists() {
            when(fleetManagerRepository.existsById(createCarDto.fleetManagerId())).thenReturn(true);
            when(carMapper.toEntity(createCarDto)).thenReturn(car);
            when(carRepository.save(car)).thenThrow(
                new DataIntegrityViolationException("Unique index or primary key violation: \"uc_vehicle_license_plate\"")
            );

            FieldConflictException exception = assertThrows(
                FieldConflictException.class,
                () -> carService.createCar(createCarDto)
            );

            assertEquals("licensePlate", exception.getErrorField());
            assertEquals("A car with the provided license plate already exists.", exception.getMessage());

            verify(fleetManagerRepository, times(1)).existsById(createCarDto.fleetManagerId());
            verify(carMapper, times(1)).toEntity(createCarDto);
            verify(carRepository, times(1)).save(car);
            verify(carMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should throw RuntimeException when an unexpected DataIntegrityViolationException occurs")
        void shouldThrowRuntimeExceptionWhenUnexpectedDataIntegrityViolation() {
            when(fleetManagerRepository.existsById(createCarDto.fleetManagerId())).thenReturn(true);
            when(carMapper.toEntity(createCarDto)).thenReturn(car);
            DataIntegrityViolationException originalException = new DataIntegrityViolationException(
                "Some other constraint violation"
            );
            when(carRepository.save(car)).thenThrow(originalException);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> carService.createCar(createCarDto)
            );

            assertEquals(originalException, exception.getCause());

            verify(fleetManagerRepository, times(1)).existsById(createCarDto.fleetManagerId());
            verify(carMapper, times(1)).toEntity(createCarDto);
            verify(carRepository, times(1)).save(car);
            verify(carMapper, never()).toDto(any());
        }
    }

    @Nested
    @DisplayName("getAllCars Tests")
    class GetAllCarsTests {

        @Test
        @DisplayName("Should return paginated list of cars")
        void shouldReturnPaginatedListOfCars() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Car> cars = List.of(car);
            Page<Car> carPage = new PageImpl<>(cars, pageable, cars.size());

            PaginatedResponseDto<CarDto> expectedResponse = new PaginatedResponseDto<>(
                List.of(carDto),
                0,
                10,
                1,
                1,
                false,
                false
            );

            when(carRepository.findAll(pageable)).thenReturn(carPage);
            when(carMapper.toDto(car)).thenReturn(carDto);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(expectedResponse);

            PaginatedResponseDto<CarDto> result = carService.getAllCars(pageable);

            assertNotNull(result);
            assertEquals(1, result.content().size());
            assertEquals(carDto, result.content().getFirst());
            assertEquals(0, result.pageNumber());
            assertEquals(10, result.pageSize());
            assertEquals(1, result.totalElements());
            assertEquals(1, result.totalPages());

            verify(carRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(any(Page.class));
        }

        @Test
        @DisplayName("Should return empty paginated list when no cars exist")
        void shouldReturnEmptyPaginatedListWhenNoCarsExist() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Car> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            PaginatedResponseDto<CarDto> emptyResponse = new PaginatedResponseDto<>(
                List.of(),
                0,
                10,
                0,
                0,
                false,
                false
            );

            when(carRepository.findAll(pageable)).thenReturn(emptyPage);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(emptyResponse);

            PaginatedResponseDto<CarDto> result = carService.getAllCars(pageable);

            assertNotNull(result);
            assertTrue(result.content().isEmpty());
            assertEquals(0, result.totalElements());

            verify(carRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(any(Page.class));
        }

        @Test
        @DisplayName("Should handle pagination parameters correctly")
        void shouldHandlePaginationParametersCorrectly() {
            Pageable pageable = PageRequest.of(2, 5);
            List<Car> cars = List.of(car);
            Page<Car> carPage = new PageImpl<>(cars, pageable, 15);

            PaginatedResponseDto<CarDto> expectedResponse = new PaginatedResponseDto<>(
                List.of(carDto),
                2,
                5,
                15,
                3,
                false,
                true
            );

            when(carRepository.findAll(pageable)).thenReturn(carPage);
            when(carMapper.toDto(car)).thenReturn(carDto);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(expectedResponse);

            PaginatedResponseDto<CarDto> result = carService.getAllCars(pageable);

            assertNotNull(result);
            assertEquals(2, result.pageNumber());
            assertEquals(5, result.pageSize());
            assertEquals(15, result.totalElements());
            assertEquals(3, result.totalPages());

            verify(carRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(any(Page.class));
        }
    }

    @Nested
    @DisplayName("deleteCar Tests")
    class DeleteCarTests {

        @Test
        @DisplayName("Should delete car by id")
        void shouldDeleteCarById() {
            Long carId = 1L;
            doNothing().when(carRepository).deleteById(carId);

            carService.deleteCar(carId);

            verify(carRepository, times(1)).deleteById(carId);
        }

        @Test
        @DisplayName("Should handle deletion of non-existent car")
        void shouldHandleDeletionOfNonExistentCar() {
            Long carId = 999L;
            doNothing().when(carRepository).deleteById(carId);

            carService.deleteCar(carId);

            verify(carRepository, times(1)).deleteById(carId);
        }

        @Test
        @DisplayName("Should call repository delete method with correct id")
        void shouldCallRepositoryDeleteMethodWithCorrectId() {
            Long carId = 42L;

            carService.deleteCar(carId);

            verify(carRepository, times(1)).deleteById(eq(42L));
            verifyNoMoreInteractions(carRepository);
        }
    }
}
