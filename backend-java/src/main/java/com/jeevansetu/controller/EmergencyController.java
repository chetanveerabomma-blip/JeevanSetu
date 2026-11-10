package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.dto.DuplicateMatchResult;
import com.jeevansetu.dto.EmergencyReportRequest;
import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.EmergencyStatus;
import com.jeevansetu.model.EmergencyType;
import com.jeevansetu.model.IncidentUpdate;
import com.jeevansetu.model.User;
import com.jeevansetu.repository.UserRepository;
import com.jeevansetu.service.DuplicateDetectionService;
import com.jeevansetu.service.EmergencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/emergencies")
@Tag(name = "Emergencies", description = "Endpoints for emergency reporting, priority triage queue, and incident lifecycle")
public class EmergencyController {

    private final EmergencyService emergencyService;
    private final UserRepository userRepository;
    private final DuplicateDetectionService duplicateDetectionService;

    public EmergencyController(EmergencyService emergencyService, UserRepository userRepository, DuplicateDetectionService duplicateDetectionService) {
        this.emergencyService = emergencyService;
        this.userRepository = userRepository;
        this.duplicateDetectionService = duplicateDetectionService;
    }

    @PostMapping("/report")
    @Operation(summary = "Submit an emergency report (multi-step citizen form)")
    public ResponseEntity<ApiResponse<Emergency>> reportEmergency(
            @RequestBody EmergencyReportRequest request,
            Authentication auth) {
        String email = auth != null ? auth.getName() : "citizen@demo.jeevansetu.in";
        Optional<User> userOpt = userRepository.findByEmail(email);

        String userId = userOpt.map(User::getId).orElse(null);
        String userName = userOpt.map(User::getFullName).orElse("Citizen Reporter");
        String userPhone = userOpt.map(User::getPhone).orElse("N/A");

        Emergency reported = emergencyService.reportEmergency(request, userId, email, userName, userPhone);
        return ResponseEntity.ok(ApiResponse.ok("Emergency reported successfully. Incident ID: " + reported.getIncidentCode(), reported));
    }

    @PostMapping("/sos")
    @Operation(summary = "One-click Citizen Emergency SOS panic trigger with automatic GPS fix")
    public ResponseEntity<ApiResponse<Emergency>> triggerSos(
            @RequestBody Map<String, Object> payload,
            Authentication auth) {
        String email = auth != null ? auth.getName() : "sos-caller@jeevansetu.in";
        Optional<User> userOpt = userRepository.findByEmail(email);

        String userName = userOpt.map(User::getFullName).orElse((String) payload.getOrDefault("name", "SOS Caller"));
        String userPhone = userOpt.map(User::getPhone).orElse((String) payload.getOrDefault("phone", "N/A"));

        double lat = payload.containsKey("latitude") ? ((Number) payload.get("latitude")).doubleValue() : 17.3850;
        double lng = payload.containsKey("longitude") ? ((Number) payload.get("longitude")).doubleValue() : 78.4867;
        String address = (String) payload.getOrDefault("address", "SOS GPS Location Coordinates");

        Emergency sos = emergencyService.reportSosEmergency(lat, lng, address, email, userName, userPhone);
        return ResponseEntity.ok(ApiResponse.ok("🚨 SOS Alert Dispatched! Incident: " + sos.getIncidentCode(), sos));
    }

    @GetMapping("/priority-queue")
    @Operation(summary = "Get active emergencies ordered by Java PriorityQueue triage ranking")
    public ResponseEntity<ApiResponse<List<Emergency>>> getPriorityQueue() {
        List<Emergency> ordered = emergencyService.getActiveEmergenciesPriorityOrdered();
        return ResponseEntity.ok(ApiResponse.ok("Active triage queue retrieved", ordered));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all emergencies (historical and active)")
    public ResponseEntity<ApiResponse<List<Emergency>>> getAllEmergencies() {
        return ResponseEntity.ok(ApiResponse.ok(emergencyService.getAllEmergencies()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get emergency details by ID")
    public ResponseEntity<ApiResponse<Emergency>> getEmergencyById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(emergencyService.getEmergencyById(id)));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get emergency details by incident code (e.g. JS-2026-000184)")
    public ResponseEntity<ApiResponse<Emergency>> getEmergencyByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(emergencyService.getEmergencyByCode(code)));
    }

    @GetMapping("/my-reports")
    @Operation(summary = "Get emergency reports submitted by the logged-in citizen")
    public ResponseEntity<ApiResponse<List<Emergency>>> getMyReports(Authentication auth) {
        String email = auth != null ? auth.getName() : "";
        Optional<User> user = userRepository.findByEmail(email);
        List<Emergency> list = user.map(u -> emergencyService.getEmergenciesForCitizen(u.getId())).orElse(List.of());
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update emergency status (e.g. EN_ROUTE, ARRIVED, RESOLVED)")
    public ResponseEntity<ApiResponse<Emergency>> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String statusStr = body.get("status");
        String note = body.get("note");
        EmergencyStatus status = EmergencyStatus.valueOf(statusStr.toUpperCase());

        String email = auth != null ? auth.getName() : "CONTROL_CENTER";
        String role = auth != null && !auth.getAuthorities().isEmpty()
                ? auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "")
                : "CONTROL_CENTER";

        Emergency updated = emergencyService.updateStatus(id, status, email, role, note);
        return ResponseEntity.ok(ApiResponse.ok("Status updated to " + status, updated));
    }

    @GetMapping("/{id}/timeline")
    @Operation(summary = "Get complete chronological audit timeline of an incident")
    public ResponseEntity<ApiResponse<List<IncidentUpdate>>> getTimeline(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(emergencyService.getIncidentTimeline(id)));
    }

    @PostMapping("/check-duplicate")
    @Operation(summary = "Check for possible duplicate incidents using spatial, temporal, and NLP text similarity")
    public ResponseEntity<ApiResponse<List<DuplicateMatchResult>>> checkDuplicate(
            @RequestBody Map<String, Object> body) {
        double lat = Double.parseDouble(body.getOrDefault("latitude", 0).toString());
        double lng = Double.parseDouble(body.getOrDefault("longitude", 0).toString());
        String typeStr = (String) body.getOrDefault("type", "MEDICAL");
        String description = (String) body.getOrDefault("description", "");

        EmergencyType type;
        try {
            type = EmergencyType.valueOf(typeStr.toUpperCase());
        } catch (Exception ex) {
            type = EmergencyType.MEDICAL;
        }

        List<DuplicateMatchResult> matches = duplicateDetectionService.checkPotentialDuplicates(lat, lng, type, description, java.time.Instant.now());
        String message = matches.isEmpty() ? "No duplicate incidents detected." : "⚠️ Possible similar emergency reports found (" + matches.size() + " matches)";
        return ResponseEntity.ok(ApiResponse.ok(message, matches));
    }
}
