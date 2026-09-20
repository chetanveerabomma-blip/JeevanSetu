package com.jeevansetu.service;

import com.jeevansetu.algorithm.ResourceSuitabilityScorer;
import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.BloodBank;
import com.jeevansetu.repository.BloodBankRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class BloodBankService {

    private final BloodBankRepository bloodBankRepository;
    private final AuditLogService auditLogService;

    public BloodBankService(BloodBankRepository bloodBankRepository, AuditLogService auditLogService) {
        this.bloodBankRepository = bloodBankRepository;
        this.auditLogService = auditLogService;
    }

    public List<BloodBank> getAllBloodBanks() {
        return bloodBankRepository.findAll();
    }

    public BloodBank getBloodBankById(String id) {
        return bloodBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood bank not found: " + id));
    }

    /**
     * Search nearest blood banks with available stock of a specific blood group.
     */
    public List<Map<String, Object>> searchBloodSupply(String bloodGroup, double lat, double lng, int minUnits) {
        List<BloodBank> all = bloodBankRepository.findAll();
        List<Map<String, Object>> results = new ArrayList<>();

        for (BloodBank bb : all) {
            int available = bb.getInventory().getOrDefault(bloodGroup.trim().toUpperCase(), 0);
            if (available >= minUnits) {
                double distanceKm = ResourceSuitabilityScorer.calculateHaversineDistance(
                        lat, lng, bb.getLatitude(), bb.getLongitude()
                );
                Map<String, Object> item = new HashMap<>();
                item.put("bloodBank", bb);
                item.put("bloodGroup", bloodGroup);
                item.put("availableUnits", available);
                item.put("distanceKm", distanceKm);
                results.add(item);
            }
        }

        results.sort(Comparator.comparingDouble(a -> (double) a.get("distanceKm")));
        return results;
    }

    public BloodBank updateInventory(String bloodBankId, Map<String, Integer> inventory, String actorEmail) {
        BloodBank bb = getBloodBankById(bloodBankId);
        bb.getInventory().putAll(inventory);
        bb.setLastUpdated(Instant.now());

        BloodBank saved = bloodBankRepository.save(bb);

        auditLogService.logAction(
                actorEmail, "BLOOD_BANK", "BLOOD_INVENTORY_UPDATED",
                bb.getId(), bb.getRegistrationId(),
                "Updated stock inventory for " + inventory.keySet(),
                null, bb.getStatus()
        );

        return saved;
    }
}
