package ro.upb.acs.fleetman.common;

import org.springframework.data.domain.Page;

public class PaginationMapper {

    public static <T> PaginatedResponseDto<T> toPaginatedResponse(Page<T> page) {
        return new PaginatedResponseDto<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
}
