package ro.upb.acs.fleetman.employee.driver;

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

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DriverService Unit Tests")
class DriverServiceTest {

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private PaginationMapper paginationMapper;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    private CreateDriverDto createDriverDto;
    private Driver driver;
    private DriverDto driverDto;

    @BeforeEach
    void setUp() {
        License license = new License();
        license.setLicenseType(LicenseType.B);
        license.setIssueDate(Date.valueOf("2020-01-15"));
        license.setExpiryDate(Date.valueOf("2030-01-15"));

        createDriverDto = new CreateDriverDto(
            "DRV-001",
            "John",
            "Doe",
            "john.doe@example.com",
            "+40123456789",
            List.of(license),
            Date.valueOf("2027-12-31"),
            "TACH-12345"
        );

        driver = new Driver();
        driver.setId(1L);
        driver.setEmployeeCode(createDriverDto.employeeCode());
        driver.setFirstName(createDriverDto.firstName());
        driver.setLastName(createDriverDto.lastName());
        driver.setEmail(createDriverDto.email());
        driver.setPhoneNumber(createDriverDto.phoneNumber());
        driver.setLicenses(createDriverDto.licenses());
        driver.setMedicalCertificateExpiryDate(createDriverDto.medicalCertificateExpiryDate());
        driver.setTachographCardNumber(createDriverDto.tachographCardNumber());

        driverDto = new DriverDto(
            1L,
            createDriverDto.employeeCode(),
            createDriverDto.firstName(),
            createDriverDto.lastName(),
            createDriverDto.email(),
            createDriverDto.phoneNumber(),
            createDriverDto.licenses(),
            createDriverDto.medicalCertificateExpiryDate(),
            createDriverDto.tachographCardNumber()
        );
    }

    @Nested
    @DisplayName("createDriver Tests")
    class CreateDriverTests {

        @Test
        @DisplayName("Should create driver successfully")
        void shouldCreateDriverSuccessfully() {
            when(driverMapper.toEntity(createDriverDto)).thenReturn(driver);
            when(driverRepository.save(driver)).thenReturn(driver);
            when(driverMapper.toDto(driver)).thenReturn(driverDto);

            DriverDto result = driverService.createDriver(createDriverDto);

            assertNotNull(result);
            assertEquals(driverDto.id(), result.id());
            assertEquals(driverDto.employeeCode(), result.employeeCode());
            assertEquals(driverDto.firstName(), result.firstName());
            assertEquals(driverDto.lastName(), result.lastName());
            assertEquals(driverDto.email(), result.email());
            assertEquals(driverDto.phoneNumber(), result.phoneNumber());
            assertEquals(driverDto.tachographCardNumber(), result.tachographCardNumber());

            verify(driverMapper, times(1)).toEntity(createDriverDto);
            verify(driverRepository, times(1)).save(driver);
            verify(driverMapper, times(1)).toDto(driver);
        }

        @Test
        @DisplayName("Should throw FieldConflictException when employee code already exists")
        void shouldThrowExceptionWhenEmployeeCodeAlreadyExists() {
            when(driverMapper.toEntity(createDriverDto)).thenReturn(driver);
            when(driverRepository.save(driver)).thenThrow(
                new DataIntegrityViolationException("Unique index or primary key violation: \"uc_employee_employee_code\"")
            );

            FieldConflictException exception = assertThrows(
                FieldConflictException.class,
                () -> driverService.createDriver(createDriverDto)
            );

            assertEquals("employeeCode", exception.getErrorField());
            assertEquals("A driver with the provided employee code already exists.", exception.getMessage());

            verify(driverMapper, times(1)).toEntity(createDriverDto);
            verify(driverRepository, times(1)).save(driver);
            verify(driverMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should throw FieldConflictException when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            when(driverMapper.toEntity(createDriverDto)).thenReturn(driver);
            when(driverRepository.save(driver)).thenThrow(
                new DataIntegrityViolationException("Unique index or primary key violation: \"uc_employee_email\"")
            );

            FieldConflictException exception = assertThrows(
                FieldConflictException.class,
                () -> driverService.createDriver(createDriverDto)
            );

            assertEquals("email", exception.getErrorField());
            assertEquals("A driver with the provided email already exists.", exception.getMessage());

            verify(driverMapper, times(1)).toEntity(createDriverDto);
            verify(driverRepository, times(1)).save(driver);
            verify(driverMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should throw FieldConflictException when tachograph card number already exists")
        void shouldThrowExceptionWhenTachographCardNumberAlreadyExists() {
            when(driverMapper.toEntity(createDriverDto)).thenReturn(driver);
            when(driverRepository.save(driver)).thenThrow(
                new DataIntegrityViolationException("Unique index or primary key violation: \"uc_driver_tachograph_card_number\"")
            );

            FieldConflictException exception = assertThrows(
                FieldConflictException.class,
                () -> driverService.createDriver(createDriverDto)
            );

            assertEquals("tachographCardNumber", exception.getErrorField());
            assertEquals("A driver with the provided tachograph card number already exists.", exception.getMessage());

            verify(driverMapper, times(1)).toEntity(createDriverDto);
            verify(driverRepository, times(1)).save(driver);
            verify(driverMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("Should throw RuntimeException when an unexpected DataIntegrityViolationException occurs")
        void shouldThrowRuntimeExceptionWhenUnexpectedDataIntegrityViolation() {
            when(driverMapper.toEntity(createDriverDto)).thenReturn(driver);
            DataIntegrityViolationException originalException = new DataIntegrityViolationException(
                "Some other constraint violation"
            );
            when(driverRepository.save(driver)).thenThrow(originalException);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> driverService.createDriver(createDriverDto)
            );

            assertEquals(originalException, exception.getCause());

            verify(driverMapper, times(1)).toEntity(createDriverDto);
            verify(driverRepository, times(1)).save(driver);
            verify(driverMapper, never()).toDto(any());
        }
    }

    @Nested
    @DisplayName("getAllDrivers Tests")
    class GetAllDriversTests {

        @Test
        @DisplayName("Should return paginated list of drivers")
        void shouldReturnPaginatedListOfDrivers() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Driver> drivers = List.of(driver);
            Page<Driver> driverPage = new PageImpl<>(drivers, pageable, drivers.size());

            PaginatedResponseDto<DriverDto> expectedResponse = new PaginatedResponseDto<>(
                List.of(driverDto),
                0,
                10,
                1,
                1,
                false,
                false
            );

            when(driverRepository.findAll(pageable)).thenReturn(driverPage);
            when(driverMapper.toDto(driver)).thenReturn(driverDto);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(expectedResponse);

            PaginatedResponseDto<DriverDto> result = driverService.getAllDrivers(pageable);

            assertNotNull(result);
            assertEquals(1, result.content().size());
            assertEquals(driverDto, result.content().getFirst());
            assertEquals(0, result.pageNumber());
            assertEquals(10, result.pageSize());
            assertEquals(1, result.totalElements());
            assertEquals(1, result.totalPages());

            verify(driverRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(any(Page.class));
        }

        @Test
        @DisplayName("Should return empty paginated list when no drivers exist")
        void shouldReturnEmptyPaginatedListWhenNoDriversExist() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Driver> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            PaginatedResponseDto<DriverDto> emptyResponse = new PaginatedResponseDto<>(
                List.of(),
                0,
                10,
                0,
                0,
                false,
                false
            );

            when(driverRepository.findAll(pageable)).thenReturn(emptyPage);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(emptyResponse);

            PaginatedResponseDto<DriverDto> result = driverService.getAllDrivers(pageable);

            assertNotNull(result);
            assertTrue(result.content().isEmpty());
            assertEquals(0, result.totalElements());

            verify(driverRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(any(Page.class));
        }

        @Test
        @DisplayName("Should handle pagination parameters correctly")
        void shouldHandlePaginationParametersCorrectly() {
            Pageable pageable = PageRequest.of(2, 5);
            List<Driver> drivers = List.of(driver);
            Page<Driver> driverPage = new PageImpl<>(drivers, pageable, 15);

            PaginatedResponseDto<DriverDto> expectedResponse = new PaginatedResponseDto<>(
                List.of(driverDto),
                2,
                5,
                15,
                3,
                false,
                true
            );

            when(driverRepository.findAll(pageable)).thenReturn(driverPage);
            when(driverMapper.toDto(driver)).thenReturn(driverDto);
            when(paginationMapper.toPaginatedResponse(any(Page.class))).thenReturn(expectedResponse);

            PaginatedResponseDto<DriverDto> result = driverService.getAllDrivers(pageable);

            assertNotNull(result);
            assertEquals(2, result.pageNumber());
            assertEquals(5, result.pageSize());
            assertEquals(15, result.totalElements());
            assertEquals(3, result.totalPages());

            verify(driverRepository, times(1)).findAll(pageable);
            verify(paginationMapper, times(1)).toPaginatedResponse(any(Page.class));
        }
    }

    @Nested
    @DisplayName("deleteDriver Tests")
    class DeleteDriverTests {

        @Test
        @DisplayName("Should delete driver by id")
        void shouldDeleteDriverById() {
            Long driverId = 1L;
            doNothing().when(driverRepository).deleteById(driverId);

            driverService.deleteDriver(driverId);

            verify(driverRepository, times(1)).deleteById(driverId);
        }

        @Test
        @DisplayName("Should handle deletion of non-existent driver")
        void shouldHandleDeletionOfNonExistentDriver() {
            Long driverId = 999L;
            doNothing().when(driverRepository).deleteById(driverId);

            driverService.deleteDriver(driverId);

            verify(driverRepository, times(1)).deleteById(driverId);
        }

        @Test
        @DisplayName("Should call repository delete method with correct id")
        void shouldCallRepositoryDeleteMethodWithCorrectId() {
            Long driverId = 42L;

            driverService.deleteDriver(driverId);

            verify(driverRepository, times(1)).deleteById(eq(42L));
            verifyNoMoreInteractions(driverRepository);
        }
    }
}
