package ro.upb.acs.fleetman.vehicle.base;

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
import ro.upb.acs.fleetman.exception.ResourceNotFoundException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("VehicleController Unit Tests")
@WebMvcTest(VehicleController.class)
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @Nested
    @DisplayName("getAllVehicles Tests")
    class GetAllVehiclesTests {

        @Test
        @DisplayName("Should return paginated list of vehicles")
        void shouldReturnPaginatedListOfVehicles() throws Exception {
            VehicleDto vehicleDto1 = createVehicleDto(1L, "CAR");
            VehicleDto vehicleDto2 = createVehicleDto(2L, "TRUCK");

            PaginatedResponseDto<VehicleDto> page = new PaginatedResponseDto<>(
                List.of(vehicleDto1, vehicleDto2),
                0,
                20,
                2,
                1,
                false,
                false
            );

            when(vehicleService.getAllVehicles(any())).thenReturn(page);

            mockMvc.perform(get("/api/vehicles")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(vehicleDto1.id()))
                .andExpect(jsonPath("$.content[0].vin").value(vehicleDto1.vin()))
                .andExpect(jsonPath("$.content[0].licensePlate").value(vehicleDto1.licensePlate()))
                .andExpect(jsonPath("$.content[0].mileage").value(vehicleDto1.mileage()))
                .andExpect(jsonPath("$.content[0].vehicleType").value(vehicleDto1.vehicleType()))
                .andExpect(jsonPath("$.content[1].id").value(vehicleDto2.id()))
                .andExpect(jsonPath("$.content[1].vehicleType").value(vehicleDto2.vehicleType()))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));

            verify(vehicleService).getAllVehicles(any());
        }

        @Test
        @DisplayName("Should return empty paginated list when no vehicles exist")
        void shouldReturnEmptyPaginatedListWhenNoVehiclesExist() throws Exception {
            PaginatedResponseDto<VehicleDto> emptyPage = new PaginatedResponseDto<>(
                List.of(),
                0,
                20,
                0,
                0,
                false,
                false
            );

            when(vehicleService.getAllVehicles(any())).thenReturn(emptyPage);

            mockMvc.perform(get("/api/vehicles")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

            verify(vehicleService).getAllVehicles(any());
        }

        @Test
        @DisplayName("Should handle custom pagination parameters")
        void shouldHandleCustomPaginationParameters() throws Exception {
            VehicleDto vehicleDto = createVehicleDto(1L, "CAR");

            PaginatedResponseDto<VehicleDto> page = new PaginatedResponseDto<>(
                List.of(vehicleDto),
                2,
                5,
                15,
                3,
                false,
                true
            );

            when(vehicleService.getAllVehicles(any())).thenReturn(page);

            mockMvc.perform(get("/api/vehicles")
                    .param("page", "2")
                    .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value(2))
                .andExpect(jsonPath("$.pageSize").value(5))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(true));

            verify(vehicleService).getAllVehicles(any());
        }
    }

    @Nested
    @DisplayName("deleteVehicle Tests")
    class DeleteVehicleTests {

        @Test
        @DisplayName("Should delete vehicle by id")
        void shouldDeleteVehicleById() throws Exception {
            doNothing().when(vehicleService).deleteVehicle(1L);

            mockMvc.perform(delete("/api/vehicles/1"))
                .andExpect(status().isNoContent());

            verify(vehicleService).deleteVehicle(1L);
        }

        @Test
        @DisplayName("Should return not found when vehicle does not exist")
        void shouldReturnNotFoundWhenVehicleDoesNotExist() throws Exception {
            doThrow(new ResourceNotFoundException("Vehicle", "1"))
                .when(vehicleService).deleteVehicle(1L);

            mockMvc.perform(delete("/api/vehicles/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resourceType").value("Vehicle"))
                .andExpect(jsonPath("$.resourceId").value("1"))
                .andExpect(jsonPath("$.message").value("Vehicle not found with id: 1"))
                .andExpect(jsonPath("$.resourceType").exists())
                .andExpect(jsonPath("$.resourceId").exists())
                .andExpect(jsonPath("$.message").exists());

            verify(vehicleService).deleteVehicle(1L);
        }

        @Test
        @DisplayName("Should delete vehicle with different id")
        void shouldDeleteVehicleWithDifferentId() throws Exception {
            doNothing().when(vehicleService).deleteVehicle(999L);

            mockMvc.perform(delete("/api/vehicles/999"))
                .andExpect(status().isNoContent());

            verify(vehicleService).deleteVehicle(999L);
        }
    }

    private VehicleDto createVehicleDto(Long id, String vehicleType) {
        return new VehicleDto(
            id,
            "VIN-" + vehicleType + "-" + id,
            "LP-" + vehicleType + "-" + id,
            10000 + id.intValue() * 1000,
            new PowertrainInformationDto(
                EngineType.DIESEL,
                200,
                80,
                2500,
                true
            ),
            vehicleType
        );
    }
}
