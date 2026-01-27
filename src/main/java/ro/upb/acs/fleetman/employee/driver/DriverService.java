package ro.upb.acs.fleetman.employee.driver;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.common.PaginationMapper;
import ro.upb.acs.fleetman.exception.FieldConflictException;

@Service
public class DriverService {

    private final DriverMapper driverMapper;
    private final PaginationMapper paginationMapper;
    private final DriverRepository driverRepository;

    private static final String EMPLOYEE_CODE_UNIQUE_INDEX = "uc_employee_employee_code";
    private static final String EMAIL_UNIQUE_INDEX = "uc_employee_email";
    private static final String TACHOGRAPH_CARD_UNIQUE_INDEX = "uc_driver_tachograph_card_number";

    private static final String EMPLOYEE_CODE_CONFLICT_ERROR_FIELD = "employeeCode";
    private static final String EMPLOYEE_CODE_CONFLICT_ERROR_MESSAGE = "A driver with the provided employee code already exists.";
    private static final String EMAIL_CONFLICT_ERROR_FIELD = "email";
    private static final String EMAIL_CONFLICT_ERROR_MESSAGE = "A driver with the provided email already exists.";
    private static final String TACHOGRAPH_CARD_CONFLICT_ERROR_FIELD = "tachographCardNumber";
    private static final String TACHOGRAPH_CARD_CONFLICT_ERROR_MESSAGE = "A driver with the provided tachograph card number already exists.";

    public DriverService(DriverMapper driverMapper, PaginationMapper paginationMapper, DriverRepository driverRepository) {
        this.driverMapper = driverMapper;
        this.paginationMapper = paginationMapper;
        this.driverRepository = driverRepository;
    }

    public DriverDto createDriver(CreateDriverDto createDriverDto) {
        var driver = driverMapper.toEntity(createDriverDto);

        try {
            return driverMapper.toDto(driverRepository.save(driver));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(EMPLOYEE_CODE_UNIQUE_INDEX)) {
                throw new FieldConflictException(EMPLOYEE_CODE_CONFLICT_ERROR_FIELD, EMPLOYEE_CODE_CONFLICT_ERROR_MESSAGE);
            }
            if (e.getMessage().contains(EMAIL_UNIQUE_INDEX)) {
                throw new FieldConflictException(EMAIL_CONFLICT_ERROR_FIELD, EMAIL_CONFLICT_ERROR_MESSAGE);
            }
            if (e.getMessage().contains(TACHOGRAPH_CARD_UNIQUE_INDEX)) {
                throw new FieldConflictException(TACHOGRAPH_CARD_CONFLICT_ERROR_FIELD, TACHOGRAPH_CARD_CONFLICT_ERROR_MESSAGE);
            }
            throw new RuntimeException(e);
        }
    }

    public PaginatedResponseDto<DriverDto> getAllDrivers(Pageable pageable) {
        Page<DriverDto> page = driverRepository.findAll(pageable).map(driverMapper::toDto);
        return paginationMapper.toPaginatedResponse(page);
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }
}
