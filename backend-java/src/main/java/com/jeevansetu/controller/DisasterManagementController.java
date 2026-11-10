package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.DisasterEvent;
import com.jeevansetu.model.ReliefCenter;
import com.jeevansetu.model.ReliefRequest;
import com.jeevansetu.service.DisasterManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/disaster")
@CrossOrigin(origins = "*")
public class DisasterManagementController {

    private final DisasterManagementService disasterService;

    public DisasterManagementController(DisasterManagementService disasterService) {
        this.disasterService = disasterService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok(disasterService.getDisasterOverview()));
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<DisasterEvent>> createEvent(
            @RequestBody DisasterEvent event,
            @RequestParam(defaultValue = "Admin") String actorName) {
        DisasterEvent created = disasterService.createDisasterEvent(event, actorName);
        return ResponseEntity.ok(ApiResponse.ok("Disaster event registered", created));
    }

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<DisasterEvent>>> getAllEvents(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(disasterService.getAllEvents(status)));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<ApiResponse<DisasterEvent>> getEventById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(disasterService.getEventById(id)));
    }

    @PutMapping("/events/{id}/status")
    public ResponseEntity<ApiResponse<DisasterEvent>> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "ACTIVE");
        String actorName = body.getOrDefault("actorName", "Dispatcher");
        DisasterEvent updated = disasterService.updateEventStatus(id, status, actorName);
        return ResponseEntity.ok(ApiResponse.ok("Status updated", updated));
    }

    // Relief Centers
    @PostMapping("/centers")
    public ResponseEntity<ApiResponse<ReliefCenter>> registerCenter(
            @RequestBody ReliefCenter center,
            @RequestParam(defaultValue = "Relief Manager") String actorName) {
        ReliefCenter registered = disasterService.registerReliefCenter(center, actorName);
        return ResponseEntity.ok(ApiResponse.ok("Relief center registered", registered));
    }

    @GetMapping("/centers")
    public ResponseEntity<ApiResponse<List<ReliefCenter>>> getCenters(
            @RequestParam(required = false) String disasterEventId) {
        return ResponseEntity.ok(ApiResponse.ok(disasterService.getReliefCenters(disasterEventId)));
    }

    @PutMapping("/centers/{id}/occupancy")
    public ResponseEntity<ApiResponse<ReliefCenter>> updateOccupancy(
            @PathVariable String id,
            @RequestBody Map<String, Integer> body) {
        int occupied = body.getOrDefault("currentOccupancy", 0);
        int foodDays = body.getOrDefault("foodSupplyDays", 7);
        int waterLiters = body.getOrDefault("waterSupplyLiters", 5000);
        int medicalKits = body.getOrDefault("medicalKitsAvailable", 50);

        ReliefCenter updated = disasterService.updateCenterOccupancy(id, occupied, foodDays, waterLiters, medicalKits);
        return ResponseEntity.ok(ApiResponse.ok("Relief center capacity & supplies updated", updated));
    }

    // Relief Requests
    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<ReliefRequest>> submitRequest(@RequestBody ReliefRequest request) {
        ReliefRequest submitted = disasterService.submitReliefRequest(request);
        return ResponseEntity.ok(ApiResponse.ok("Relief request submitted", submitted));
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<ReliefRequest>>> getRequests(
            @RequestParam(required = false) String disasterEventId) {
        return ResponseEntity.ok(ApiResponse.ok(disasterService.getReliefRequests(disasterEventId)));
    }

    @PostMapping("/requests/{id}/dispatch")
    public ResponseEntity<ApiResponse<ReliefRequest>> dispatchSupply(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        int quantity = Integer.parseInt(body.getOrDefault("quantityDispatched", 0).toString());
        String notes = (String) body.getOrDefault("notes", "Dispatched to relief point");
        String coordinator = (String) body.getOrDefault("coordinatorName", "Relief Coordinator");

        ReliefRequest dispatched = disasterService.coordinateSupplyDistribution(id, quantity, notes, coordinator);
        return ResponseEntity.ok(ApiResponse.ok("Supplies coordinated and dispatched", dispatched));
    }
}
