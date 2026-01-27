package ro.upb.acs.fleetman.exception;

import java.io.Serializable;

public record ResourceNotFoundExceptionDto(
    String resourceType,
    String resourceId,
    String message
) implements Serializable { }
