package ro.upb.acs.fleetman.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ro.upb.acs.fleetman.GlobalExceptionHandler;
import ro.upb.acs.fleetman.exception.ResourceNotFoundException;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@Import(GlobalExceptionHandler.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Nested
    @DisplayName("getReportForFleetManager Tests")
    class GetReportForFleetManagerTests {

        @Test
        @DisplayName("Should return report successfully")
        void shouldReturnReportSuccessfully() throws Exception {
            ReportDto reportDto = new ReportDto(
                    100.0,
                    1,
                    Map.of("VIN123", 1L),
                    Map.of("John Doe", 100.0)
            );

            when(reportService.generateReportForFleetManager(1L)).thenReturn(reportDto);

            mockMvc.perform(get("/api/reports/fleet-manager/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalDistance").value(100.0))
                    .andExpect(jsonPath("$.totalTrips").value(1))
                    .andExpect(jsonPath("$.tripsPerVehicle.VIN123").value(1))
                    .andExpect(jsonPath("$.distancePerDriver.['John Doe']").value(100.0));
        }

        @Test
        @DisplayName("Should handle ResourceNotFoundException")
        void shouldHandleResourceNotFoundException() throws Exception {
            when(reportService.generateReportForFleetManager(1L))
                    .thenThrow(new ResourceNotFoundException("FleetManager", "1"));

            mockMvc.perform(get("/api/reports/fleet-manager/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resourceType").value("FleetManager"))
                    .andExpect(jsonPath("$.resourceId").value("1"));
        }
    }
}
