package com.jeevansetu.maps;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maps/hospitals")
public class HospitalLocationController {

    private final HospitalLocationService hospitalService;

    public HospitalLocationController(HospitalLocationService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HospitalLocation>>> getVerifiedHospitals() {
        return ResponseEntity.ok(ApiResponse.ok("Verified hospital geospatial records", hospitalService.getVerifiedHospitals()));
    }

    @GetMapping("/nearest")
    public ResponseEntity<ApiResponse<List<HospitalLocation>>> getNearestHospitals(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(ApiResponse.ok("Nearest verified hospitals", hospitalService.findNearestHospitals(lat, lng, limit)));
    }

    @PutMapping("/{id}/beds")
    public ResponseEntity<ApiResponse<HospitalLocation>> updateBeds(
            @PathVariable String id,
            @RequestBody Map<String, Integer> body) {
        int icu = body.getOrDefault("icuBedsAvailable", 5);
        int er = body.getOrDefault("emergencyBedsAvailable", 15);
        return ResponseEntity.ok(ApiResponse.ok("Bed telemetry updated", hospitalService.updateBedTelemetry(id, icu, er)));
    }
}
