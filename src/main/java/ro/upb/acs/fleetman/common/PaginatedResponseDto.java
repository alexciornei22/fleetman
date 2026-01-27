package ro.upb.acs.fleetman.common;

import java.io.Serializable;
import java.util.List;

public record PaginatedResponseDto<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) implements Serializable { }
