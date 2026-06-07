package ro.upb.acs.fleetman.report;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.upb.acs.fleetman.config.LogResponse;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/fleet-manager/{fleetManagerId}")
    @LogResponse
    public ReportDto getReportForFleetManager(@PathVariable Long fleetManagerId) {
        return reportService.generateReportForFleetManager(fleetManagerId);
    }
}
