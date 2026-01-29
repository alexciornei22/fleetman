package ro.upb.acs.fleetman.employee.fleetmanager;

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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("FleetManagerController Unit Tests")
@WebMvcTest(FleetManagerController.class)
@Import(TestObjectMapperConfig.class)
public class FleetManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FleetManagerService fleetManagerService;

    @Nested
    @DisplayName("createFleetManager Tests")
    class CreateFleetManagerTests {

        @Test
        @DisplayName("Should create fleet manager successfully")
        void shouldCreateFleetManagerSuccessfully() throws Exception {
            FleetManagerDto dto = fleetManagerDto();
            CreateFleetManagerDto request = createFleetManagerDto();

            when(fleetManagerService.createFleetManager(any(CreateFleetManagerDto.class))).thenReturn(dto);

            mockMvc.perform(post("/api/fleet-managers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dto.id()))
                .andExpect(jsonPath("$.employeeCode").value(dto.employeeCode()))
                .andExpect(jsonPath("$.firstName").value(dto.firstName()))
                .andExpect(jsonPath("$.lastName").value(dto.lastName()))
                .andExpect(jsonPath("$.email").value(dto.email()))
                .andExpect(jsonPath("$.vehicleCount").value(dto.vehicleCount()));

            verify(fleetManagerService).createFleetManager(any(CreateFleetManagerDto.class));
        }

        @Test
        @DisplayName("Should return conflict when employee code already exists")
        void shouldReturnConflictWhenEmployeeCodeExists() throws Exception {
            CreateFleetManagerDto request = createFleetManagerDto();

            when(fleetManagerService.createFleetManager(any(CreateFleetManagerDto.class)))
                .thenThrow(new FieldConflictException("employeeCode", "A fleet manager with the provided employee code already exists."));

            mockMvc.perform(post("/api/fleet-managers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorField").value("employeeCode"))
                .andExpect(jsonPath("$.errorMessage").value("A fleet manager with the provided employee code already exists."));

            verify(fleetManagerService).createFleetManager(any(CreateFleetManagerDto.class));
        }

        @Test
        @DisplayName("Should return conflict when email already exists")
        void shouldReturnConflictWhenEmailExists() throws Exception {
            CreateFleetManagerDto request = createFleetManagerDto();

            when(fleetManagerService.createFleetManager(any(CreateFleetManagerDto.class)))
                .thenThrow(new FieldConflictException("email", "A fleet manager with the provided email already exists."));

            mockMvc.perform(post("/api/fleet-managers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorField").value("email"))
                .andExpect(jsonPath("$.errorMessage").value("A fleet manager with the provided email already exists."));

            verify(fleetManagerService).createFleetManager(any(CreateFleetManagerDto.class));
        }
    }

    @Nested
    @DisplayName("getAllFleetManagers Tests")
    class GetAllFleetManagersTests {

        @Test
        @DisplayName("Should return paginated list of fleet managers")
        void shouldReturnPaginatedListOfFleetManagers() throws Exception {
            FleetManagerDto dto = fleetManagerDto();
            PaginatedResponseDto<FleetManagerDto> page = new PaginatedResponseDto<>(
                List.of(dto),
                0,
                20,
                1,
                1,
                false,
                false
            );

            when(fleetManagerService.getAllFleetManagers(any())).thenReturn(page);

            mockMvc.perform(get("/api/fleet-managers")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].employeeCode").value(dto.employeeCode()))
                .andExpect(jsonPath("$.content[0].firstName").value(dto.firstName()))
                .andExpect(jsonPath("$.content[0].lastName").value(dto.lastName()))
                .andExpect(jsonPath("$.content[0].vehicleCount").value(dto.vehicleCount()))
                .andExpect(jsonPath("$.totalElements").value(1));

            verify(fleetManagerService).getAllFleetManagers(any());
        }
    }

    @Nested
    @DisplayName("deleteFleetManager Tests")
    class DeleteFleetManagerTests {

        @Test
        @DisplayName("Should delete fleet manager by id")
        void shouldDeleteFleetManagerById() throws Exception {
            doNothing().when(fleetManagerService).deleteFleetManager(1L);

            mockMvc.perform(delete("/api/fleet-managers/1"))
                .andExpect(status().isNoContent());

            verify(fleetManagerService).deleteFleetManager(1L);
        }
    }

    private CreateFleetManagerDto createFleetManagerDto() {
        return new CreateFleetManagerDto(
            "FM-001",
            "Fleet",
            "Manager",
            "fleet.manager@example.com",
            "+40123456700"
        );
    }

    private FleetManagerDto fleetManagerDto() {
        return new FleetManagerDto(
            1L,
            "FM-001",
            "Fleet",
            "Manager",
            "fleet.manager@example.com",
            "+40123456700",
            5
        );
    }
}
