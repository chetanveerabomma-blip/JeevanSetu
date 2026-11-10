package com.jeevansetu.maps;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maps")
public class MapController {

    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMapOverview() {
        return ResponseEntity.ok(ApiResponse.ok("Live geospatial telemetry layers retrieved", mapService.getComprehensiveMapTelemetry()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchLocations(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.ok("Geocoded location candidates", mapService.searchLocations(q)));
    }
}
