package ro.upb.acs.fleetman.common;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaginationMapper {

    public <T> PaginatedResponseDto<T> toPaginatedResponse(Page<T> page) {
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
