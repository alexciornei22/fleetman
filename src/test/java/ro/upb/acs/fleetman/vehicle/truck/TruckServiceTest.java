package ro.upb.acs.fleetman.vehicle.truck;

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
import ro.upb.acs.fleetman.exception.ResourceNotFoundException;
import ro.upb.acs.fleetman.vehicle.base.EngineType;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformation;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TruckService Unit Tests")
class TruckServiceTest {

    @Mock
    private TruckMapper truckMapper;

    @Mock
    private PaginationMapper paginationMapper;

    @Mock
    private TruckRepository truckRepository;

    @Mock
    private FleetManagerRepository fleetManagerRepository;

    @InjectMocks
    private TruckService truckService;

    private CreateTruckDto createTruckDto;
    private Truck truck;
    private TruckDto truckDto;
    private UpdateTruckDto updateTruckDto;

    @BeforeEach
    void setUp() {
        PowertrainInformationDto powertrainInfoDto = new PowertrainInformationDto(
            EngineType.DIESEL,
            300,
            150,
            5000,
            false
        );

        createTruckDto = new CreateTruckDto(
            "1HGCM82633A456789",
            "B-456-TRK",
            25000,
            powertrainInfoDto,
            15000,
            4,
            true,
            1L
        );

        truck = new Truck();
        truck.setId(1L);
        truck.setVin(createTruckDto.vin());
        truck.setLicensePlate(createTruckDto.licensePlate());
        truck.setMileage(createTruckDto.mileage());
        truck.setMaxLoadKg(createTruckDto.maxLoadKg());
        truck.setNumberOfAxles(createTruckDto.numberOfAxles());
        truck.setHasRefrigerationUnit(createTruckDto.hasRefrigerationUnit());

        PowertrainInformation powertrainInfo = new PowertrainInformation();
        powertrainInfo.setEngineType(EngineType.DIESEL);
        powertrainInfo.setHorsepower(300);
        powertrainInfo.setFuelCapacityLiters(150);
        powertrainInfo.setEngineDisplacementCc(5000);
        powertrainInfo.setHasAutomaticTransmission(false);
        truck.setPowertrainInformation(powertrainInfo);

        FleetManager fleetManager = new FleetManager();
        fleetManager.setId(1L);
        truck.setFleetManager(fleetManager);

        truckDto = new TruckDto(
            1L,
            createTruckDto.vin(),
            createTruckDto.licensePlate(),
            createTruckDto.mileage(),
            powertrainInfoDto,
            createTruckDto.maxLoadKg(),
            createTruckDto.numberOfAxles(),
            createTruckDto.hasRefrigerationUnit(),
            1L
        );

        updateTruckDto = new UpdateTruckDto(
            "1HGCM82633A456789",
            "B-456-TRK",
            26000,
            new PowertrainInformationDto(EngineType.DIESEL, 320, 160, 5200, true),
            16000,
            5,
            false
        );
    }

    @Nested
    @DisplayName("createTruck Tests")
    class CreateTruckTests {

        @Test
        @DisplayName("Should create truck successfully when fleet manager exists")
        void shouldCreateTruckSuccessfully() {
            when(fleetManagerRepository.existsById(createTruckDto.fleetManagerId())).thenReturn(true);
            when(truckMapper.toEntity(createTruckDto)).thenReturn(truck);
            when(truckRepository.save(truck)).thenReturn(truck);
            when(truckMapper.toDto(truck)).thenReturn(truckDto);

            TruckDto result = truckService.createTruck(createTruckDto);

            assertNotNull(result);
            assertEquals(truckDto.id(), result.id());
            assertEquals(truckDto.vin(), result.vin());
            assertEquals(truckDto.licensePlate(), result.licensePlate());
            assertEquals(truckDto.mileage(), result.mileage());
            assertEquals(truckDto.maxLoadKg(), result.maxLoadKg());
            assertEquals(truckDto.numberOfAxles(), result.numberOfAxles());
            assertEquals(truckDto.hasRefrigerationUnit(), result.hasRefrigerationUnit());

            verify(fleetManagerRepository, times(1)).existsById(createTruckDto.fleetManagerId());
            verify(truckMapper, times(1)).toEntity(createTruckDto);
            verify(truckRepository, times(1)).save(truck);
            verify(truckMapper, times(1)).toDto(truck);
        }

        @Test
        @DisplayName("Should throw InvalidResourceReferenceException when fleet manager does not exist")
        void shouldThrowExceptionWhenFleetManagerDoesNotExist() {
            when(fleetManagerRepository.existsById(createTruckDto.fleetManagerId())).thenReturn(false);

            InvalidResourceReferenceException exception = assertThrows(
                InvalidResourceReferenceException.class,
                () -> truckService.createTruck(createTruckDto)
            );

            assertEquals("FleetManager", exception.getResourceType());
            assertEquals("1", exception.getResourceId());
            assertTrue(exception.getMessage().contains("FleetManager not found with id: 1"));

            verify(fleetManagerRepository, times(1)).existsById(createTruckDto.fleetManagerId());
            verify(truckMapper, never()).toEntity(any());
            verify(truckRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw FieldConflictException when VIN already exists")
        void shouldThrowExceptionWhenVinAlreadyExists() {
            when(fleetManagerRepository.existsById(createTruckDto.fleetManagerId())).thenReturn(true);
            when(truckMapper.toEntity(createTruckDto)).thenReturn(truck);
            when(truckRepository.save(truck)).thenThrow(
                new DataIntegrityViolationException("Unique index or primary key violation: \"uc_vehicle_vin\"")
            );

            FieldConflictException exception = assertThrows(
                FieldConflictException.class,
                () -> truckService.createTruck(createTruckDto)
            );

            assertEquals("vin", exception.getErrorField());
            assertEquals("A truck with the provided VIN already exists.", exception.getMessage());

            verify(fleetManagerRepository, times(1)).existsById(createTruckDto.fleetManagerId());
            verify(truckMapper, times(1)).toEntity(createTruckDto);
            verify(truckRepository, times(1)).save(truck);
            verify(truckMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should throw FieldConflictException when license plate already exists")
        void shouldThrowExceptionWhenLicensePlateAlreadyExists() {
            when(fleetManagerRepository.existsById(createTruckDto.fleetManagerId())).thenReturn(true);
            when(truckMapper.toEntity(createTruckDto)).thenReturn(truck);
            when(truckRepository.save(truck)).thenThrow(
                new DataIntegrityViolationException("Unique index or primary key violation: \"uc_vehicle_license_plate\"")
            );

            FieldConflictException exception = assertThrows(
                FieldConflictException.class,
                () -> truckService.createTruck(createTruckDto)
            );

            assertEquals("licensePlate", exception.getErrorField());
            assertEquals("A truck with the provided license plate already exists.", exception.getMessage());

            verify(fleetManagerRepository, times(1)).existsById(createTruckDto.fleetManagerId());
            verify(truckMapper, times(1)).toEntity(createTruckDto);
            verify(truckRepository, times(1)).save(truck);
            verify(truckMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should throw RuntimeException when an unexpected DataIntegrityViolationException occurs")
        void shouldThrowRuntimeExceptionWhenUnexpectedDataIntegrityViolation() {
            when(fleetManagerRepository.existsById(createTruckDto.fleetManagerId())).thenReturn(true);
            when(truckMapper.toEntity(createTruckDto)).thenReturn(truck);
            DataIntegrityViolationException originalException = new DataIntegrityViolationException(
                "Some other constraint violation"
            );
            when(truckRepository.save(truck)).thenThrow(originalException);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> truckService.createTruck(createTruckDto)
            );

            assertEquals(originalException, exception.getCause());

            verify(fleetManagerRepository, times(1)).existsById(createTruckDto.fleetManagerId());
            verify(truckMapper, times(1)).toEntity(createTruckDto);
            verify(truckRepository, times(1)).save(truck);
            verify(truckMapper, never()).toDto(any());
        }
    }

    @Nested
    @DisplayName("updateTruck Tests")
    class UpdateTruckTests {

        @Test
        @DisplayName("Should update truck successfully")
        void shouldUpdateTruckSuccessfully() {
            when(truckRepository.findById(1L)).thenReturn(java.util.Optional.of(truck));
            doNothing().when(truckMapper).updateEntity(updateTruckDto, truck);
            when(truckRepository.save(truck)).thenReturn(truck);
            when(truckMapper.toDto(truck)).thenReturn(truckDto);

            TruckDto result = truckService.updateTruck(1L, updateTruckDto);

            assertEquals(truckDto, result);
            verify(truckRepository).findById(1L);
            verify(truckMapper).updateEntity(updateTruckDto, truck);
            verify(truckRepository).save(truck);
            verify(truckMapper).toDto(truck);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when truck missing")
        void shouldThrowResourceNotFoundWhenTruckMissing() {
            when(truckRepository.findById(1L)).thenReturn(java.util.Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> truckService.updateTruck(1L, updateTruckDto));

            verify(truckRepository).findById(1L);
            verify(truckMapper, never()).updateEntity(any(UpdateTruckDto.class), any(Truck.class));
        }

        @Test
        @DisplayName("Should throw FieldConflictException when VIN conflicts on update")
        void shouldThrowVinConflictOnUpdate() {
            when(truckRepository.findById(1L)).thenReturn(java.util.Optional.of(truck));
            doNothing().when(truckMapper).updateEntity(updateTruckDto, truck);
            when(truckRepository.save(truck)).thenThrow(new DataIntegrityViolationException("uc_vehicle_vin"));

            assertThrows(FieldConflictException.class, () -> truckService.updateTruck(1L, updateTruckDto));

            verify(truckRepository).save(truck);
        }

        @Test
        @DisplayName("Should throw FieldConflictException when license plate conflicts on update")
        void shouldThrowLicenseConflictOnUpdate() {
            when(truckRepository.findById(1L)).thenReturn(java.util.Optional.of(truck));
            doNothing().when(truckMapper).updateEntity(updateTruckDto, truck);
            when(truckRepository.save(truck)).thenThrow(new DataIntegrityViolationException("uc_vehicle_license_plate"));

            assertThrows(FieldConflictException.class, () -> truckService.updateTruck(1L, updateTruckDto));

            verify(truckRepository).save(truck);
        }
    }

    @Nested
    @DisplayName("getAllTrucks Tests")
    class GetAllTrucksTests {

        @Test
        @DisplayName("Should return paginated list of trucks")
        void shouldReturnPaginatedListOfTrucks() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Truck> trucks = List.of(truck);
            Page<Truck> truckPage = new PageImpl<>(trucks, pageable, trucks.size());

            PaginatedResponseDto<TruckDto> expectedResponse = new PaginatedResponseDto<>(
                List.of(truckDto),
                0,
                10,
                1,
                1,
                false,
                false
            );

            when(truckRepository.findAll(pageable)).thenReturn(truckPage);
            when(truckMapper.toDto(truck)).thenReturn(truckDto);
            when(paginationMapper.toPaginatedResponse(Mockito.<Page<TruckDto>>any())).thenReturn(expectedResponse);

            PaginatedResponseDto<TruckDto> result = truckService.getAllTrucks(pageable);

            assertNotNull(result);
            assertEquals(1, result.content().size());
            assertEquals(truckDto, result.content().getFirst());
            assertEquals(0, result.pageNumber());
            assertEquals(10, result.pageSize());
            assertEquals(1, result.totalElements());
            assertEquals(1, result.totalPages());

            verify(truckRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(Mockito.<Page<TruckDto>>any());
        }

        @Test
        @DisplayName("Should return empty paginated list when no trucks exist")
        void shouldReturnEmptyPaginatedListWhenNoTrucksExist() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Truck> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            PaginatedResponseDto<TruckDto> emptyResponse = new PaginatedResponseDto<>(
                List.of(),
                0,
                10,
                0,
                0,
                false,
                false
            );

            when(truckRepository.findAll(pageable)).thenReturn(emptyPage);
            when(paginationMapper.toPaginatedResponse(Mockito.<Page<TruckDto>>any())).thenReturn(emptyResponse);

            PaginatedResponseDto<TruckDto> result = truckService.getAllTrucks(pageable);

            assertNotNull(result);
            assertTrue(result.content().isEmpty());
            assertEquals(0, result.totalElements());

            verify(truckRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(Mockito.<Page<TruckDto>>any());
        }

        @Test
        @DisplayName("Should handle pagination parameters correctly")
        void shouldHandlePaginationParametersCorrectly() {
            Pageable pageable = PageRequest.of(2, 5);
            List<Truck> trucks = List.of(truck);
            Page<Truck> truckPage = new PageImpl<>(trucks, pageable, 15);

            PaginatedResponseDto<TruckDto> expectedResponse = new PaginatedResponseDto<>(
                List.of(truckDto),
                2,
                5,
                15,
                3,
                false,
                true
            );

            when(truckRepository.findAll(pageable)).thenReturn(truckPage);
            when(truckMapper.toDto(truck)).thenReturn(truckDto);
            when(paginationMapper.toPaginatedResponse(Mockito.<Page<TruckDto>>any())).thenReturn(expectedResponse);

            PaginatedResponseDto<TruckDto> result = truckService.getAllTrucks(pageable);

            assertNotNull(result);
            assertEquals(2, result.pageNumber());
            assertEquals(5, result.pageSize());
            assertEquals(15, result.totalElements());
            assertEquals(3, result.totalPages());

            verify(truckRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(Mockito.<Page<TruckDto>>any());
        }
    }

    @Nested
    @DisplayName("deleteTruck Tests")
    class DeleteTruckTests {

        @Test
        @DisplayName("Should delete truck by id")
        void shouldDeleteTruckById() {
            Long truckId = 1L;
            doNothing().when(truckRepository).deleteById(truckId);

            truckService.deleteTruck(truckId);

            verify(truckRepository, times(1)).deleteById(truckId);
        }

        @Test
        @DisplayName("Should handle deletion of non-existent truck")
        void shouldHandleDeletionOfNonExistentTruck() {
            Long truckId = 999L;
            doNothing().when(truckRepository).deleteById(truckId);

            truckService.deleteTruck(truckId);

            verify(truckRepository, times(1)).deleteById(truckId);
        }

        @Test
        @DisplayName("Should call repository delete method with correct id")
        void shouldCallRepositoryDeleteMethodWithCorrectId() {
            Long truckId = 42L;

            truckService.deleteTruck(truckId);

            verify(truckRepository, times(1)).deleteById(eq(42L));
            verifyNoMoreInteractions(truckRepository);
        }
    }
}
