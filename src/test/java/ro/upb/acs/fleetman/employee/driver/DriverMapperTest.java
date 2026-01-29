package ro.upb.acs.fleetman.employee.driver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DriverMapper Unit Tests")
class DriverMapperTest {

    private DriverMapper driverMapper;

    @BeforeEach
    void setUp() {
        driverMapper = new DriverMapper();
    }

    @Nested
    @DisplayName("toEntity Tests")
    class ToEntityTests {

        @ParameterizedTest(name = "{index} - {0}")
        @CsvSource({
            "DRV-001,John,Doe,john.doe@example.com,+40123456789,TACH-001",
            "DRV-002,Jane,Smith,jane.smith@example.com,+40987654321,TACH-002",
            "DRV-999,Mike,Johnson,mike.j@example.com,+40111222333,TACH-999",
            "DRV-ABC,Alice,Brown,alice.brown@example.com,+40444555666,TACH-ABC",
            "DRV-XYZ,Bob,Wilson,bob.wilson@example.com,+40777888999,TACH-XYZ"
        })
        @DisplayName("Should map CreateDriverDto to Driver entity")
        void shouldMapCreateDriverDtoToDriverEntity(String employeeCode, String firstName, String lastName,
                                                    String email, String phoneNumber, String tachographCard) {
            License license = new License();
            license.setLicenseType(LicenseType.B);
            license.setIssueDate(Date.valueOf("2020-01-15"));
            license.setExpiryDate(Date.valueOf("2030-01-15"));

            CreateDriverDto createDriverDto = new CreateDriverDto(
                employeeCode,
                firstName,
                lastName,
                email,
                phoneNumber,
                List.of(license),
                Date.valueOf("2027-12-31"),
                tachographCard
            );

            Driver result = driverMapper.toEntity(createDriverDto);

            assertNotNull(result);
            assertEquals(employeeCode, result.getEmployeeCode());
            assertEquals(firstName, result.getFirstName());
            assertEquals(lastName, result.getLastName());
            assertEquals(email, result.getEmail());
            assertEquals(phoneNumber, result.getPhoneNumber());
            assertEquals(tachographCard, result.getTachographCardNumber());
            assertEquals(Date.valueOf("2027-12-31"), result.getMedicalCertificateExpiryDate());
            assertNotNull(result.getLicenses());
            assertEquals(1, result.getLicenses().size());
            assertEquals(LicenseType.B, result.getLicenses().get(0).getLicenseType());
            assertNull(result.getId());
        }
    }

    @Nested
    @DisplayName("toDto Tests")
    class ToDtoTests {

        @ParameterizedTest(name = "{index} - {0}")
        @CsvSource({
            "1,DRV-001,John,Doe,john.doe@example.com,+40123456789,TACH-001",
            "2,DRV-002,Jane,Smith,jane.smith@example.com,+40987654321,TACH-002",
            "50,DRV-999,Mike,Johnson,mike.j@example.com,+40111222333,TACH-999",
            "100,DRV-ABC,Alice,Brown,alice.brown@example.com,+40444555666,TACH-ABC",
            "9999,DRV-XYZ,Bob,Wilson,bob.wilson@example.com,+40777888999,TACH-XYZ"
        })
        @DisplayName("Should map Driver entity to DriverDto")
        void shouldMapDriverEntityToDriverDto(Long id, String employeeCode, String firstName, String lastName,
                                             String email, String phoneNumber, String tachographCard) {
            License license = new License();
            license.setLicenseType(LicenseType.C);
            license.setIssueDate(Date.valueOf("2019-05-20"));
            license.setExpiryDate(Date.valueOf("2029-05-20"));

            Driver driver = new Driver();
            driver.setId(id);
            driver.setEmployeeCode(employeeCode);
            driver.setFirstName(firstName);
            driver.setLastName(lastName);
            driver.setEmail(email);
            driver.setPhoneNumber(phoneNumber);
            driver.setLicenses(List.of(license));
            driver.setMedicalCertificateExpiryDate(Date.valueOf("2028-06-30"));
            driver.setTachographCardNumber(tachographCard);

            DriverDto result = driverMapper.toDto(driver);

            assertNotNull(result);
            assertEquals(id, result.id());
            assertEquals(employeeCode, result.employeeCode());
            assertEquals(firstName, result.firstName());
            assertEquals(lastName, result.lastName());
            assertEquals(email, result.email());
            assertEquals(phoneNumber, result.phoneNumber());
            assertEquals(tachographCard, result.tachographCardNumber());
            assertEquals(Date.valueOf("2028-06-30"), result.medicalCertificateExpiryDate());
            assertNotNull(result.licenses());
            assertEquals(1, result.licenses().size());
            assertEquals(LicenseType.C, result.licenses().get(0).getLicenseType());
        }
    }

    @Nested
    @DisplayName("Bidirectional Mapping Tests")
    class BidirectionalMappingTests {

        @ParameterizedTest(name = "{index} - Employee: {0}")
        @CsvSource({
            "DRV-001,John,Doe,john.doe@example.com",
            "DRV-002,Jane,Smith,jane.smith@example.com",
            "DRV-999,Mike,Johnson,mike.j@example.com",
            "DRV-ABC,Alice,Brown,alice.brown@example.com",
            "DRV-XYZ,Bob,Wilson,bob.wilson@example.com"
        })
        @DisplayName("Should correctly map CreateDriverDto to Driver and back to DriverDto bidirectionally")
        void shouldMapBidirectionally(String employeeCode, String firstName, String lastName, String email) {
            License license = new License();
            license.setLicenseType(LicenseType.D);
            license.setIssueDate(Date.valueOf("2021-03-10"));
            license.setExpiryDate(Date.valueOf("2031-03-10"));

            CreateDriverDto createDriverDto = new CreateDriverDto(
                employeeCode,
                firstName,
                lastName,
                email,
                "+40123456789",
                List.of(license),
                Date.valueOf("2026-11-30"),
                "TACH-" + employeeCode
            );

            Driver mappedDriver = driverMapper.toEntity(createDriverDto);
            DriverDto result = driverMapper.toDto(mappedDriver);

            assertNull(result.id());
            assertEquals(createDriverDto.employeeCode(), result.employeeCode());
            assertEquals(createDriverDto.firstName(), result.firstName());
            assertEquals(createDriverDto.lastName(), result.lastName());
            assertEquals(createDriverDto.email(), result.email());
            assertEquals(createDriverDto.phoneNumber(), result.phoneNumber());
            assertEquals(createDriverDto.tachographCardNumber(), result.tachographCardNumber());
            assertEquals(createDriverDto.medicalCertificateExpiryDate(), result.medicalCertificateExpiryDate());
            assertEquals(createDriverDto.licenses().size(), result.licenses().size());
            assertEquals(createDriverDto.licenses().get(0).getLicenseType(), result.licenses().get(0).getLicenseType());
        }
    }
}
