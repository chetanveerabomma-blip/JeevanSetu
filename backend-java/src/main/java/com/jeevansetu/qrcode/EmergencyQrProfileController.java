package com.jeevansetu.qrcode;

import com.jeevansetu.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qr")
public class EmergencyQrProfileController {

    private final EmergencyQrProfileService qrProfileService;

    public EmergencyQrProfileController(EmergencyQrProfileService qrProfileService) {
        this.qrProfileService = qrProfileService;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<EmergencyQrProfileService.QrProfileView>> getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (auth != null && auth.getName() != null) ? auth.getName() : "user-default";
        return ResponseEntity.ok(ApiResponse.ok("Emergency QR profile retrieved", qrProfileService.getOrCreateProfile(userId, auth != null ? auth.getName() : "Citizen User")));
    }

    @PostMapping("/permissions")
    public ResponseEntity<ApiResponse<EmergencyQrProfile>> updatePermissions(@RequestBody Map<String, Boolean> toggles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (auth != null && auth.getName() != null) ? auth.getName() : "user-default";
        return ResponseEntity.ok(ApiResponse.ok("Data disclosure permissions updated", qrProfileService.updatePermissions(userId, toggles)));
    }

    @PostMapping("/regenerate")
    public ResponseEntity<ApiResponse<EmergencyQrProfileService.QrProfileView>> regenerateQr() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (auth != null && auth.getName() != null) ? auth.getName() : "user-default";
        return ResponseEntity.ok(ApiResponse.ok("Previous QR revoked and new key issued", qrProfileService.regenerateAccessKey(userId)));
    }

    /**
     * Public scan endpoint for first responders (paramedics, police). Returns ONLY permitted data.
     */
    @GetMapping("/public-view/{accessKey}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> scanEmergencyQr(
            @PathVariable String accessKey,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = (auth != null && auth.isAuthenticated()) ? auth.getAuthorities().toString() : "ON_SCENE_RESPONDER";

        Map<String, Object> data = qrProfileService.scanAndFetchPermittedData(accessKey, role, ip);
        return ResponseEntity.ok(ApiResponse.ok("Permitted emergency vital details resolved", data));
    }

    @GetMapping("/access-logs")
    public ResponseEntity<ApiResponse<List<QrAccessLog>>> getMyAccessLogs() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (auth != null && auth.getName() != null) ? auth.getName() : "user-default";
        return ResponseEntity.ok(ApiResponse.ok("Audit log history retrieved", qrProfileService.getAuditLogsForUser(userId)));
    }
}
