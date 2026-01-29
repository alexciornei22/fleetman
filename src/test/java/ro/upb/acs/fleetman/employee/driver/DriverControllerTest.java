package ro.upb.acs.fleetman.employee.driver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.exception.FieldConflictException;
import ro.upb.acs.fleetman.vehicle.config.TestObjectMapperConfig;

import java.sql.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("DriverController Unit Tests")
@WebMvcTest(DriverController.class)
@Import(TestObjectMapperConfig.class)
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DriverService driverService;

    @Nested
    @DisplayName("createDriver Tests")
    class CreateDriverTests {

        @Test
        @DisplayName("Should create driver successfully")
        void shouldCreateDriverSuccessfully() throws Exception {
            DriverDto driverDto = driverDto();
            CreateDriverDto createDriverDto = createDriverDto();

            when(driverService.createDriver(any(CreateDriverDto.class))).thenReturn(driverDto);

            mockMvc.perform(post("/api/drivers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDriverDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(driverDto.id()))
                .andExpect(jsonPath("$.employeeCode").value(driverDto.employeeCode()))
                .andExpect(jsonPath("$.firstName").value(driverDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(driverDto.lastName()))
                .andExpect(jsonPath("$.email").value(driverDto.email()))
                .andExpect(jsonPath("$.phoneNumber").value(driverDto.phoneNumber()))
                .andExpect(jsonPath("$.tachographCardNumber").value(driverDto.tachographCardNumber()))
                .andExpect(jsonPath("$.licenses").isArray());

            verify(driverService).createDriver(any(CreateDriverDto.class));
        }

        @Test
        @DisplayName("Should return conflict when employee code already exists")
        void shouldReturnConflictWhenEmployeeCodeAlreadyExists() throws Exception {
            CreateDriverDto createDriverDto = createDriverDto();

            when(driverService.createDriver(any(CreateDriverDto.class)))
                .thenThrow(new FieldConflictException("employeeCode", "A driver with the provided employee code already exists."));

            mockMvc.perform(post("/api/drivers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDriverDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorField").value("employeeCode"))
                .andExpect(jsonPath("$.errorMessage").value("A driver with the provided employee code already exists."))
                .andExpect(jsonPath("$.errorField").exists())
                .andExpect(jsonPath("$.errorMessage").exists());

            verify(driverService).createDriver(any(CreateDriverDto.class));
        }

        @Test
        @DisplayName("Should return conflict when email already exists")
        void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
            CreateDriverDto createDriverDto = createDriverDto();

            when(driverService.createDriver(any(CreateDriverDto.class)))
                .thenThrow(new FieldConflictException("email", "A driver with the provided email already exists."));

            mockMvc.perform(post("/api/drivers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDriverDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorField").value("email"))
                .andExpect(jsonPath("$.errorMessage").value("A driver with the provided email already exists."))
                .andExpect(jsonPath("$.errorField").exists())
                .andExpect(jsonPath("$.errorMessage").exists());

            verify(driverService).createDriver(any(CreateDriverDto.class));
        }

        @Test
        @DisplayName("Should return bad request when validation fails")
        void shouldReturnBadRequestWhenValidationFails() throws Exception {
            CreateDriverDto invalidDto = new CreateDriverDto(
                null,
                null,
                null,
                "invalid-email",
                null,
                null,
                null,
                null
            );

            mockMvc.perform(post("/api/drivers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[*].errorField").exists())
                .andExpect(jsonPath("$.errors[*].errorMessage").exists());

            verify(driverService, never()).createDriver(any(CreateDriverDto.class));
        }
    }

    @Nested
    @DisplayName("getAllDrivers Tests")
    class GetAllDriversTests {

        @Test
        @DisplayName("Should return paginated list of drivers")
        void shouldReturnPaginatedListOfDrivers() throws Exception {
            DriverDto driverDto = driverDto();
            PaginatedResponseDto<DriverDto> page = new PaginatedResponseDto<>(
                List.of(driverDto),
                0,
                20,
                1,
                1,
                false,
                false
            );

            when(driverService.getAllDrivers(any())).thenReturn(page);

            mockMvc.perform(get("/api/drivers")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(driverDto.id()))
                .andExpect(jsonPath("$.content[0].employeeCode").value(driverDto.employeeCode()))
                .andExpect(jsonPath("$.content[0].firstName").value(driverDto.firstName()))
                .andExpect(jsonPath("$.content[0].lastName").value(driverDto.lastName()))
                .andExpect(jsonPath("$.content[0].email").value(driverDto.email()))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));

            verify(driverService).getAllDrivers(any());
        }
    }

    @Nested
    @DisplayName("deleteDriver Tests")
    class DeleteDriverTests {

        @Test
        @DisplayName("Should delete driver by id")
        void shouldDeleteDriverById() throws Exception {
            doNothing().when(driverService).deleteDriver(1L);

            mockMvc.perform(delete("/api/drivers/1"))
                .andExpect(status().isNoContent());

            verify(driverService).deleteDriver(1L);
        }
    }

    private CreateDriverDto createDriverDto() {
        License license = new License();
        license.setLicenseType(LicenseType.B);
        license.setIssueDate(Date.valueOf("2020-01-15"));
        license.setExpiryDate(Date.valueOf("2030-01-15"));

        return new CreateDriverDto(
            "DRV-001",
            "John",
            "Doe",
            "john.doe@example.com",
            "+40123456789",
            List.of(license),
            Date.valueOf("2027-12-31"),
            "TACH-12345"
        );
    }

    private DriverDto driverDto() {
        License license = new License();
        license.setLicenseType(LicenseType.B);
        license.setIssueDate(Date.valueOf("2020-01-15"));
        license.setExpiryDate(Date.valueOf("2030-01-15"));

        return new DriverDto(
            1L,
            "DRV-001",
            "John",
            "Doe",
            "john.doe@example.com",
            "+40123456789",
            List.of(license),
            Date.valueOf("2027-12-31"),
            "TACH-12345"
        );
    }
}
