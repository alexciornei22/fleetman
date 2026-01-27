package ro.upb.acs.fleetman.employee.driver;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public record CreateDriverDto(
    @NotNull String employeeCode,
    @NotNull String firstName,
    @NotNull String lastName,
    @Email @NotNull String email,
    String phoneNumber,
    List<License> licenses,
    Date medicalCertificateExpiryDate,
    String tachographCardNumber
) implements Serializable { }
