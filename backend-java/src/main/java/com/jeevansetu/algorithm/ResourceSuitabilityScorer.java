package com.jeevansetu.algorithm;

import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.EmergencyPriority;
import com.jeevansetu.model.ResourceStatus;
import com.jeevansetu.model.ResponderUnit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ResourceSuitabilityScorer {

    /**
     * Score and rank candidate responder units for an emergency incident.
     */
    public List<ScoredResource> rankResources(Emergency emergency, List<ResponderUnit> candidateUnits) {
        List<ScoredResource> scoredList = new ArrayList<>();

        for (ResponderUnit unit : candidateUnits) {
            double distanceKm = calculateHaversineDistance(
                    unit.getLatitude(), unit.getLongitude(),
                    emergency.getLatitude(), emergency.getLongitude()
            );

            ScoredResource scored = new ScoredResource(unit, 0, distanceKm);

            // 1. Distance Score (0 - 40 points): closer gets higher points
            double maxDistanceCutoff = 30.0; // km
            double distRatio = Math.max(0, 1.0 - (distanceKm / maxDistanceCutoff));
            double distanceScore = distRatio * 40.0;
            scored.setDistanceScore(distanceScore);
            scored.addReason(String.format("%.1f km away", distanceKm));

            // 2. Availability Score (0 - 30 points)
            double availabilityScore = 0.0;
            if (unit.getStatus() == ResourceStatus.AVAILABLE) {
                availabilityScore = 30.0;
                scored.addReason("Available immediately");
            } else if (unit.getStatus() == ResourceStatus.EN_ROUTE) {
                availabilityScore = 12.0;
                scored.addReason("En route (nearby reroute possible)");
            } else if (unit.getStatus() == ResourceStatus.ASSIGNED) {
                availabilityScore = 5.0;
            } else {
                availabilityScore = 0.0;
            }
            scored.setAvailabilityScore(availabilityScore);

            // 3. Equipment / Emergency Compatibility Score (0 - 15 points)
            double compatibilityScore = 5.0;
            String eq = unit.getEquipmentLevel() != null ? unit.getEquipmentLevel().toLowerCase() : "";
            if (emergency.getPriority() == EmergencyPriority.CRITICAL) {
                if (eq.contains("advanced") || eq.contains("als") || eq.contains("trauma")) {
                    compatibilityScore = 15.0;
                    scored.addReason("Advanced Trauma/ALS equipment equipped");
                } else if (eq.contains("rescue") || eq.contains("hazmat")) {
                    compatibilityScore = 12.0;
                    scored.addReason("Specialized rescue gear available");
                } else {
                    compatibilityScore = 8.0;
                    scored.addReason("Basic equipment");
                }
            } else {
                compatibilityScore = 10.0;
                scored.addReason("Equipment fully suitable for priority level");
            }
            scored.setCompatibilityScore(compatibilityScore);

            // 4. Capacity Score (0 - 10 points)
            double capacityScore = 5.0;
            if (unit.getCapacity() >= Math.max(1, emergency.getInjuredCount())) {
                capacityScore = 10.0;
                scored.addReason(String.format("Capacity (%d) sufficient for %d injured", unit.getCapacity(), emergency.getInjuredCount()));
            } else if (unit.getCapacity() > 0) {
                capacityScore = 5.0;
                scored.addReason(String.format("Partial capacity (%d)", unit.getCapacity()));
            }
            scored.setCapacityScore(capacityScore);

            // 5. Workload Score (0 - 5 points)
            // Units with fewer trips today get higher rest preference
            double workloadScore = Math.max(0, 5.0 - (unit.getCompletedTrips() * 0.5));
            scored.setWorkloadScore(workloadScore);

            // Total Score calculation
            double total = distanceScore + availabilityScore + compatibilityScore + capacityScore + workloadScore;
            scored.setTotalScore(Math.round(total * 10.0) / 10.0);

            scoredList.add(scored);
        }

        Collections.sort(scoredList);
        return scoredList;
    }

    /**
     * Haversine distance in kilometers between two GPS coordinates.
     */
    public static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        if (lat1 == 0 || lon1 == 0 || lat2 == 0 || lon2 == 0) return 1.0;
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 100.0) / 100.0;
    }
}
