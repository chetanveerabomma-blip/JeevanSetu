package com.jeevansetu.service;

import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.VehicleRecord;
import com.jeevansetu.repository.VehicleRecordRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VehicleMaintenanceService {

    private final VehicleRecordRepository vehicleRepository;
    private final AuditLogService auditLogService;

    public VehicleMaintenanceService(VehicleRecordRepository vehicleRepository, AuditLogService auditLogService) {
        this.vehicleRepository = vehicleRepository;
        this.auditLogService = auditLogService;
    }

    public VehicleRecord registerVehicle(VehicleRecord vehicle) {
        if (vehicle.getVehicleNumber() != null && vehicleRepository.findByVehicleNumber(vehicle.getVehicleNumber()).isPresent()) {
            throw new IllegalArgumentException("Vehicle already registered with plate: " + vehicle.getVehicleNumber());
        }
        if (vehicle.getMaintenanceStatus() == null) {
            vehicle.setMaintenanceStatus("ACTIVE");
        }
        vehicle.setCreatedAt(Instant.now());
        vehicle.setUpdatedAt(Instant.now());

        VehicleRecord saved = vehicleRepository.save(vehicle);
        auditLogService.log("REGISTER_VEHICLE", saved.getId(), "VEHICLE", "Registered: " + saved.getVehicleNumber());
        return saved;
    }

    public List<VehicleRecord> getAllVehicles(String status, String category) {
        List<VehicleRecord> all = vehicleRepository.findAll(Sort.by(Sort.Direction.ASC, "vehicleNumber"));
        return all.stream()
                .filter(v -> status == null || status.isBlank() || v.getMaintenanceStatus().equalsIgnoreCase(status))
                .filter(v -> category == null || category.isBlank() || v.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public VehicleRecord getVehicleById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle record not found: " + id));
    }

    public VehicleRecord updateMaintenanceStatus(String id, String status, String condition, String notes, String actorName) {
        VehicleRecord v = getVehicleById(id);
        v.setMaintenanceStatus(status);
        if (condition != null && !condition.isBlank()) {
            v.setConditionReport(condition);
        }
        v.setUpdatedAt(Instant.now());
        VehicleRecord saved = vehicleRepository.save(v);

        auditLogService.log("UPDATE_VEHICLE_MAINTENANCE", saved.getId(), "VEHICLE",
                "Status: " + status + " (" + condition + ") by " + actorName + " | Notes: " + notes);

        return saved;
    }

    public VehicleRecord scheduleService(String id, LocalDate serviceDate, String notes) {
        VehicleRecord v = getVehicleById(id);
        v.setNextServiceDate(serviceDate);
        v.setUpdatedAt(Instant.now());
        return vehicleRepository.save(v);
    }

    public List<VehicleRecord> getExpiringDocuments(int daysThreshold) {
        return vehicleRepository.findAll().stream()
                .filter(v -> v.isDocumentExpiringSoon(daysThreshold))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getVehicleOverview() {
        long total = vehicleRepository.count();
        long active = vehicleRepository.countByMaintenanceStatus("ACTIVE");
        long maintenance = vehicleRepository.countByMaintenanceStatus("MAINTENANCE");
        long outOfService = vehicleRepository.countByMaintenanceStatus("OUT_OF_SERVICE");
        long expiringSoon = getExpiringDocuments(30).size();

        return Map.of(
                "totalVehicles", total,
                "activeVehicles", active,
                "inMaintenance", maintenance,
                "outOfService", outOfService,
                "expiringDocuments", expiringSoon
        );
    }
}
