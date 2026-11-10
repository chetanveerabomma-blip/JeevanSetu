package com.jeevansetu.evacuation;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evacuation")
public class EvacuationController {

    private final EvacuationService evacuationService;
    private final SafeZoneService safeZoneService;
    private final RouteCalculationService routeCalculationService;

    public EvacuationController(EvacuationService evacuationService,
                                SafeZoneService safeZoneService,
                                RouteCalculationService routeCalculationService) {
        this.evacuationService = evacuationService;
        this.safeZoneService = safeZoneService;
        this.routeCalculationService = routeCalculationService;
    }

    @GetMapping("/safe-zones")
    public ResponseEntity<ApiResponse<List<SafeZone>>> getSafeZones() {
        return ResponseEntity.ok(ApiResponse.ok("Active safe zones retrieved", safeZoneService.getAllActiveSafeZones()));
    }

    @PostMapping("/safe-zones")
    public ResponseEntity<ApiResponse<SafeZone>> registerSafeZone(@RequestBody SafeZone zone) {
        SafeZone saved = safeZoneService.registerSafeZone(zone);
        return ResponseEntity.ok(ApiResponse.ok("Safe zone registered successfully", saved));
    }

    @GetMapping("/blocked-roads")
    public ResponseEntity<ApiResponse<List<BlockedRoad>>> getBlockedRoads() {
        return ResponseEntity.ok(ApiResponse.ok("Active road blockages retrieved", evacuationService.getActiveBlockedRoads()));
    }

    @PostMapping("/blocked-roads")
    public ResponseEntity<ApiResponse<BlockedRoad>> blockRoad(@RequestBody BlockedRoad road) {
        BlockedRoad saved = evacuationService.reportBlockedRoad(road);
        return ResponseEntity.ok(ApiResponse.ok("Road restriction recorded", saved));
    }

    @DeleteMapping("/blocked-roads/{id}")
    public ResponseEntity<ApiResponse<Void>> unblockRoad(@PathVariable String id) {
        evacuationService.removeBlockedRoad(id);
        return ResponseEntity.ok(ApiResponse.message("Road blockage cleared"));
    }

    @PostMapping("/plan")
    public ResponseEntity<ApiResponse<EvacuationPlan>> generatePlan(@RequestBody Map<String, Object> req) {
        String area = (String) req.getOrDefault("affectedAreaName", "Civic Danger Zone");
        double lat = ((Number) req.getOrDefault("originLatitude", 12.9716)).doubleValue();
        double lng = ((Number) req.getOrDefault("originLongitude", 77.5946)).doubleValue();
        double radius = ((Number) req.getOrDefault("affectedRadiusMeters", 1000.0)).doubleValue();

        EvacuationPlan plan = evacuationService.generateEvacuationPlan(area, lat, lng, radius);
        return ResponseEntity.ok(ApiResponse.ok("Evacuation plan generated", plan));
    }

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<EvacuationPlan>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.ok("Active evacuation plans", evacuationService.getActivePlans()));
    }

    @PostMapping("/calculate-route")
    public ResponseEntity<ApiResponse<RouteCalculationService.CalculatedRoute>> calculateDirectRoute(@RequestBody Map<String, Object> req) {
        double startLat = ((Number) req.get("startLatitude")).doubleValue();
        double startLng = ((Number) req.get("startLongitude")).doubleValue();
        double endLat = ((Number) req.get("endLatitude")).doubleValue();
        double endLng = ((Number) req.get("endLongitude")).doubleValue();
        boolean alt = (Boolean) req.getOrDefault("alternative", false);

        RouteCalculationService.CalculatedRoute route = routeCalculationService.calculateRoute(startLat, startLng, endLat, endLng, alt);
        return ResponseEntity.ok(ApiResponse.ok("Route calculated", route));
    }
}
