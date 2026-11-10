package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.EmergencyPriority;
import com.jeevansetu.model.EmergencyStatus;
import com.jeevansetu.model.EmergencyType;
import com.jeevansetu.model.IncidentTimelineEntry;
import com.jeevansetu.service.IncidentAssignmentService;
import com.jeevansetu.service.IncidentCommandService;
import com.jeevansetu.service.IncidentTimelineService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/command")
@CrossOrigin(origins = "*")
public class IncidentCommandController {

    private final IncidentCommandService commandService;
    private final IncidentAssignmentService assignmentService;
    private final IncidentTimelineService timelineService;

    public IncidentCommandController(IncidentCommandService commandService,
                                     IncidentAssignmentService assignmentService,
                                     IncidentTimelineService timelineService) {
        this.commandService = commandService;
        this.assignmentService = assignmentService;
        this.timelineService = timelineService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok(commandService.getCommandOverview()));
    }

    @GetMapping("/incidents")
    public ResponseEntity<ApiResponse<List<Emergency>>> getFilteredIncidents(
            @RequestParam(required = false) EmergencyStatus status,
            @RequestParam(required = false) EmergencyPriority priority,
            @RequestParam(required = false) EmergencyType type,
            @RequestParam(required = false) String agency,
            @RequestParam(required = false) Boolean escalated,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        List<Emergency> incidents = commandService.getFilteredIncidents(status, priority, type, agency, escalated, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(incidents));
    }

    @GetMapping("/incident/{id}/timeline")
    public ResponseEntity<ApiResponse<List<IncidentTimelineEntry>>> getTimeline(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(timelineService.getTimeline(id)));
    }

    @PostMapping("/incident/{id}/assign-agency")
    public ResponseEntity<ApiResponse<Emergency>> assignAgency(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String agency = body.get("agency");
        String dispatcherName = body.getOrDefault("dispatcherName", "Dispatcher");
        String remarks = body.get("remarks");

        Emergency updated = assignmentService.assignAgency(id, agency, dispatcherName, remarks);
        return ResponseEntity.ok(ApiResponse.ok("Incident assigned to agency " + agency, updated));
    }

    @PostMapping("/incident/{id}/remark")
    public ResponseEntity<ApiResponse<Emergency>> addRemark(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String dispatcherName = body.getOrDefault("dispatcherName", "Dispatcher");
        String remarks = body.get("remarks");

        Emergency updated = assignmentService.addDispatcherRemark(id, dispatcherName, remarks);
        return ResponseEntity.ok(ApiResponse.ok("Remark recorded", updated));
    }

    @PostMapping("/incident/{id}/escalate")
    public ResponseEntity<ApiResponse<Emergency>> escalate(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "Manual escalation by dispatcher");
        String dispatcherName = body.getOrDefault("dispatcherName", "Dispatcher");

        Emergency updated = commandService.escalateIncident(id, reason, dispatcherName);
        return ResponseEntity.ok(ApiResponse.ok("Incident escalated", updated));
    }

    @PostMapping("/incident/{id}/acknowledge-escalation")
    public ResponseEntity<ApiResponse<Emergency>> acknowledge(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String dispatcherName = body.getOrDefault("dispatcherName", "Dispatcher");

        Emergency updated = commandService.acknowledgeEscalation(id, dispatcherName);
        return ResponseEntity.ok(ApiResponse.ok("Escalation acknowledged", updated));
    }

    @PostMapping("/incident/{id}/resolve")
    public ResponseEntity<ApiResponse<Emergency>> resolve(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String notes = body.getOrDefault("notes", "Resolved on scene");
        String dispatcherName = body.getOrDefault("dispatcherName", "Dispatcher");

        Emergency updated = commandService.resolveIncident(id, notes, dispatcherName);
        return ResponseEntity.ok(ApiResponse.ok("Incident resolved successfully", updated));
    }
}
