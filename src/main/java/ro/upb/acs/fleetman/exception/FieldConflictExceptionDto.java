package ro.upb.acs.fleetman.exception;

import java.io.Serializable;

public record FieldConflictExceptionDto(
    String errorField,
    String errorMessage
) implements Serializable { }
