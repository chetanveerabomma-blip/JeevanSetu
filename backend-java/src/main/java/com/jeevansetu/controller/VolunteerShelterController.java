package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.Shelter;
import com.jeevansetu.model.Volunteer;
import com.jeevansetu.service.VolunteerShelterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/volunteers-shelters")
@Tag(name = "Volunteers & Shelters", description = "Endpoints for volunteer mobilization and disaster shelter management")
public class VolunteerShelterController {

    private final VolunteerShelterService service;

    public VolunteerShelterController(VolunteerShelterService service) {
        this.service = service;
    }

    @GetMapping("/volunteers")
    @Operation(summary = "List all registered volunteers")
    public ResponseEntity<ApiResponse<List<Volunteer>>> getVolunteers(
            @RequestParam(required = false) Boolean availableOnly) {
        if (Boolean.TRUE.equals(availableOnly)) {
            return ResponseEntity.ok(ApiResponse.ok(service.getAvailableVolunteers()));
        }
        return ResponseEntity.ok(ApiResponse.ok(service.getAllVolunteers()));
    }

    @PatchMapping("/volunteers/{id}/status")
    @Operation(summary = "Update volunteer availability status (AVAILABLE, ENGAGED, OFFLINE)")
    public ResponseEntity<ApiResponse<Volunteer>> updateVolunteerStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(ApiResponse.ok(service.updateVolunteerStatus(id, status)));
    }

    @GetMapping("/shelters")
    @Operation(summary = "List all disaster relief shelters")
    public ResponseEntity<ApiResponse<List<Shelter>>> getShelters() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAllShelters()));
    }

    @PatchMapping("/shelters/{id}/occupancy")
    @Operation(summary = "Update shelter occupancy numbers")
    public ResponseEntity<ApiResponse<Shelter>> updateShelterOccupancy(
            @PathVariable String id,
            @RequestBody Map<String, Integer> body,
            Authentication auth) {
        int occupied = body.getOrDefault("occupied", 0);
        String email = auth != null ? auth.getName() : "shelter@jeevansetu.gov.in";
        return ResponseEntity.ok(ApiResponse.ok(service.updateShelterOccupancy(id, occupied, email)));
    }
}
