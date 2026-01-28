package ro.upb.acs.fleetman.employee.fleetmanager;

import org.springframework.stereotype.Component;

@Component
public class FleetManagerMapper {

    public FleetManager toEntity(CreateFleetManagerDto request) {
        FleetManager fleetManager = new FleetManager();
        fleetManager.setEmployeeCode(request.employeeCode());
        fleetManager.setFirstName(request.firstName());
        fleetManager.setLastName(request.lastName());
        fleetManager.setEmail(request.email());
        fleetManager.setPhoneNumber(request.phoneNumber());
        return fleetManager;
    }

    public FleetManagerDto toDto(FleetManager fleetManager) {
        return new FleetManagerDto(
            fleetManager.getId(),
            fleetManager.getEmployeeCode(),
            fleetManager.getFirstName(),
            fleetManager.getLastName(),
            fleetManager.getEmail(),
            fleetManager.getPhoneNumber(),
            fleetManager.getVehicleCount()
        );
    }
}
