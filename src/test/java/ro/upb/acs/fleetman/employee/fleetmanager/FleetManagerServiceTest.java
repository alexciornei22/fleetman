package ro.upb.acs.fleetman.employee.fleetmanager;

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
import ro.upb.acs.fleetman.exception.FieldConflictException;
import ro.upb.acs.fleetman.vehicle.base.Vehicle;
import ro.upb.acs.fleetman.vehicle.car.Car;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FleetManagerService Unit Tests")
class FleetManagerServiceTest {

    @Mock
    private FleetManagerMapper fleetManagerMapper;

    @Mock
    private PaginationMapper paginationMapper;

    @Mock
    private FleetManagerRepository fleetManagerRepository;

    @InjectMocks
    private FleetManagerService fleetManagerService;

    private CreateFleetManagerDto request;
    private FleetManager entity;
    private FleetManagerDto dto;

    @BeforeEach
    void setUp() {
        request = new CreateFleetManagerDto(
            "FM-001",
            "Fleet",
            "Manager",
            "fleet.manager@example.com",
            "+40123456700"
        );

        entity = new FleetManager();
        entity.setId(1L);
        entity.setEmployeeCode(request.employeeCode());
        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
        entity.setEmail(request.email());
        entity.setPhoneNumber(request.phoneNumber());
        entity.setManagedVehicles(vehiclesOfSize(5));

        dto = new FleetManagerDto(
            1L,
            request.employeeCode(),
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phoneNumber(),
            5
        );
    }

    private List<Vehicle> vehiclesOfSize(int count) {
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            vehicles.add(new Car());
        }
        return vehicles;
    }

    @Nested
    @DisplayName("createFleetManager Tests")
    class CreateFleetManagerTests {

        @Test
        @DisplayName("Should create fleet manager successfully")
        void shouldCreateFleetManagerSuccessfully() {
            when(fleetManagerMapper.toEntity(request)).thenReturn(entity);
            when(fleetManagerRepository.save(entity)).thenReturn(entity);
            when(fleetManagerMapper.toDto(entity)).thenReturn(dto);

            FleetManagerDto result = fleetManagerService.createFleetManager(request);

            assertNotNull(result);
            assertEquals(dto.id(), result.id());
            assertEquals(dto.employeeCode(), result.employeeCode());
            assertEquals(dto.email(), result.email());
            assertEquals(dto.vehicleCount(), result.vehicleCount());

            verify(fleetManagerMapper, times(1)).toEntity(request);
            verify(fleetManagerRepository, times(1)).save(entity);
            verify(fleetManagerMapper, times(1)).toDto(entity);
        }

        @Test
        @DisplayName("Should throw FieldConflictException when employee code already exists")
        void shouldThrowExceptionWhenEmployeeCodeAlreadyExists() {
            when(fleetManagerMapper.toEntity(request)).thenReturn(entity);
            when(fleetManagerRepository.save(entity)).thenThrow(
                new DataIntegrityViolationException("Unique index or primary key violation: \"uc_employee_employee_code\"")
            );

            FieldConflictException exception = assertThrows(
                FieldConflictException.class,
                () -> fleetManagerService.createFleetManager(request)
            );

            assertEquals("employeeCode", exception.getErrorField());
            assertEquals("A fleet manager with the provided employee code already exists.", exception.getMessage());

            verify(fleetManagerMapper, times(1)).toEntity(request);
            verify(fleetManagerRepository, times(1)).save(entity);
            verify(fleetManagerMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should throw FieldConflictException when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            when(fleetManagerMapper.toEntity(request)).thenReturn(entity);
            when(fleetManagerRepository.save(entity)).thenThrow(
                new DataIntegrityViolationException("Unique index or primary key violation: \"uc_employee_email\"")
            );

            FieldConflictException exception = assertThrows(
                FieldConflictException.class,
                () -> fleetManagerService.createFleetManager(request)
            );

            assertEquals("email", exception.getErrorField());
            assertEquals("A fleet manager with the provided email already exists.", exception.getMessage());

            verify(fleetManagerMapper, times(1)).toEntity(request);
            verify(fleetManagerRepository, times(1)).save(entity);
            verify(fleetManagerMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should throw RuntimeException when unexpected constraint violation occurs")
        void shouldThrowRuntimeExceptionWhenUnexpectedViolation() {
            when(fleetManagerMapper.toEntity(request)).thenReturn(entity);
            DataIntegrityViolationException originalException = new DataIntegrityViolationException("Other violation");
            when(fleetManagerRepository.save(entity)).thenThrow(originalException);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> fleetManagerService.createFleetManager(request)
            );

            assertEquals(originalException, exception.getCause());

            verify(fleetManagerMapper, times(1)).toEntity(request);
            verify(fleetManagerRepository, times(1)).save(entity);
            verify(fleetManagerMapper, never()).toDto(any());
        }
    }

    @Nested
    @DisplayName("getAllFleetManagers Tests")
    class GetAllFleetManagersTests {

        @Test
        @DisplayName("Should return paginated list of fleet managers")
        void shouldReturnPaginatedListOfFleetManagers() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<FleetManager> page = new PageImpl<>(List.of(entity), pageable, 1);
            PaginatedResponseDto<FleetManagerDto> expectedResponse = new PaginatedResponseDto<>(
                List.of(dto),
                0,
                10,
                1,
                1,
                false,
                false
            );

            when(fleetManagerRepository.findAll(pageable)).thenReturn(page);
            when(fleetManagerMapper.toDto(entity)).thenReturn(dto);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(expectedResponse);

            PaginatedResponseDto<FleetManagerDto> result = fleetManagerService.getAllFleetManagers(pageable);

            assertNotNull(result);
            assertEquals(1, result.content().size());
            assertEquals(dto, result.content().getFirst());

            verify(fleetManagerRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(any(Page.class));
        }
    }

    @Nested
    @DisplayName("deleteFleetManager Tests")
    class DeleteFleetManagerTests {

        @Test
        @DisplayName("Should delete fleet manager by id")
        void shouldDeleteFleetManagerById() {
            Long id = 1L;
            doNothing().when(fleetManagerRepository).deleteById(id);

            fleetManagerService.deleteFleetManager(id);

            verify(fleetManagerRepository, times(1)).deleteById(id);
        }

        @Test
        @DisplayName("Should call delete method with different id")
        void shouldCallDeleteMethodWithDifferentId() {
            Long id = 42L;
            fleetManagerService.deleteFleetManager(id);

            verify(fleetManagerRepository, times(1)).deleteById(eq(42L));
            verifyNoMoreInteractions(fleetManagerRepository);
        }
    }
}
