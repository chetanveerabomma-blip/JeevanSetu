package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.BloodDonor;
import com.jeevansetu.model.BloodRequest;
import com.jeevansetu.service.BloodDonorCoordinationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blood-coordination")
@CrossOrigin(origins = "*")
public class BloodDonorCoordinationController {

    private final BloodDonorCoordinationService coordinationService;

    public BloodDonorCoordinationController(BloodDonorCoordinationService coordinationService) {
        this.coordinationService = coordinationService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok(coordinationService.getBloodCoordinationOverview()));
    }

    @PostMapping("/donors/register")
    public ResponseEntity<ApiResponse<BloodDonor>> registerDonor(@RequestBody BloodDonor donor) {
        BloodDonor registered = coordinationService.registerDonor(donor);
        return ResponseEntity.ok(ApiResponse.ok("Donor registered successfully (Privacy Protected)", registered));
    }

    @GetMapping("/donors/search")
    public ResponseEntity<ApiResponse<List<BloodDonor>>> findMatchingDonors(
            @RequestParam String bloodGroup,
            @RequestParam(defaultValue = "0") double latitude,
            @RequestParam(defaultValue = "0") double longitude,
            @RequestParam(defaultValue = "25") double radiusKm) {
        return ResponseEntity.ok(ApiResponse.ok(coordinationService.findMatchingDonors(bloodGroup, latitude, longitude, radiusKm)));
    }

    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<BloodRequest>> createRequest(@RequestBody BloodRequest request) {
        BloodRequest created = coordinationService.createBloodRequest(request);
        return ResponseEntity.ok(ApiResponse.ok("Blood requirement broadcasted", created));
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<BloodRequest>>> getRequests(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(coordinationService.getAllRequests(status)));
    }

    @PostMapping("/requests/{id}/fulfill")
    public ResponseEntity<ApiResponse<BloodRequest>> fulfillRequest(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        int units = Integer.parseInt(body.getOrDefault("unitsProvided", 1).toString());
        String coordinator = (String) body.getOrDefault("coordinatorName", "Hospital Coordinator");

        BloodRequest fulfilled = coordinationService.fulfillRequest(id, units, coordinator);
        return ResponseEntity.ok(ApiResponse.ok("Request fulfillment updated", fulfilled));
    }
}
