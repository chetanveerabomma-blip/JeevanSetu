package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.GeoZone;
import com.jeevansetu.service.GeoFenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/geofence")
@CrossOrigin(origins = "*")
public class GeoFenceController {

    private final GeoFenceService geoFenceService;

    public GeoFenceController(GeoFenceService geoFenceService) {
        this.geoFenceService = geoFenceService;
    }

    @PostMapping("/zones")
    public ResponseEntity<ApiResponse<GeoZone>> createZone(
            @RequestBody GeoZone zone,
            @RequestParam(defaultValue = "Command Dispatcher") String creatorName) {
        GeoZone created = geoFenceService.createZone(zone, creatorName);
        return ResponseEntity.ok(ApiResponse.ok("Geo-Zone activated and broadcasted", created));
    }

    @GetMapping("/zones")
    public ResponseEntity<ApiResponse<List<GeoZone>>> getZones(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(geoFenceService.getAllZones(status)));
    }

    @PutMapping("/zones/{id}/status")
    public ResponseEntity<ApiResponse<GeoZone>> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "ACTIVE");
        String actor = body.getOrDefault("actorName", "Dispatcher");
        GeoZone updated = geoFenceService.updateZoneStatus(id, status, actor);
        return ResponseEntity.ok(ApiResponse.ok("Zone status updated", updated));
    }

    @GetMapping("/zones/{id}/scan")
    public ResponseEntity<ApiResponse<Map<String, Object>>> scanZone(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(geoFenceService.scanResourcesInZone(id)));
    }

    @GetMapping("/check-point")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPoint(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        boolean inDanger = geoFenceService.isPointInAnyActiveDangerZone(latitude, longitude);
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "latitude", latitude,
                "longitude", longitude,
                "insideDangerZone", inDanger
        )));
    }
}
