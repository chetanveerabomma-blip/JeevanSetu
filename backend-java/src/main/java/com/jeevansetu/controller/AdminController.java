package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.*;
import com.jeevansetu.repository.*;
import com.jeevansetu.service.AuditLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final EmergencyRepository emergencyRepository;
    private final ResponderUnitRepository responderRepository;
    private final HospitalRepository hospitalRepository;
    private final VolunteerRepository volunteerRepository;
    private final DisasterEventRepository disasterRepository;
    private final InventoryItemRepository inventoryRepository;
    private final VehicleRecordRepository vehicleRepository;
    private final BloodDonorRepository bloodDonorRepository;
    private final AuditLogService auditLogService;

    @Value("${jeevansetu.escalation.critical-threshold-sec:180}")
    private long criticalThresholdSec;

    @Value("${jeevansetu.escalation.high-threshold-sec:600}")
    private long highThresholdSec;

    @Value("${jeevansetu.ai.service-url:http://localhost:8000}")
    private String aiServiceUrl;

    public AdminController(UserRepository userRepository,
                           EmergencyRepository emergencyRepository,
                           ResponderUnitRepository responderRepository,
                           HospitalRepository hospitalRepository,
                           VolunteerRepository volunteerRepository,
                           DisasterEventRepository disasterRepository,
                           InventoryItemRepository inventoryRepository,
                           VehicleRecordRepository vehicleRepository,
                           BloodDonorRepository bloodDonorRepository,
                           AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.emergencyRepository = emergencyRepository;
        this.responderRepository = responderRepository;
        this.hospitalRepository = hospitalRepository;
        this.volunteerRepository = volunteerRepository;
        this.disasterRepository = disasterRepository;
        this.inventoryRepository = inventoryRepository;
        this.vehicleRepository = vehicleRepository;
        this.bloodDonorRepository = bloodDonorRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminOverview() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalUsers", userRepository.count());
        map.put("totalEmergencies", emergencyRepository.count());
        map.put("activeEmergencies", emergencyRepository.countByStatus(EmergencyStatus.REPORTED) + emergencyRepository.countByStatus(EmergencyStatus.PROCESSING));
        map.put("criticalEmergencies", emergencyRepository.countByPriority(EmergencyPriority.CRITICAL));
        map.put("resolvedEmergencies", emergencyRepository.countByStatus(EmergencyStatus.RESOLVED));
        map.put("totalResponders", responderRepository.count());
        map.put("availableResponders", responderRepository.countByStatus(ResourceStatus.AVAILABLE));
        map.put("totalHospitals", hospitalRepository.count());
        map.put("totalVolunteers", volunteerRepository.count());
        map.put("activeDisasters", disasterRepository.countByStatus("ACTIVE"));
        map.put("totalInventoryItems", inventoryRepository.count());
        map.put("totalVehicles", vehicleRepository.count());
        map.put("activeVehicles", vehicleRepository.countByMaintenanceStatus("ACTIVE"));
        map.put("totalBloodDonors", bloodDonorRepository.count());
        map.put("systemStatus", "ALL_SYSTEMS_OPERATIONAL");
        map.put("serverTime", Instant.now());

        return ResponseEntity.ok(ApiResponse.ok(map));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(userRepository.findAll()));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<User>> updateUserRole(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String roleStr = body.get("role");
        Role role = Role.valueOf(roleStr.toUpperCase());
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        u.setRole(role);
        User saved = userRepository.save(u);

        auditLogService.log("UPDATE_USER_ROLE", saved.getId(), "USER", "Role changed to " + role);
        return ResponseEntity.ok(ApiResponse.ok("User role updated", saved));
    }

    @GetMapping("/system-config")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemConfig() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "escalationCriticalThresholdSec", criticalThresholdSec,
                "escalationHighThresholdSec", highThresholdSec,
                "aiServiceUrl", aiServiceUrl,
                "authoritativeBackend", "Java 21 / Spring Boot 3.3.4",
                "database", "MongoDB 2dsphere",
                "realtimeBroker", "Spring WebSocket / STOMP"
        )));
    }
}
