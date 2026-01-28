package ro.upb.acs.fleetman.employee.fleetmanager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FleetManagerRepository extends JpaRepository<FleetManager, Long> { }
