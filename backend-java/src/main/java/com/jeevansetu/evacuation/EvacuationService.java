package com.jeevansetu.evacuation;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class EvacuationService {

    private final EvacuationPlanRepository evacuationPlanRepository;
    private final SafeZoneService safeZoneService;
    private final RouteCalculationService routeCalculationService;
    private final BlockedRoadRepository blockedRoadRepository;

    public EvacuationService(EvacuationPlanRepository evacuationPlanRepository,
                             SafeZoneService safeZoneService,
                             RouteCalculationService routeCalculationService,
                             BlockedRoadRepository blockedRoadRepository) {
        this.evacuationPlanRepository = evacuationPlanRepository;
        this.safeZoneService = safeZoneService;
        this.routeCalculationService = routeCalculationService;
        this.blockedRoadRepository = blockedRoadRepository;
    }

    public EvacuationPlan generateEvacuationPlan(String areaName, double originLat, double originLng, double radiusMeters) {
        List<SafeZone> nearestSafeZones = safeZoneService.findNearestSafeZones(originLat, originLng, 3);
        if (nearestSafeZones.isEmpty()) {
            throw new IllegalStateException("No active safe zones or relief shelters registered in the region.");
        }

        SafeZone primary = nearestSafeZones.get(0);
        SafeZone secondary = nearestSafeZones.size() > 1 ? nearestSafeZones.get(1) : primary;

        // Calculate primary route avoiding blocked roads
        RouteCalculationService.CalculatedRoute primaryRoute = routeCalculationService.calculateRoute(
                originLat, originLng, primary.getLatitude(), primary.getLongitude(), false
        );

        // Calculate alternative route
        RouteCalculationService.CalculatedRoute altRoute = routeCalculationService.calculateRoute(
                originLat, originLng, secondary.getLatitude(), secondary.getLongitude(), true
        );

        EvacuationPlan plan = new EvacuationPlan();
        plan.setPlanName("Evacuation Protocol: " + areaName);
        plan.setAffectedAreaName(areaName);
        plan.setOriginLatitude(originLat);
        plan.setOriginLongitude(originLng);
        plan.setAffectedRadiusMeters(radiusMeters);

        plan.setPrimarySafeZoneId(primary.getId());
        plan.setPrimarySafeZoneName(primary.getName() + " (" + primary.getAvailableCapacity() + " beds available)");
        plan.setSecondarySafeZoneId(secondary.getId());
        plan.setSecondarySafeZoneName(secondary.getName() + " (" + secondary.getAvailableCapacity() + " beds available)");

        plan.setEstimatedDistanceKm(primaryRoute.getDistanceKm());
        plan.setEstimatedWalkMinutes(primaryRoute.getDurationMinutes());
        plan.setPrimaryRouteCoordinates(primaryRoute.getCoordinates());
        plan.setAlternativeRouteCoordinates(altRoute.getCoordinates());
        plan.setAvoidedBlockedRoads(primaryRoute.getAvoidedBlockedRoads());

        List<String> instructions = new ArrayList<>();
        instructions.add("1. IMMEDIATELY move along the designated primary corridor towards " + primary.getName() + ".");
        instructions.add("2. Carry your emergency kit, vital medications, and ID cards.");
        if (!primaryRoute.getAvoidedBlockedRoads().isEmpty()) {
            instructions.add("3. AVOID BLOCKED SECTORS: " + String.join(", ", primaryRoute.getAvoidedBlockedRoads()) + ".");
        } else {
            instructions.add("3. Stay clear of low-lying underpasses and loose power cables.");
        }
        instructions.add("4. If the primary corridor is obstructed, pivot immediately to Alternative Refuge: " + secondary.getName() + ".");
        instructions.add("5. Check in at the shelter registration desk upon arrival to record your safety status.");

        plan.setEvacuationInstructions(instructions);
        return evacuationPlanRepository.save(plan);
    }

    public List<EvacuationPlan> getActivePlans() {
        return evacuationPlanRepository.findByActiveTrueOrderByGeneratedAtDesc();
    }

    public BlockedRoad reportBlockedRoad(BlockedRoad road) {
        return blockedRoadRepository.save(road);
    }

    public List<BlockedRoad> getActiveBlockedRoads() {
        return blockedRoadRepository.findByActiveTrue();
    }

    public void removeBlockedRoad(String id) {
        blockedRoadRepository.findById(id).ifPresent(r -> {
            r.setActive(false);
            blockedRoadRepository.save(r);
        });
    }
}
