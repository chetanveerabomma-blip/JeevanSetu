package com.jeevansetu.maps;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/maps/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LocationRecord>> recordLocation(@RequestBody Map<String, Object> req) {
        String entityId = (String) req.getOrDefault("entityId", UUID.randomUUID().toString());
        String entityType = (String) req.getOrDefault("entityType", "CITIZEN");
        double lat = ((Number) req.get("latitude")).doubleValue();
        double lng = ((Number) req.get("longitude")).doubleValue();
        String address = (String) req.getOrDefault("address", "GPS Coordinates");

        LocationRecord rec = locationService.recordLocation(entityId, entityType, lat, lng, address);
        return ResponseEntity.ok(ApiResponse.ok("Location logged", rec));
    }

    @GetMapping("/type/{entityType}")
    public ResponseEntity<ApiResponse<List<LocationRecord>>> getByType(@PathVariable String entityType) {
        return ResponseEntity.ok(ApiResponse.ok("Locations retrieved", locationService.getLocationsByType(entityType)));
    }

    @GetMapping("/entity/{entityId}")
    public ResponseEntity<ApiResponse<List<LocationRecord>>> getEntityHistory(@PathVariable String entityId) {
        return ResponseEntity.ok(ApiResponse.ok("Entity trajectory history", locationService.getEntityHistory(entityId)));
    }
}
