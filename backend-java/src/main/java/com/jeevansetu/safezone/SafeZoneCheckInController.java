package com.jeevansetu.safezone;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/safezone")
public class SafeZoneCheckInController {

    private final SafeZoneCheckInService checkInService;

    public SafeZoneCheckInController(SafeZoneCheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<ShelterCheckIn>> checkIn(@RequestBody ShelterCheckIn checkIn) {
        return ResponseEntity.ok(ApiResponse.ok("Shelter check-in recorded successfully", checkInService.checkInPerson(checkIn)));
    }

    @PostMapping("/confirm-safe/{code}")
    public ResponseEntity<ApiResponse<ShelterCheckIn>> confirmSafe(@PathVariable String code) {
        return checkInService.confirmSafeStatus(code)
                .map(c -> ResponseEntity.ok(ApiResponse.ok("Safe status confirmed", c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/depart/{id}")
    public ResponseEntity<ApiResponse<ShelterCheckIn>> depart(@PathVariable String id) {
        return checkInService.checkOutPerson(id)
                .map(c -> ResponseEntity.ok(ApiResponse.ok("Departure logged and shelter capacity updated", c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/shelter/{shelterId}/occupancy")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOccupancy(@PathVariable String shelterId) {
        return ResponseEntity.ok(ApiResponse.ok("Shelter occupancy data", checkInService.getShelterOccupancyActivity(shelterId)));
    }

    @GetMapping("/unresolved")
    public ResponseEntity<ApiResponse<List<ShelterCheckIn>>> getUnresolved() {
        return ResponseEntity.ok(ApiResponse.ok("Unresolved shelter check-ins", checkInService.getUnresolvedCheckIns()));
    }
}
