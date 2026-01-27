package ro.upb.acs.fleetman.vehicle.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.upb.acs.fleetman.common.PaginatedResponseDto;
import ro.upb.acs.fleetman.common.PaginationMapper;

@Service
public class VehicleService {

    private final VehicleMapper vehicleMapper;
    private final PaginationMapper paginationMapper;
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleMapper vehicleMapper, PaginationMapper paginationMapper, VehicleRepository vehicleRepository) {
        this.vehicleMapper = vehicleMapper;
        this.paginationMapper = paginationMapper;
        this.vehicleRepository = vehicleRepository;
    }

    public PaginatedResponseDto<VehicleDto> getAllVehicles(Pageable pageable) {
        Page<VehicleDto> page = vehicleRepository.findAll(pageable).map(vehicleMapper::toDto);
        return paginationMapper.toPaginatedResponse(page);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
}
