package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.VehicleRecord;
import com.jeevansetu.service.VehicleMaintenanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleMaintenanceController {

    private final VehicleMaintenanceService vehicleService;

    public VehicleMaintenanceController(VehicleMaintenanceService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.getVehicleOverview()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleRecord>>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.getAllVehicles(status, category)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleRecord>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.getVehicleById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleRecord>> registerVehicle(@RequestBody VehicleRecord vehicle) {
        VehicleRecord registered = vehicleService.registerVehicle(vehicle);
        return ResponseEntity.ok(ApiResponse.ok("Vehicle registered", registered));
    }

    @PutMapping("/{id}/maintenance-status")
    public ResponseEntity<ApiResponse<VehicleRecord>> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("maintenanceStatus", "ACTIVE");
        String condition = body.getOrDefault("conditionReport", "GOOD");
        String notes = body.getOrDefault("notes", "Status update");
        String actor = body.getOrDefault("actorName", "Fleet Manager");

        VehicleRecord updated = vehicleService.updateMaintenanceStatus(id, status, condition, notes, actor);
        return ResponseEntity.ok(ApiResponse.ok("Vehicle maintenance status updated", updated));
    }

    @PostMapping("/{id}/schedule-service")
    public ResponseEntity<ApiResponse<VehicleRecord>> scheduleService(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String dateStr = body.get("serviceDate");
        LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now().plusMonths(3);
        String notes = body.getOrDefault("notes", "Routine checkup");

        VehicleRecord updated = vehicleService.scheduleService(id, date, notes);
        return ResponseEntity.ok(ApiResponse.ok("Service scheduled", updated));
    }

    @GetMapping("/alerts/expiring-documents")
    public ResponseEntity<ApiResponse<List<VehicleRecord>>> getExpiringDocuments(
            @RequestParam(defaultValue = "30") int daysThreshold) {
        return ResponseEntity.ok(ApiResponse.ok(vehicleService.getExpiringDocuments(daysThreshold)));
    }
}
