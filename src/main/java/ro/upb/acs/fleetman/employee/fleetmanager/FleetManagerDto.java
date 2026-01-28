package ro.upb.acs.fleetman.employee.fleetmanager;

import java.io.Serializable;

public record FleetManagerDto(
    Long id,
    String employeeCode,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    Integer vehicleCount
) implements Serializable { }
