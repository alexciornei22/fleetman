package ro.upb.acs.fleetman.employee.fleetmanager;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.common.PaginationMapper;
import ro.upb.acs.fleetman.exception.FieldConflictException;

@Service
public class FleetManagerService {

    private final FleetManagerMapper fleetManagerMapper;
    private final PaginationMapper paginationMapper;
    private final FleetManagerRepository fleetManagerRepository;

    private static final String EMPLOYEE_CODE_UNIQUE_INDEX = "uc_employee_employee_code";
    private static final String EMAIL_UNIQUE_INDEX = "uc_employee_email";

    private static final String EMPLOYEE_CODE_CONFLICT_ERROR_FIELD = "employeeCode";
    private static final String EMPLOYEE_CODE_CONFLICT_ERROR_MESSAGE = "A fleet manager with the provided employee code already exists.";
    private static final String EMAIL_CONFLICT_ERROR_FIELD = "email";
    private static final String EMAIL_CONFLICT_ERROR_MESSAGE = "A fleet manager with the provided email already exists.";

    public FleetManagerService(FleetManagerMapper fleetManagerMapper, PaginationMapper paginationMapper, FleetManagerRepository fleetManagerRepository) {
        this.fleetManagerMapper = fleetManagerMapper;
        this.paginationMapper = paginationMapper;
        this.fleetManagerRepository = fleetManagerRepository;
    }

    public FleetManagerDto createFleetManager(CreateFleetManagerDto createFleetManagerDto) {
        var fleetManager = fleetManagerMapper.toEntity(createFleetManagerDto);

        try {
            return fleetManagerMapper.toDto(fleetManagerRepository.save(fleetManager));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(EMPLOYEE_CODE_UNIQUE_INDEX)) {
                throw new FieldConflictException(EMPLOYEE_CODE_CONFLICT_ERROR_FIELD, EMPLOYEE_CODE_CONFLICT_ERROR_MESSAGE);
            }
            if (e.getMessage().contains(EMAIL_UNIQUE_INDEX)) {
                throw new FieldConflictException(EMAIL_CONFLICT_ERROR_FIELD, EMAIL_CONFLICT_ERROR_MESSAGE);
            }
            throw new RuntimeException(e);
        }
    }

    public PaginatedResponseDto<FleetManagerDto> getAllFleetManagers(Pageable pageable) {
        Page<FleetManagerDto> page = fleetManagerRepository.findAll(pageable).map(fleetManagerMapper::toDto);
        return paginationMapper.toPaginatedResponse(page);
    }

    public void deleteFleetManager(Long id) {
        fleetManagerRepository.deleteById(id);
    }
}
