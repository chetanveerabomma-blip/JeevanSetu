package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.dto.UpdateHospitalCapacityRequest;
import com.jeevansetu.model.Hospital;
import com.jeevansetu.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@Tag(name = "Hospitals", description = "Endpoints for hospital capacity management and nearby hospital discovery")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping
    @Operation(summary = "List all registered hospitals")
    public ResponseEntity<ApiResponse<List<Hospital>>> getAllHospitals() {
        return ResponseEntity.ok(ApiResponse.ok(hospitalService.getAllHospitals()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hospital details by ID")
    public ResponseEntity<ApiResponse<Hospital>> getHospitalById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(hospitalService.getHospitalById(id)));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find closest hospitals by coordinates")
    public ResponseEntity<ApiResponse<List<Hospital>>> getNearbyHospitals(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(hospitalService.getNearbyHospitals(lat, lng, limit)));
    }

    @PatchMapping("/{id}/capacity")
    @Operation(summary = "Update hospital ICU beds, emergency beds, and ventilator availability")
    public ResponseEntity<ApiResponse<Hospital>> updateCapacity(
            @PathVariable String id,
            @RequestBody UpdateHospitalCapacityRequest request,
            Authentication auth) {
        String email = auth != null ? auth.getName() : "hospital@jeevansetu.gov.in";
        Hospital updated = hospitalService.updateCapacity(id, request, email);
        return ResponseEntity.ok(ApiResponse.ok("Hospital capacity updated successfully", updated));
    }
}
