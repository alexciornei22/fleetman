package ro.upb.acs.fleetman.exception;

import java.io.Serializable;

public record SingleArgumentNotValidDto(
    String errorField,
    String errorMessage
) implements Serializable {}
