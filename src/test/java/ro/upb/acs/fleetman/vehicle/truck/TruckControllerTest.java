package ro.upb.acs.fleetman.vehicle.truck;

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
import ro.upb.acs.fleetman.exception.InvalidResourceReferenceException;
import ro.upb.acs.fleetman.exception.ResourceNotFoundException;
import ro.upb.acs.fleetman.vehicle.base.EngineType;
import ro.upb.acs.fleetman.vehicle.base.PowertrainInformationDto;
import ro.upb.acs.fleetman.vehicle.config.TestObjectMapperConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("TruckController Unit Tests")
@WebMvcTest(TruckController.class)
@Import(TestObjectMapperConfig.class)
public class TruckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TruckService truckService;

    @Nested
    @DisplayName("createTruck Tests")
    class CreateTruckTests {

        @Test
        @DisplayName("Should create truck successfully")
        void shouldCreateTruckSuccessfully() throws Exception {
            TruckDto truckDto = truckDto();
            CreateTruckDto createTruckDto = createTruckDto();

            when(truckService.createTruck(any(CreateTruckDto.class))).thenReturn(truckDto);

            mockMvc.perform(post("/api/trucks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTruckDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(truckDto.id()))
                .andExpect(jsonPath("$.vin").value(truckDto.vin()))
                .andExpect(jsonPath("$.licensePlate").value(truckDto.licensePlate()))
                .andExpect(jsonPath("$.mileage").value(truckDto.mileage()))
                .andExpect(jsonPath("$.maxLoadKg").value(truckDto.maxLoadKg()))
                .andExpect(jsonPath("$.numberOfAxles").value(truckDto.numberOfAxles()))
                .andExpect(jsonPath("$.hasRefrigerationUnit").value(truckDto.hasRefrigerationUnit()))
                .andExpect(jsonPath("$.fleetManagerId").value(truckDto.fleetManagerId()))
                .andExpect(jsonPath("$.powertrainInformation.engineType").value(truckDto.powertrainInformation().engineType().toString()))
                .andExpect(jsonPath("$.powertrainInformation.horsepower").value(truckDto.powertrainInformation().horsepower()))
                .andExpect(jsonPath("$.powertrainInformation.fuelCapacityLiters").value(truckDto.powertrainInformation().fuelCapacityLiters()))
                .andExpect(jsonPath("$.powertrainInformation.engineDisplacementCc").value(truckDto.powertrainInformation().engineDisplacementCc()))
                .andExpect(jsonPath("$.powertrainInformation.hasAutomaticTransmission").value(truckDto.powertrainInformation().hasAutomaticTransmission()));

            verify(truckService).createTruck(any(CreateTruckDto.class));
        }

        @Test
        @DisplayName("Should return conflict when VIN already exists")
        void shouldReturnConflictWhenVinAlreadyExists() throws Exception {
            CreateTruckDto createTruckDto = createTruckDto();

            when(truckService.createTruck(any(CreateTruckDto.class)))
                .thenThrow(new FieldConflictException("vin", "A truck with the provided VIN already exists."));

            mockMvc.perform(post("/api/trucks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTruckDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorField").value("vin"))
                .andExpect(jsonPath("$.errorMessage").value("A truck with the provided VIN already exists."))
                .andExpect(jsonPath("$.errorField").exists())
                .andExpect(jsonPath("$.errorMessage").exists());

            verify(truckService).createTruck(any(CreateTruckDto.class));
        }

        @Test
        @DisplayName("Should return unprocessable entity when fleet manager is missing")
        void shouldReturnUnprocessableEntityWhenFleetManagerIsMissing() throws Exception {
            CreateTruckDto createTruckDto = createTruckDto();

            when(truckService.createTruck(any(CreateTruckDto.class)))
                .thenThrow(new InvalidResourceReferenceException("FleetManager", "99"));

            mockMvc.perform(post("/api/trucks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTruckDto)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resourceType").value("FleetManager"))
                .andExpect(jsonPath("$.resourceId").value("99"))
                .andExpect(jsonPath("$.message").value("FleetManager not found with id: 99"))
                .andExpect(jsonPath("$.resourceType").exists())
                .andExpect(jsonPath("$.resourceId").exists())
                .andExpect(jsonPath("$.message").exists());

            verify(truckService).createTruck(any(CreateTruckDto.class));
        }

        @Test
        @DisplayName("Should return bad request when validation fails")
        void shouldReturnBadRequestWhenValidationFails() throws Exception {
            CreateTruckDto invalidDto = new CreateTruckDto(
                null,
                "B-99-XYZ",
                1500,
                new PowertrainInformationDto(
                    EngineType.DIESEL,
                    220,
                    90,
                    2600,
                    true
                ),
                8000,
                3,
                true,
                10L
            );

            mockMvc.perform(post("/api/trucks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[*].errorField").exists())
                .andExpect(jsonPath("$.errors[*].errorMessage").exists());

            verify(truckService, never()).createTruck(any(CreateTruckDto.class));
        }
    }

    @Nested
    @DisplayName("getAllTrucks Tests")
    class GetAllTrucksTests {

        @Test
        @DisplayName("Should return paginated list of trucks")
        void shouldReturnPaginatedListOfTrucks() throws Exception {
            TruckDto truckDto = truckDto();
            PaginatedResponseDto<TruckDto> page = new PaginatedResponseDto<>(
                List.of(truckDto),
                0,
                20,
                1,
                1,
                false,
                false
            );

            when(truckService.getAllTrucks(any())).thenReturn(page);

            mockMvc.perform(get("/api/trucks")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(truckDto.id()))
                .andExpect(jsonPath("$.content[0].vin").value(truckDto.vin()))
                .andExpect(jsonPath("$.content[0].licensePlate").value(truckDto.licensePlate()))
                .andExpect(jsonPath("$.content[0].mileage").value(truckDto.mileage()))
                .andExpect(jsonPath("$.content[0].maxLoadKg").value(truckDto.maxLoadKg()))
                .andExpect(jsonPath("$.content[0].numberOfAxles").value(truckDto.numberOfAxles()))
                .andExpect(jsonPath("$.content[0].hasRefrigerationUnit").value(truckDto.hasRefrigerationUnit()))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));

            verify(truckService).getAllTrucks(any());
        }
    }

    @Nested
    @DisplayName("deleteTruck Tests")
    class DeleteTruckTests {

        @Test
        @DisplayName("Should delete truck by id")
        void shouldDeleteTruckById() throws Exception {
            doNothing().when(truckService).deleteTruck(1L);

            mockMvc.perform(delete("/api/trucks/1"))
                .andExpect(status().isNoContent());

            verify(truckService).deleteTruck(1L);
        }

        @Test
        @DisplayName("Should return not found when truck does not exist")
        void shouldReturnNotFoundWhenTruckDoesNotExist() throws Exception {
            doThrow(new ResourceNotFoundException("Truck", "1"))
                .when(truckService).deleteTruck(1L);

            mockMvc.perform(delete("/api/trucks/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resourceType").value("Truck"))
                .andExpect(jsonPath("$.resourceId").value("1"))
                .andExpect(jsonPath("$.message").value("Truck not found with id: 1"))
                .andExpect(jsonPath("$.resourceType").exists())
                .andExpect(jsonPath("$.resourceId").exists())
                .andExpect(jsonPath("$.message").exists());

            verify(truckService).deleteTruck(1L);
        }
    }

    @Nested
    @DisplayName("updateTruck Tests")
    class UpdateTruckTests {

        @Test
        @DisplayName("Should update truck successfully")
        void shouldUpdateTruckSuccessfully() throws Exception {
            TruckDto truckDto = truckDto();
            UpdateTruckDto updateTruckDto = updateTruckDto();

            when(truckService.updateTruck(eq(1L), any(UpdateTruckDto.class))).thenReturn(truckDto);

            mockMvc.perform(put("/api/trucks/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateTruckDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(truckDto.id()))
                .andExpect(jsonPath("$.vin").value(truckDto.vin()))
                .andExpect(jsonPath("$.licensePlate").value(truckDto.licensePlate()))
                .andExpect(jsonPath("$.mileage").value(truckDto.mileage()))
                .andExpect(jsonPath("$.maxLoadKg").value(truckDto.maxLoadKg()))
                .andExpect(jsonPath("$.numberOfAxles").value(truckDto.numberOfAxles()))
                .andExpect(jsonPath("$.hasRefrigerationUnit").value(truckDto.hasRefrigerationUnit()))
                .andExpect(jsonPath("$.fleetManagerId").value(truckDto.fleetManagerId()))
                .andExpect(jsonPath("$.powertrainInformation.engineType").value(truckDto.powertrainInformation().engineType().toString()))
                .andExpect(jsonPath("$.powertrainInformation.horsepower").value(truckDto.powertrainInformation().horsepower()))
                .andExpect(jsonPath("$.powertrainInformation.fuelCapacityLiters").value(truckDto.powertrainInformation().fuelCapacityLiters()))
                .andExpect(jsonPath("$.powertrainInformation.engineDisplacementCc").value(truckDto.powertrainInformation().engineDisplacementCc()))
                .andExpect(jsonPath("$.powertrainInformation.hasAutomaticTransmission").value(truckDto.powertrainInformation().hasAutomaticTransmission()));

            verify(truckService).updateTruck(eq(1L), any(UpdateTruckDto.class));
        }

        @Test
        @DisplayName("Should return not found when truck to update does not exist")
        void shouldReturnNotFoundWhenTruckToUpdateDoesNotExist() throws Exception {
            UpdateTruckDto updateTruckDto = updateTruckDto();

            when(truckService.updateTruck(eq(1L), any(UpdateTruckDto.class)))
                .thenThrow(new ResourceNotFoundException("Truck", "1"));

            mockMvc.perform(put("/api/trucks/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateTruckDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resourceType").value("Truck"))
                .andExpect(jsonPath("$.resourceId").value(1L))
                .andExpect(jsonPath("$.message").value("Truck not found with id: 1"));

            verify(truckService).updateTruck(eq(1L), any(UpdateTruckDto.class));
        }

        @Test
        @DisplayName("Should return conflict when VIN already exists during update")
        void shouldReturnConflictWhenVinAlreadyExistsDuringUpdate() throws Exception {
            UpdateTruckDto updateTruckDto = updateTruckDto();

            when(truckService.updateTruck(eq(1L), any(UpdateTruckDto.class)))
                .thenThrow(new FieldConflictException("vin", "A truck with the provided VIN already exists."));

            mockMvc.perform(put("/api/trucks/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateTruckDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorField").value("vin"))
                .andExpect(jsonPath("$.errorMessage").value("A truck with the provided VIN already exists."));

            verify(truckService).updateTruck(eq(1L), any(UpdateTruckDto.class));
        }

        @Test
        @DisplayName("Should return bad request when validation fails during update")
        void shouldReturnBadRequestWhenValidationFailsDuringUpdate() throws Exception {
            UpdateTruckDto invalidDto = new UpdateTruckDto(
                null,
                "B-88-TRK",
                2000,
                new PowertrainInformationDto(
                    EngineType.DIESEL,
                    180,
                    70,
                    2400,
                    true
                ),
                9000,
                3,
                true
            );

            mockMvc.perform(put("/api/trucks/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[*].errorField").exists())
                .andExpect(jsonPath("$.errors[*].errorMessage").exists());

            verify(truckService, never()).updateTruck(any(Long.class), any(UpdateTruckDto.class));
        }
    }

    private CreateTruckDto createTruckDto() {
        return new CreateTruckDto(
            "VIN-456",
            "B-88-TRK",
            5000,
            new PowertrainInformationDto(
                EngineType.DIESEL,
                250,
                100,
                3000,
                false
            ),
            10000,
            3,
            true,
            10L
        );
    }

    private TruckDto truckDto() {
        return new TruckDto(
            1L,
            "VIN-456",
            "B-88-TRK",
            5000,
            new PowertrainInformationDto(
                EngineType.DIESEL,
                250,
                100,
                3000,
                false
            ),
            10000,
            3,
            true,
            10L
        );
    }

    private UpdateTruckDto updateTruckDto() {
        return new UpdateTruckDto(
            "VIN-456",
            "B-88-TRK",
            6000,
            new PowertrainInformationDto(
                EngineType.DIESEL,
                260,
                110,
                3200,
                false
            ),
            12000,
            4,
            false
        );
    }
}
