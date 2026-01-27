package ro.upb.acs.fleetman.vehicle.truck;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.common.PaginationMapper;
import ro.upb.acs.fleetman.exception.FieldConflictException;

@Service
public class TruckService {

    private final TruckMapper truckMapper;
    private final PaginationMapper paginationMapper;
    private final TruckRepository truckRepository;

    private static final String VIN_UNIQUE_INDEX = "uc_vehicle_vin";
    private static final String LICENSE_PLATE_UNIQUE_INDEX = "uc_vehicle_license_plate";

    private static final String VIN_CONFLICT_ERROR_FIELD = "vin";
    private static final String VIN_CONFLICT_ERROR_MESSAGE = "A truck with the provided VIN already exists.";
    private static final String LICENSE_PLATE_CONFLICT_ERROR_FIELD = "licensePlate";
    private static final String LICENSE_PLATE_CONFLICT_ERROR_MESSAGE = "A truck with the provided license plate already exists.";

    public TruckService(TruckMapper truckMapper, PaginationMapper paginationMapper, TruckRepository truckRepository) {
        this.truckMapper = truckMapper;
        this.paginationMapper = paginationMapper;
        this.truckRepository = truckRepository;
    }

    public TruckDto createTruck(CreateTruckDto createTruckDto) {
        var truck = truckMapper.toEntity(createTruckDto);

        try {
            return truckMapper.toDto(truckRepository.save(truck));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(VIN_UNIQUE_INDEX)) {
                throw new FieldConflictException(VIN_CONFLICT_ERROR_FIELD, VIN_CONFLICT_ERROR_MESSAGE);
            }
            if (e.getMessage().contains(LICENSE_PLATE_UNIQUE_INDEX)) {
                throw new FieldConflictException(LICENSE_PLATE_CONFLICT_ERROR_FIELD, LICENSE_PLATE_CONFLICT_ERROR_MESSAGE);
            }
            throw new RuntimeException(e);
        }
    }

    public PaginatedResponseDto<TruckDto> getAllTrucks(Pageable pageable) {
        Page<TruckDto> page = truckRepository.findAll(pageable).map(truckMapper::toDto);
        return paginationMapper.toPaginatedResponse(page);
    }

    public void deleteTruck(Long id) {
        truckRepository.deleteById(id);
    }
}
