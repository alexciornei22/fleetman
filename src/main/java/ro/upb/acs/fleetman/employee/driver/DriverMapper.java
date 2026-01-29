package ro.upb.acs.fleetman.employee.driver;

import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public Driver toEntity(CreateDriverDto request) {
        Driver driver = new Driver();
        driver.setEmployeeCode(request.employeeCode());
        driver.setFirstName(request.firstName());
        driver.setLastName(request.lastName());
        driver.setEmail(request.email());
        driver.setPhoneNumber(request.phoneNumber());
        driver.setLicenses(request.licenses());
        driver.setMedicalCertificateExpiryDate(request.medicalCertificateExpiryDate());
        driver.setTachographCardNumber(request.tachographCardNumber());
        return driver;
    }

    public void updateEntity(UpdateDriverDto request, Driver existing) {
        existing.setEmployeeCode(request.employeeCode());
        existing.setFirstName(request.firstName());
        existing.setLastName(request.lastName());
        existing.setEmail(request.email());
        existing.setPhoneNumber(request.phoneNumber());
        existing.setLicenses(request.licenses());
        existing.setMedicalCertificateExpiryDate(request.medicalCertificateExpiryDate());
        existing.setTachographCardNumber(request.tachographCardNumber());
    }

    public DriverDto toDto(Driver driver) {
        return new DriverDto(
            driver.getId(),
            driver.getEmployeeCode(),
            driver.getFirstName(),
            driver.getLastName(),
            driver.getEmail(),
            driver.getPhoneNumber(),
            driver.getLicenses(),
            driver.getMedicalCertificateExpiryDate(),
            driver.getTachographCardNumber()
        );
    }
}
