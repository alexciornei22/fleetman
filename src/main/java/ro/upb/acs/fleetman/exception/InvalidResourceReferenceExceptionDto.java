package ro.upb.acs.fleetman.exception;

import java.io.Serializable;

public record InvalidResourceReferenceExceptionDto(
    String resourceType,
    String resourceId,
    String message
) implements Serializable { }
