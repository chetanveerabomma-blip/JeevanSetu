package com.jeevansetu.maps;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("mapsGeofenceService")
public class GeofenceService {

    private final EmergencyZoneRepository zoneRepository;

    public GeofenceService(EmergencyZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    @PostConstruct
    public void seedDefaultEmergencyZones() {
        if (zoneRepository.count() == 0) {
            double baseLat = 12.9716;
            double baseLng = 77.5946;

            EmergencyZone floodZone = new EmergencyZone(
                    "MG Road Underpass Flash Flood Inundation",
                    "FLOOD_AFFECTED",
                    baseLat + 0.002, baseLng - 0.003,
                    600.0,
                    "CAUTION: Water accumulation 1.2m deep. Low-clearance vehicles prohibited."
            );
            floodZone.setSeverity("CRITICAL");
            zoneRepository.save(floodZone);

            EmergencyZone firePerimeter = new EmergencyZone(
                    "Brigade Commercial Plaza Fire Exclusion Corridor",
                    "FIRE_PERIMETER",
                    baseLat - 0.008, baseLng + 0.006,
                    450.0,
                    "DANGER: Active structural firefighting and heavy smoke hazard. Evacuation in effect."
            );
            firePerimeter.setSeverity("CRITICAL");
            zoneRepository.save(firePerimeter);

            EmergencyZone safeHaven = new EmergencyZone(
                    "Kanteerava Sports Complex Central Relief Safe Zone",
                    "SAFE_ZONE",
                    baseLat - 0.003, baseLng - 0.004,
                    800.0,
                    "SAFE HAVEN: Emergency medical aid, food rations, and dry shelter beds established."
            );
            safeHaven.setSeverity("SAFE");
            zoneRepository.save(safeHaven);
        }
    }

    public List<EmergencyZone> getActiveZones() {
        return zoneRepository.findByActiveTrue();
    }

    public EmergencyZone createZone(EmergencyZone zone) {
        zone.setActive(true);
        zone.setCreatedAt(Instant.now());
        return zoneRepository.save(zone);
    }

    public void removeZone(String id) {
        zoneRepository.findById(id).ifPresent(z -> {
            z.setActive(false);
            zoneRepository.save(z);
        });
    }

    public List<EmergencyZone> findZonesIntersectingPoint(double lat, double lng) {
        return zoneRepository.findByActiveTrue().stream()
                .filter(z -> {
                    double distMeters = haversineDistance(lat, lng, z.getCenterLatitude(), z.getCenterLongitude()) * 1000.0;
                    return distMeters <= z.getRadiusMeters();
                })
                .collect(Collectors.toList());
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
