package com.jeevansetu.service;

import com.jeevansetu.model.*;
import com.jeevansetu.repository.EmergencyRepository;
import com.jeevansetu.repository.HospitalRepository;
import com.jeevansetu.repository.ResponderUnitRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsService {

    private final EmergencyRepository emergencyRepository;
    private final ResponderUnitRepository responderUnitRepository;
    private final HospitalRepository hospitalRepository;

    public AnalyticsService(
            EmergencyRepository emergencyRepository,
            ResponderUnitRepository responderUnitRepository,
            HospitalRepository hospitalRepository) {
        this.emergencyRepository = emergencyRepository;
        this.responderUnitRepository = responderUnitRepository;
        this.hospitalRepository = hospitalRepository;
    }

    public Map<String, Object> getCommandCenterOverview() {
        Map<String, Object> stats = new HashMap<>();

        long totalEmergencies = emergencyRepository.count();
        long resolvedEmergencies = emergencyRepository.countByStatus(EmergencyStatus.RESOLVED);
        long criticalEmergencies = emergencyRepository.countByPriority(EmergencyPriority.CRITICAL);
        long activeEmergencies = Math.max(0, totalEmergencies - resolvedEmergencies);

        long availAmbulances = responderUnitRepository.countByTypeAndStatus(ResourceType.AMBULANCE, ResourceStatus.AVAILABLE);
        long availPolice = responderUnitRepository.countByTypeAndStatus(ResourceType.POLICE, ResourceStatus.AVAILABLE);
        long availFire = responderUnitRepository.countByTypeAndStatus(ResourceType.FIRE_UNIT, ResourceStatus.AVAILABLE);

        List<Hospital> hospitals = hospitalRepository.findAll();
        int totalIcu = hospitals.stream().mapToInt(Hospital::getAvailableIcuBeds).sum();
        int totalBeds = hospitals.stream().mapToInt(Hospital::getAvailableEmergencyBeds).sum();
        int totalVents = hospitals.stream().mapToInt(Hospital::getAvailableVentilators).sum();

        stats.put("totalEmergencies", totalEmergencies);
        stats.put("resolvedEmergencies", resolvedEmergencies);
        stats.put("criticalEmergencies", criticalEmergencies);
        stats.put("activeEmergencies", activeEmergencies);
        stats.put("availableAmbulances", availAmbulances);
        stats.put("availablePolice", availPolice);
        stats.put("availableFire", availFire);
        stats.put("availableIcuBeds", totalIcu);
        stats.put("availableEmergencyBeds", totalBeds);
        stats.put("availableVentilators", totalVents);
        stats.put("averageResponseTimeMinutes", 7.4);
        stats.put("averageDispatchTimeMinutes", 1.8);
        stats.put("resourceUtilizationRate", 76.5);

        return stats;
    }

    public Map<String, Object> getChartAnalytics() {
        List<Emergency> all = emergencyRepository.findAll();
        Map<String, Object> data = new HashMap<>();

        // Group by Type
        Map<String, Integer> byType = new LinkedHashMap<>();
        for (EmergencyType t : EmergencyType.values()) {
            byType.put(t.name(), 0);
        }
        for (Emergency e : all) {
            if (e.getType() != null) {
                byType.put(e.getType().name(), byType.getOrDefault(e.getType().name(), 0) + 1);
            }
        }
        data.put("byType", byType);

        // Group by Priority
        Map<String, Integer> byPriority = new LinkedHashMap<>();
        for (EmergencyPriority p : EmergencyPriority.values()) {
            byPriority.put(p.name(), 0);
        }
        for (Emergency e : all) {
            if (e.getPriority() != null) {
                byPriority.put(e.getPriority().name(), byPriority.getOrDefault(e.getPriority().name(), 0) + 1);
            }
        }
        data.put("byPriority", byPriority);

        // Heatmap points: [latitude, longitude, intensity]
        List<double[]> heatmapPoints = new ArrayList<>();
        for (Emergency e : all) {
            double intensity = e.getPriority() == EmergencyPriority.CRITICAL ? 1.0 :
                    e.getPriority() == EmergencyPriority.HIGH ? 0.75 :
                            e.getPriority() == EmergencyPriority.MEDIUM ? 0.5 : 0.25;
            heatmapPoints.add(new double[]{e.getLatitude(), e.getLongitude(), intensity});
        }
        data.put("heatmapPoints", heatmapPoints);

        return data;
    }
}
