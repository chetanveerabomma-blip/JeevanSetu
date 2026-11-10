package com.jeevansetu.maps;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maps/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<RouteService.RouteResponse>> calculateRoute(@RequestBody Map<String, Object> req) {
        String entityId = (String) req.getOrDefault("entityId", "route-" + System.currentTimeMillis());
        String routeType = (String) req.getOrDefault("routeType", "EMERGENCY_DISPATCH");
        double startLat = ((Number) req.get("startLatitude")).doubleValue();
        double startLng = ((Number) req.get("startLongitude")).doubleValue();
        double endLat = ((Number) req.get("endLatitude")).doubleValue();
        double endLng = ((Number) req.get("endLongitude")).doubleValue();
        boolean alternative = (Boolean) req.getOrDefault("alternative", false);

        RouteService.RouteResponse route = routeService.calculateAndRecordRoute(
                entityId, routeType, startLat, startLng, endLat, endLng, alternative
        );
        return ResponseEntity.ok(ApiResponse.ok("Optimal routing corridor resolved", route));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<RouteHistory>>> getHistory() {
        return ResponseEntity.ok(ApiResponse.ok("Recent routing calculations", routeService.getRecentRoutes()));
    }
}
