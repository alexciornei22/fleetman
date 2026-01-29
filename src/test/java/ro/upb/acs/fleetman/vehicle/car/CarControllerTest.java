package ro.upb.acs.fleetman.vehicle.car;

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

@DisplayName("CarController Unit Tests")
@WebMvcTest(CarController.class)
@Import(TestObjectMapperConfig.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarService carService;

    @Nested
    @DisplayName("createCar Tests")
    class CreateCarTests {

        @Test
        @DisplayName("Should create car successfully")
        void shouldCreateCarSuccessfully() throws Exception {
            CarDto carDto = carDto();
            CreateCarDto createCarDto = createCarDto();

            when(carService.createCar(any(CreateCarDto.class))).thenReturn(carDto);

            mockMvc.perform(post("/api/cars")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createCarDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(carDto.id()))
                .andExpect(jsonPath("$.vin").value(carDto.vin()))
                .andExpect(jsonPath("$.licensePlate").value(carDto.licensePlate()))
                .andExpect(jsonPath("$.mileage").value(carDto.mileage()))
                .andExpect(jsonPath("$.numberOfSeats").value(carDto.numberOfSeats()))
                .andExpect(jsonPath("$.numberOfDoors").value(carDto.numberOfDoors()))
                .andExpect(jsonPath("$.isChildSeatCompatible").value(carDto.isChildSeatCompatible()))
                .andExpect(jsonPath("$.hasSunroof").value(carDto.hasSunroof()))
                .andExpect(jsonPath("$.carBodyType").value(carDto.carBodyType().toString()))
                .andExpect(jsonPath("$.fleetManagerId").value(carDto.fleetManagerId()))
                .andExpect(jsonPath("$.powertrainInformation.engineType").value(carDto.powertrainInformation().engineType().toString()))
                .andExpect(jsonPath("$.powertrainInformation.horsepower").value(carDto.powertrainInformation().horsepower()))
                .andExpect(jsonPath("$.powertrainInformation.fuelCapacityLiters").value(carDto.powertrainInformation().fuelCapacityLiters()))
                .andExpect(jsonPath("$.powertrainInformation.engineDisplacementCc").value(carDto.powertrainInformation().engineDisplacementCc()))
                .andExpect(jsonPath("$.powertrainInformation.hasAutomaticTransmission").value(carDto.powertrainInformation().hasAutomaticTransmission()));

            verify(carService).createCar(any(CreateCarDto.class));
        }

        @Test
        @DisplayName("Should return conflict when VIN already exists")
        void shouldReturnConflictWhenVinAlreadyExists() throws Exception {
            CreateCarDto createCarDto = createCarDto();

            when(carService.createCar(any(CreateCarDto.class)))
                .thenThrow(new FieldConflictException("vin", "A car with the provided VIN already exists."));

            mockMvc.perform(post("/api/cars")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createCarDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorField").value("vin"))
                .andExpect(jsonPath("$.errorMessage").value("A car with the provided VIN already exists."))
                .andExpect(jsonPath("$.errorField").exists())
                .andExpect(jsonPath("$.errorMessage").exists());

            verify(carService).createCar(any(CreateCarDto.class));
        }

        @Test
        @DisplayName("Should return unprocessable entity when fleet manager is missing")
        void shouldReturnUnprocessableEntityWhenFleetManagerIsMissing() throws Exception {
            CreateCarDto createCarDto = createCarDto();

            when(carService.createCar(any(CreateCarDto.class)))
                .thenThrow(new InvalidResourceReferenceException("FleetManager", "99"));

            mockMvc.perform(post("/api/cars")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createCarDto)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resourceType").value("FleetManager"))
                .andExpect(jsonPath("$.resourceId").value("99"))
                .andExpect(jsonPath("$.message").value("FleetManager not found with id: 99"))
                .andExpect(jsonPath("$.resourceType").exists())
                .andExpect(jsonPath("$.resourceId").exists())
                .andExpect(jsonPath("$.message").exists());

            verify(carService).createCar(any(CreateCarDto.class));
        }

        @Test
        @DisplayName("Should return bad request when validation fails")
        void shouldReturnBadRequestWhenValidationFails() throws Exception {
            CreateCarDto invalidDto = new CreateCarDto(
                null,
                "B-99-XYZ",
                -1,
                new PowertrainInformationDto(
                    EngineType.PETROL,
                    -10,
                    -1,
                    -1,
                    true
                ),
                1,
                1,
                true,
                true,
                CarBodyType.SEDAN,
                10L
            );

            mockMvc.perform(post("/api/cars")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[*].errorField").exists())
                .andExpect(jsonPath("$.errors[*].errorMessage").exists());

            verify(carService, never()).createCar(any(CreateCarDto.class));
        }
    }

    @Nested
    @DisplayName("getAllCars Tests")
    class GetAllCarsTests {

        @Test
        @DisplayName("Should return paginated list of cars")
        void shouldReturnPaginatedListOfCars() throws Exception {
            CarDto carDto = carDto();
            PaginatedResponseDto<CarDto> page = new PaginatedResponseDto<>(
                List.of(carDto),
                0,
                20,
                1,
                1,
                false,
                false
            );

            when(carService.getAllCars(any())).thenReturn(page);

            mockMvc.perform(get("/api/cars")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(carDto.id()))
                .andExpect(jsonPath("$.content[0].vin").value(carDto.vin()))
                .andExpect(jsonPath("$.content[0].licensePlate").value(carDto.licensePlate()))
                .andExpect(jsonPath("$.content[0].mileage").value(carDto.mileage()))
                .andExpect(jsonPath("$.content[0].numberOfSeats").value(carDto.numberOfSeats()))
                .andExpect(jsonPath("$.content[0].numberOfDoors").value(carDto.numberOfDoors()))
                .andExpect(jsonPath("$.content[0].carBodyType").value(carDto.carBodyType().toString()))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));

            verify(carService).getAllCars(any());
        }
    }

    @Nested
    @DisplayName("deleteCar Tests")
    class DeleteCarTests {

        @Test
        @DisplayName("Should delete car by id")
        void shouldDeleteCarById() throws Exception {
            doNothing().when(carService).deleteCar(1L);

            mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isNoContent());

            verify(carService).deleteCar(1L);
        }
    }

    @Nested
    @DisplayName("updateCar Tests")
    class UpdateCarTests {

        @Test
        @DisplayName("Should update car successfully")
        void shouldUpdateCarSuccessfully() throws Exception {
            CarDto carDto = carDto();
            UpdateCarDto updateCarDto = updateCarDto();

            when(carService.updateCar(eq(1L), any(UpdateCarDto.class))).thenReturn(carDto);

            mockMvc.perform(put("/api/cars/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateCarDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(carDto.id()))
                .andExpect(jsonPath("$.vin").value(carDto.vin()))
                .andExpect(jsonPath("$.licensePlate").value(carDto.licensePlate()))
                .andExpect(jsonPath("$.mileage").value(carDto.mileage()))
                .andExpect(jsonPath("$.numberOfSeats").value(carDto.numberOfSeats()))
                .andExpect(jsonPath("$.numberOfDoors").value(carDto.numberOfDoors()))
                .andExpect(jsonPath("$.isChildSeatCompatible").value(carDto.isChildSeatCompatible()))
                .andExpect(jsonPath("$.hasSunroof").value(carDto.hasSunroof()))
                .andExpect(jsonPath("$.carBodyType").value(carDto.carBodyType().toString()))
                .andExpect(jsonPath("$.fleetManagerId").value(carDto.fleetManagerId()))
                .andExpect(jsonPath("$.powertrainInformation.engineType").value(carDto.powertrainInformation().engineType().toString()))
                .andExpect(jsonPath("$.powertrainInformation.horsepower").value(carDto.powertrainInformation().horsepower()))
                .andExpect(jsonPath("$.powertrainInformation.fuelCapacityLiters").value(carDto.powertrainInformation().fuelCapacityLiters()))
                .andExpect(jsonPath("$.powertrainInformation.engineDisplacementCc").value(carDto.powertrainInformation().engineDisplacementCc()))
                .andExpect(jsonPath("$.powertrainInformation.hasAutomaticTransmission").value(carDto.powertrainInformation().hasAutomaticTransmission()));

            verify(carService).updateCar(eq(1L), any(UpdateCarDto.class));
        }

        @Test
        @DisplayName("Should return not found when car to update does not exist")
        void shouldReturnNotFoundWhenCarToUpdateDoesNotExist() throws Exception {
            UpdateCarDto updateCarDto = updateCarDto();

            when(carService.updateCar(eq(1L), any(UpdateCarDto.class)))
                .thenThrow(new ResourceNotFoundException("Car", "1"));

            mockMvc.perform(put("/api/cars/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateCarDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resourceType").value("Car"))
                .andExpect(jsonPath("$.resourceId").value(1L))
                .andExpect(jsonPath("$.message").value("Car not found with id: 1"))
                .andExpect(jsonPath("$.resourceType").exists())
                .andExpect(jsonPath("$.resourceId").exists())
                .andExpect(jsonPath("$.message").exists());

            verify(carService).updateCar(eq(1L), any(UpdateCarDto.class));
        }

        @Test
        @DisplayName("Should return conflict when VIN already exists during update")
        void shouldReturnConflictWhenVinAlreadyExistsDuringUpdate() throws Exception {
            UpdateCarDto updateCarDto = updateCarDto();

            when(carService.updateCar(eq(1L), any(UpdateCarDto.class)))
                .thenThrow(new FieldConflictException("vin", "A car with the provided VIN already exists."));

            mockMvc.perform(put("/api/cars/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateCarDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorField").value("vin"))
                .andExpect(jsonPath("$.errorMessage").value("A car with the provided VIN already exists."))
                .andExpect(jsonPath("$.errorField").exists())
                .andExpect(jsonPath("$.errorMessage").exists());

            verify(carService).updateCar(eq(1L), any(UpdateCarDto.class));
        }

        @Test
        @DisplayName("Should return bad request when validation fails during update")
        void shouldReturnBadRequestWhenValidationFailsDuringUpdate() throws Exception {
            UpdateCarDto invalidDto = new UpdateCarDto(
                null,
                "B-99-XYZ",
                -1,
                new PowertrainInformationDto(
                    EngineType.PETROL,
                    -10,
                    -1,
                    -1,
                    true
                ),
                1,
                1,
                true,
                true,
                CarBodyType.SEDAN
            );

            mockMvc.perform(put("/api/cars/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[*].errorField").exists())
                .andExpect(jsonPath("$.errors[*].errorMessage").exists());

            verify(carService, never()).updateCar(any(Long.class), any(UpdateCarDto.class));
        }
    }

    private CreateCarDto createCarDto() {
        return new CreateCarDto(
            "VIN-123",
            "B-99-XYZ",
            1200,
            new PowertrainInformationDto(
                EngineType.PETROL,
                120,
                45,
                1600,
                true
            ),
            5,
            4,
            true,
            false,
            CarBodyType.SEDAN,
            10L
        );
    }

    private CarDto carDto() {
        return new CarDto(
            1L,
            "VIN-123",
            "B-99-XYZ",
            1200,
            new PowertrainInformationDto(
                EngineType.PETROL,
                120,
                45,
                1600,
                true
            ),
            5,
            4,
            true,
            false,
            CarBodyType.SEDAN,
            10L
        );
    }

    private UpdateCarDto updateCarDto() {
        return new UpdateCarDto(
            "VIN-123",
            "B-99-XYZ",
            1300,
            new PowertrainInformationDto(
                EngineType.DIESEL,
                150,
                50,
                1800,
                false
            ),
            5,
            4,
            true,
            true,
            CarBodyType.SUV
        );
    }
}
