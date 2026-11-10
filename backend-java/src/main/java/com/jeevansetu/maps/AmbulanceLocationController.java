package com.jeevansetu.maps;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maps/ambulances")
public class AmbulanceLocationController {

    private final AmbulanceTrackingService ambulanceService;

    public AmbulanceLocationController(AmbulanceTrackingService ambulanceService) {
        this.ambulanceService = ambulanceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AmbulanceLocation>>> getActiveAmbulances() {
        return ResponseEntity.ok(ApiResponse.ok("Live ambulance telemetry records", ambulanceService.getActiveAmbulances()));
    }

    /**
     * Receives GPS coordinates from registered ambulance device or pilot dashboard.
     */
    @PostMapping("/{id}/location")
    public ResponseEntity<ApiResponse<AmbulanceLocation>> updateAmbulanceLocation(
            @PathVariable String id,
            @RequestBody Map<String, Object> req) {
        double lat = ((Number) req.get("latitude")).doubleValue();
        double lng = ((Number) req.get("longitude")).doubleValue();
        double speed = req.containsKey("speedKmH") ? ((Number) req.get("speedKmH")).doubleValue() : 0.0;
        double heading = req.containsKey("heading") ? ((Number) req.get("heading")).doubleValue() : 0.0;
        String status = (String) req.getOrDefault("status", "AVAILABLE");

        AmbulanceLocation updated = ambulanceService.updateGpsLocation(id, lat, lng, speed, heading, status);
        return ResponseEntity.ok(ApiResponse.ok("Ambulance GPS telemetry ingested & broadcasted", updated));
    }
}
