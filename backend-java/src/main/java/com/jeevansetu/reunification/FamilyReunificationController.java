package com.jeevansetu.reunification;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reunification")
public class FamilyReunificationController {

    private final FamilyReunificationService reunificationService;

    public FamilyReunificationController(FamilyReunificationService reunificationService) {
        this.reunificationService = reunificationService;
    }

    @PostMapping("/family-groups")
    public ResponseEntity<ApiResponse<FamilyGroup>> createFamilyGroup(@RequestBody FamilyGroup group) {
        return ResponseEntity.ok(ApiResponse.ok("Family group registered", reunificationService.registerFamilyGroup(group)));
    }

    @GetMapping("/family-groups/{id}")
    public ResponseEntity<ApiResponse<FamilyGroup>> getFamilyGroup(@PathVariable String id) {
        return reunificationService.getFamilyGroup(id)
                .map(g -> ResponseEntity.ok(ApiResponse.ok("Family group retrieved", g)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/missing-reports")
    public ResponseEntity<ApiResponse<MissingPersonReport>> reportMissing(@RequestBody MissingPersonReport report) {
        return ResponseEntity.ok(ApiResponse.ok("Missing person report logged securely", reunificationService.reportMissingPerson(report)));
    }

    @GetMapping("/missing-reports")
    public ResponseEntity<ApiResponse<List<MissingPersonReport>>> searchMissing(@RequestParam(required = false) String query) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isOfficial = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN") || a.getAuthority().contains("DISPATCHER") || a.getAuthority().contains("CONTROL_CENTER"));
        
        List<MissingPersonReport> results = reunificationService.searchMissingPersons(query, isOfficial);
        return ResponseEntity.ok(ApiResponse.ok("Missing person reports retrieved", results));
    }

    @PutMapping("/missing-reports/{id}/status")
    public ResponseEntity<ApiResponse<MissingPersonReport>> updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "LOCATED");
        String remarks = body.getOrDefault("officialRemarks", "Verified by authorized emergency official");
        String verifier = body.getOrDefault("verifiedBy", "Command Dispatcher");

        return reunificationService.updateVerificationStatus(id, status, remarks, verifier)
                .map(r -> ResponseEntity.ok(ApiResponse.ok("Status updated", r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/safe-arrivals")
    public ResponseEntity<ApiResponse<SafeArrivalConfirmation>> recordArrival(@RequestBody SafeArrivalConfirmation confirmation) {
        return ResponseEntity.ok(ApiResponse.ok("Safe arrival recorded", reunificationService.confirmSafeArrival(confirmation)));
    }

    @GetMapping("/safe-arrivals")
    public ResponseEntity<ApiResponse<List<SafeArrivalConfirmation>>> getArrivals() {
        return ResponseEntity.ok(ApiResponse.ok("Recent safe arrivals", reunificationService.getRecentSafeArrivals()));
    }
}
