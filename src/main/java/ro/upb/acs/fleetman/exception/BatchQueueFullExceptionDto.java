package ro.upb.acs.fleetman.exception;

import java.io.Serializable;

public record BatchQueueFullExceptionDto(
    int capacity,
    String message
) implements Serializable { }
