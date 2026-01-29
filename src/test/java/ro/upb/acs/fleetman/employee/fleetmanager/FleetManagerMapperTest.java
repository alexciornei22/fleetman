package ro.upb.acs.fleetman.employee.fleetmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import ro.upb.acs.fleetman.vehicle.base.Vehicle;
import ro.upb.acs.fleetman.vehicle.car.Car;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FleetManagerMapper Unit Tests")
class FleetManagerMapperTest {

    private FleetManagerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FleetManagerMapper();
    }

    @Nested
    @DisplayName("toEntity Tests")
    class ToEntityTests {

        @ParameterizedTest(name = "{index} - {0}")
        @CsvSource({
            "FM-001,Fleet,Manager,fleet.manager@example.com,+40123456700",
            "FM-002,Ops,Lead,ops.lead@example.com,+40223344556",
            "FM-999,Admin,Boss,admin.boss@example.com,+40777999000"
        })
        void shouldMapCreateDtoToEntity(String employeeCode, String firstName, String lastName,
                                        String email, String phone) {
            CreateFleetManagerDto dto = new CreateFleetManagerDto(
                employeeCode,
                firstName,
                lastName,
                email,
                phone
            );

            FleetManager entity = mapper.toEntity(dto);

            entity.setManagedVehicles(vehiclesOfSize(1));

            assertNotNull(entity);
            assertEquals(employeeCode, entity.getEmployeeCode());
            assertEquals(firstName, entity.getFirstName());
            assertEquals(lastName, entity.getLastName());
            assertEquals(email, entity.getEmail());
            assertEquals(phone, entity.getPhoneNumber());
        }
    }

    @Nested
    @DisplayName("toDto Tests")
    class ToDtoTests {

        @ParameterizedTest(name = "{index} - {0}")
        @CsvSource({
            "1,FM-001,Fleet,Manager,fleet.manager@example.com,+40123456700,5",
            "2,FM-002,Ops,Lead,ops.lead@example.com,+40223344556,10",
            "3,FM-999,Admin,Boss,admin.boss@example.com,+40777999000,0"
        })
        void shouldMapEntityToDto(Long id, String employeeCode, String firstName, String lastName,
                                  String email, String phone, Integer vehicleCount) {
            FleetManager entity = new FleetManager();
            entity.setId(id);
            entity.setEmployeeCode(employeeCode);
            entity.setFirstName(firstName);
            entity.setLastName(lastName);
            entity.setEmail(email);
            entity.setPhoneNumber(phone);
            entity.setManagedVehicles(vehiclesOfSize(vehicleCount));

            FleetManagerDto dto = mapper.toDto(entity);

            assertNotNull(dto);
            assertEquals(id, dto.id());
            assertEquals(employeeCode, dto.employeeCode());
            assertEquals(firstName, dto.firstName());
            assertEquals(lastName, dto.lastName());
            assertEquals(email, dto.email());
            assertEquals(phone, dto.phoneNumber());
            assertEquals(vehicleCount, dto.vehicleCount());
        }
    }

    private List<Vehicle> vehiclesOfSize(int count) {
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            vehicles.add(new Car());
        }
        return vehicles;
    }
}
