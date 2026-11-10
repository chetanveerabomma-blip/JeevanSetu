package com.jeevansetu.publicsafety;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public-safety")
public class PublicSafetyController {

    private final PublicSafetyService publicSafetyService;

    public PublicSafetyController(PublicSafetyService publicSafetyService) {
        this.publicSafetyService = publicSafetyService;
    }

    @PostMapping("/reports")
    public ResponseEntity<ApiResponse<PublicSafetyReport>> reportHazard(@RequestBody PublicSafetyReport report) {
        return ResponseEntity.ok(ApiResponse.ok("Public safety hazard logged", publicSafetyService.submitReport(report)));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<List<PublicSafetyReport>>> listReports(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(ApiResponse.ok("Public safety reports retrieved", publicSafetyService.getAllReports(status, category)));
    }

    @GetMapping("/map-hazards")
    public ResponseEntity<ApiResponse<List<PublicSafetyReport>>> getMapHazards() {
        return ResponseEntity.ok(ApiResponse.ok("Verified map hazards retrieved", publicSafetyService.getActiveMapHazards()));
    }

    @PutMapping("/reports/{id}/verify")
    public ResponseEntity<ApiResponse<PublicSafetyReport>> verifyReport(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "VERIFIED");
        String remarks = body.getOrDefault("remarks", "Approved by authority");
        String verifier = body.getOrDefault("verifiedBy", "Safety Inspector");

        return publicSafetyService.verifyReport(id, status, remarks, verifier)
                .map(r -> ResponseEntity.ok(ApiResponse.ok("Report status updated", r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reports/{id}/upvote")
    public ResponseEntity<ApiResponse<PublicSafetyReport>> upvoteReport(@PathVariable String id) {
        return publicSafetyService.upvoteReport(id)
                .map(r -> ResponseEntity.ok(ApiResponse.ok("Report upvoted", r)))
                .orElse(ResponseEntity.notFound().build());
    }
}
