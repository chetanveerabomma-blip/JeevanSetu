package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.AgencyMessage;
import com.jeevansetu.model.Role;
import com.jeevansetu.model.User;
import com.jeevansetu.repository.UserRepository;
import com.jeevansetu.service.AgencyCommsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/agency-comms")
@Tag(name = "Agency Comms", description = "Multi-Agency Secure Tactical Communications (Police, Fire, EMS, Hospitals, Dispatch)")
public class AgencyCommsController {

    private final AgencyCommsService commsService;
    private final UserRepository userRepository;

    public AgencyCommsController(AgencyCommsService commsService, UserRepository userRepository) {
        this.commsService = commsService;
        this.userRepository = userRepository;
    }

    @PostMapping("/incident/{incidentId}")
    @Operation(summary = "Post a tactical message into an incident's inter-agency channel")
    public ResponseEntity<ApiResponse<AgencyMessage>> postMessage(
            @PathVariable String incidentId,
            @RequestBody Map<String, String> payload,
            Authentication auth) {
        String email = auth != null ? auth.getName() : "dispatch@jeevansetu.gov.in";
        Optional<User> userOpt = userRepository.findByEmail(email);

        String senderName = userOpt.map(User::getFullName).orElse(payload.getOrDefault("senderName", "Operator"));
        Role senderRole = userOpt.map(User::getRole).orElse(Role.CONTROL_CENTER);
        String agencyType = payload.getOrDefault("agencyType", senderRole.name());
        String message = payload.get("message");
        String priority = payload.getOrDefault("priority", "ROUTINE");

        AgencyMessage created = commsService.postMessage(incidentId, message, priority, email, senderName, senderRole, agencyType);
        return ResponseEntity.ok(ApiResponse.ok("Message broadcast to incident channel", created));
    }

    @GetMapping("/incident/{incidentId}")
    @Operation(summary = "Retrieve tactical communications log for an incident")
    public ResponseEntity<ApiResponse<List<AgencyMessage>>> getIncidentMessages(@PathVariable String incidentId) {
        List<AgencyMessage> messages = commsService.getMessagesForIncident(incidentId);
        return ResponseEntity.ok(ApiResponse.ok("Incident communications log", messages));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent multi-agency communications stream")
    public ResponseEntity<ApiResponse<List<AgencyMessage>>> getRecentMessages(@RequestParam(defaultValue = "50") int limit) {
        List<AgencyMessage> messages = commsService.getAllRecentMessages(limit);
        return ResponseEntity.ok(ApiResponse.ok("Recent communications stream", messages));
    }
}
