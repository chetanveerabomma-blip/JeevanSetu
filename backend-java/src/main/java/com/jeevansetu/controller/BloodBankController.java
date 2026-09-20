package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.dto.UpdateBloodStockRequest;
import com.jeevansetu.model.BloodBank;
import com.jeevansetu.service.BloodBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blood-banks")
@Tag(name = "Blood Banks", description = "Endpoints for blood inventory management and emergency supply search")
public class BloodBankController {

    private final BloodBankService bloodBankService;

    public BloodBankController(BloodBankService bloodBankService) {
        this.bloodBankService = bloodBankService;
    }

    @GetMapping
    @Operation(summary = "List all registered blood banks")
    public ResponseEntity<ApiResponse<List<BloodBank>>> getAllBloodBanks() {
        return ResponseEntity.ok(ApiResponse.ok(bloodBankService.getAllBloodBanks()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get blood bank details by ID")
    public ResponseEntity<ApiResponse<BloodBank>> getBloodBankById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(bloodBankService.getBloodBankById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search nearest blood banks with available stock of a specific blood group")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchSupply(
            @RequestParam String bloodGroup,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1") int minUnits) {
        List<Map<String, Object>> result = bloodBankService.searchBloodSupply(bloodGroup, lat, lng, minUnits);
        return ResponseEntity.ok(ApiResponse.ok("Nearest blood supply search results", result));
    }

    @PatchMapping("/{id}/inventory")
    @Operation(summary = "Update blood inventory stock")
    public ResponseEntity<ApiResponse<BloodBank>> updateInventory(
            @PathVariable String id,
            @RequestBody UpdateBloodStockRequest request,
            Authentication auth) {
        String email = auth != null ? auth.getName() : "bloodbank@jeevansetu.gov.in";
        BloodBank updated = bloodBankService.updateInventory(id, request.getInventory(), email);
        return ResponseEntity.ok(ApiResponse.ok("Inventory updated successfully", updated));
    }
}
