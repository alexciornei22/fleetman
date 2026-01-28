package ro.upb.acs.fleetman.employee.fleetmanager;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record CreateFleetManagerDto(
    @NotNull String employeeCode,
    @NotNull String firstName,
    @NotNull String lastName,
    @NotNull String email,
    String phoneNumber
) implements Serializable { }
