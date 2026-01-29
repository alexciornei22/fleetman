package ro.upb.acs.fleetman.trip;

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
import ro.upb.acs.fleetman.GlobalExceptionHandler;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.exception.InvalidResourceReferenceException;
import ro.upb.acs.fleetman.exception.ResourceNotFoundException;
import ro.upb.acs.fleetman.vehicle.config.TestObjectMapperConfig;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("TripController Unit Tests")
@WebMvcTest(TripController.class)
@Import({TestObjectMapperConfig.class, GlobalExceptionHandler.class})
public class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TripService tripService;

    @Nested
    @DisplayName("createTrip Tests")
    class CreateTripTests {

        @Test
        @DisplayName("Should create trip successfully")
        void shouldCreateTripSuccessfully() throws Exception {
            TripDto dto = tripDto();
            CreateTripDto request = createTripDto();

            when(tripService.createTrip(any(CreateTripDto.class))).thenReturn(dto);

            mockMvc.perform(post("/api/trips")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dto.id()))
                .andExpect(jsonPath("$.vehicleId").value(dto.vehicleId()))
                .andExpect(jsonPath("$.driverId").value(dto.driverId()))
                .andExpect(jsonPath("$.startLocation").value(dto.startLocation()))
                .andExpect(jsonPath("$.endLocation").value(dto.endLocation()))
                .andExpect(jsonPath("$.status").value(dto.status().name()));
        }

        @Test
        @DisplayName("Should return unprocessable content when vehicle missing")
        void shouldReturnUnprocessableContentWhenVehicleMissing() throws Exception {
            CreateTripDto request = createTripDto();
            when(tripService.createTrip(any(CreateTripDto.class)))
                .thenThrow(new InvalidResourceReferenceException("Vehicle", "99"));

            mockMvc.perform(post("/api/trips")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.resourceType").value("Vehicle"))
                .andExpect(jsonPath("$.resourceId").value("99"));
        }
    }

    @Nested
    @DisplayName("getAllTrips Tests")
    class GetAllTripsTests {

        @Test
        @DisplayName("Should return paginated trips")
        void shouldReturnPaginatedTrips() throws Exception {
            TripDto dto = tripDto();
            PaginatedResponseDto<TripDto> page = new PaginatedResponseDto<>(
                List.of(dto),
                0,
                20,
                1,
                1,
                false,
                false
            );

            when(tripService.getAllTrips(any())).thenReturn(page);

            mockMvc.perform(get("/api/trips")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(dto.id()))
                .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    @Nested
    @DisplayName("getTripsForVehicle Tests")
    class GetTripsForVehicleTests {

        @Test
        @DisplayName("Should return trips for vehicle")
        void shouldReturnTripsForVehicle() throws Exception {
            TripDto dto = tripDto();
            PaginatedResponseDto<TripDto> page = new PaginatedResponseDto<>(
                List.of(dto),
                0,
                20,
                1,
                1,
                false,
                false
            );

            when(tripService.getTripsForVehicle(eq(1L), any())).thenReturn(page);

            mockMvc.perform(get("/api/trips/vehicle/1")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].vehicleId").value(dto.vehicleId()));
        }

        @Test
        @DisplayName("Should return not found when vehicle absent")
        void shouldReturnNotFoundWhenVehicleAbsent() throws Exception {
            when(tripService.getTripsForVehicle(eq(1L), any()))
                .thenThrow(new ResourceNotFoundException("Vehicle", "1"));

            mockMvc.perform(get("/api/trips/vehicle/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resourceType").value("Vehicle"));
        }
    }

    @Nested
    @DisplayName("getTripsForDriver Tests")
    class GetTripsForDriverTests {

        @Test
        @DisplayName("Should return trips for driver")
        void shouldReturnTripsForDriver() throws Exception {
            TripDto dto = tripDto();
            PaginatedResponseDto<TripDto> page = new PaginatedResponseDto<>(
                List.of(dto),
                0,
                20,
                1,
                1,
                false,
                false
            );

            when(tripService.getTripsForDriver(eq(1L), any())).thenReturn(page);

            mockMvc.perform(get("/api/trips/driver/1")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].driverId").value(dto.driverId()));
        }
    }

    @Nested
    @DisplayName("deleteTrip Tests")
    class DeleteTripTests {

        @Test
        @DisplayName("Should delete trip by id")
        void shouldDeleteTripById() throws Exception {
            doNothing().when(tripService).deleteTrip(1L);

            mockMvc.perform(delete("/api/trips/1"))
                .andExpect(status().isNoContent());

            verify(tripService).deleteTrip(1L);
        }
    }

    private CreateTripDto createTripDto() {
        return new CreateTripDto(
            1L,
            1L,
            "City A",
            "City B",
            LocalDateTime.of(2026, 1, 1, 8, 0),
            LocalDateTime.of(2026, 1, 1, 10, 0),
            150.0,
            TripStatus.SCHEDULED,
            "Notes"
        );
    }

    private TripDto tripDto() {
        return new TripDto(
            1L,
            1L,
            1L,
            "City A",
            "City B",
            LocalDateTime.of(2026, 1, 1, 8, 0),
            LocalDateTime.of(2026, 1, 1, 10, 0),
            150.0,
            TripStatus.SCHEDULED,
            "Notes"
        );
    }
}
