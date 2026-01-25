package ro.upb.acs.fleetman.exception;

import java.io.Serializable;
import java.util.List;

public record MultipleArgumentsNotValidDto(
    List<SingleArgumentNotValidDto> errors
) implements Serializable { }
