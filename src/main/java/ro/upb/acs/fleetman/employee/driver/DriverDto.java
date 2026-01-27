package ro.upb.acs.fleetman.employee.driver;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public record DriverDto(
    Long id,
    String employeeCode,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    List<License> licenses,
    Date medicalCertificateExpiryDate,
    String tachographCardNumber
) implements Serializable { }
