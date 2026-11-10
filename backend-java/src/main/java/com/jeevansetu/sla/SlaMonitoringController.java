package com.jeevansetu.sla;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sla")
public class SlaMonitoringController {

    private final SlaMonitoringService slaService;

    public SlaMonitoringController(SlaMonitoringService slaService) {
        this.slaService = slaService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<SlaMonitoringService.SlaMetricsReport>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok("Live SLA metrics calculated from actual database timestamps", slaService.computeSlaReport()));
    }

    @GetMapping("/configs")
    public ResponseEntity<ApiResponse<List<SlaConfig>>> getConfigs() {
        return ResponseEntity.ok(ApiResponse.ok("SLA target configurations", slaService.getAllConfigs()));
    }

    @PostMapping("/configs")
    public ResponseEntity<ApiResponse<SlaConfig>> updateConfig(@RequestBody SlaConfig config) {
        return ResponseEntity.ok(ApiResponse.ok("SLA target updated", slaService.updateConfig(config)));
    }
}
