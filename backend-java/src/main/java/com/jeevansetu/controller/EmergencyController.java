package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.dto.EmergencyReportRequest;
import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.EmergencyStatus;
import com.jeevansetu.model.IncidentUpdate;
import com.jeevansetu.model.User;
import com.jeevansetu.repository.UserRepository;
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

    public EmergencyController(EmergencyService emergencyService, UserRepository userRepository) {
        this.emergencyService = emergencyService;
        this.userRepository = userRepository;
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
}
