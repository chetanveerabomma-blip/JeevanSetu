package com.jeevansetu.controller;

import com.jeevansetu.algorithm.RouteResult;
import com.jeevansetu.algorithm.ScoredResource;
import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.dto.AssignResourceRequest;
import com.jeevansetu.dto.UpdateResourceStatusRequest;
import com.jeevansetu.model.Assignment;
import com.jeevansetu.model.ResourceStatus;
import com.jeevansetu.model.ResourceType;
import com.jeevansetu.model.ResponderUnit;
import com.jeevansetu.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
@Tag(name = "Resources & Dispatch", description = "Endpoints for resource ranking, Dijkstra routing, and responder assignment")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/responders")
    @Operation(summary = "List all responder units with optional type and status filtering")
    public ResponseEntity<ApiResponse<List<ResponderUnit>>> getResponders(
            @RequestParam(required = false) ResourceType type,
            @RequestParam(required = false) ResourceStatus status) {
        if (type != null) {
            return ResponseEntity.ok(ApiResponse.ok(resourceService.getUnitsByType(type)));
        }
        if (status != null) {
            return ResponseEntity.ok(ApiResponse.ok(resourceService.getUnitsByStatus(status)));
        }
        return ResponseEntity.ok(ApiResponse.ok(resourceService.getAllResponderUnits()));
    }

    @GetMapping("/responders/{id}")
    @Operation(summary = "Get responder unit details by ID")
    public ResponseEntity<ApiResponse<ResponderUnit>> getResponderById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(resourceService.getUnitById(id)));
    }

    @GetMapping("/recommend/{emergencyId}")
    @Operation(summary = "Algorithmic scoring: rank suitable responder units using multi-factor suitability formula")
    public ResponseEntity<ApiResponse<List<ScoredResource>>> getRecommendedUnits(
            @PathVariable String emergencyId,
            @RequestParam(required = false) ResourceType type) {
        List<ScoredResource> recommendations = resourceService.getRecommendedUnits(emergencyId, type);
        return ResponseEntity.ok(ApiResponse.ok("Suitability scoring completed", recommendations));
    }

    @PostMapping("/assign/{emergencyId}")
    @Operation(summary = "Dispatch a responder unit to an emergency (executes Dijkstra route calculation)")
    public ResponseEntity<ApiResponse<Assignment>> assignResource(
            @PathVariable String emergencyId,
            @RequestBody AssignResourceRequest request,
            Authentication auth) {
        String email = auth != null ? auth.getName() : "dispatch@jeevansetu.gov.in";
        Assignment assignment = resourceService.assignUnitToEmergency(emergencyId, request.getResourceId(), email);
        return ResponseEntity.ok(ApiResponse.ok("Unit dispatched successfully via Dijkstra shortest corridor", assignment));
    }

    @PatchMapping("/assignments/{assignmentId}/status")
    @Operation(summary = "Update responder progress (ACCEPT, EN_ROUTE, ARRIVED, PATIENT_HANDLED, AVAILABLE)")
    public ResponseEntity<ApiResponse<Assignment>> updateAssignmentStatus(
            @PathVariable String assignmentId,
            @RequestBody UpdateResourceStatusRequest request,
            Authentication auth) {
        String email = auth != null ? auth.getName() : "responder@jeevansetu.gov.in";
        String role = auth != null && !auth.getAuthorities().isEmpty()
                ? auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "")
                : "AMBULANCE";

        Assignment updated = resourceService.updateAssignmentStatus(assignmentId, request.getStatus(), email, role);
        return ResponseEntity.ok(ApiResponse.ok("Status progressed to " + request.getStatus(), updated));
    }

    @PostMapping("/assignments/{assignmentId}/route-to-hospital")
    @Operation(summary = "Recalculate route and alert receiving hospital for trauma admission")
    public ResponseEntity<ApiResponse<RouteResult>> routeToHospital(
            @PathVariable String assignmentId,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String hospitalId = body.get("hospitalId");
        String email = auth != null ? auth.getName() : "ambulance@jeevansetu.gov.in";
        RouteResult route = resourceService.routeToHospital(assignmentId, hospitalId, email);
        return ResponseEntity.ok(ApiResponse.ok("Hospital corridor calculated and notification transmitted", route));
    }

    @GetMapping("/assignments/incident/{incidentId}")
    @Operation(summary = "Get all assignments for an incident")
    public ResponseEntity<ApiResponse<List<Assignment>>> getIncidentAssignments(@PathVariable String incidentId) {
        return ResponseEntity.ok(ApiResponse.ok(resourceService.getAssignmentsForIncident(incidentId)));
    }

    @GetMapping("/assignments/active/{unitId}")
    @Operation(summary = "Get the active dispatch assignment for a responder unit")
    public ResponseEntity<ApiResponse<Assignment>> getActiveAssignment(@PathVariable String unitId) {
        return resourceService.getActiveAssignmentForUnit(unitId)
                .map(a -> ResponseEntity.ok(ApiResponse.ok(a)))
                .orElse(ResponseEntity.ok(ApiResponse.ok("No active assignment", null)));
    }
}
