package com.jeevansetu.maps;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("mapsGeofenceController")
@RequestMapping("/api/maps/zones")
public class GeofenceController {

    private final GeofenceService geofenceService;

    public GeofenceController(GeofenceService geofenceService) {
        this.geofenceService = geofenceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmergencyZone>>> getZones() {
        return ResponseEntity.ok(ApiResponse.ok("Active geographical emergency zones", geofenceService.getActiveZones()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmergencyZone>> createZone(@RequestBody EmergencyZone zone) {
        return ResponseEntity.ok(ApiResponse.ok("Geographical emergency zone established", geofenceService.createZone(zone)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeZone(@PathVariable String id) {
        geofenceService.removeZone(id);
        return ResponseEntity.ok(ApiResponse.message("Emergency zone deactivated"));
    }

    @GetMapping("/intersect")
    public ResponseEntity<ApiResponse<List<EmergencyZone>>> checkIntersection(
            @RequestParam double lat,
            @RequestParam double lng) {
        return ResponseEntity.ok(ApiResponse.ok("Intersecting hazard zones", geofenceService.findZonesIntersectingPoint(lat, lng)));
    }
}
