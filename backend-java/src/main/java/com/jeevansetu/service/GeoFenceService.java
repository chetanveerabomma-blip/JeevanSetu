package com.jeevansetu.service;

import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.*;
import com.jeevansetu.repository.*;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeoFenceService {

    private static final double EARTH_RADIUS_METERS = 6371000.0;

    private final GeoZoneRepository geoZoneRepository;
    private final ResponderUnitRepository responderRepository;
    private final HospitalRepository hospitalRepository;
    private final ReliefCenterRepository reliefCenterRepository;
    private final EmergencyRepository emergencyRepository;
    private final AuditLogService auditLogService;
    private final WebSocketEventPublisher eventPublisher;

    public GeoFenceService(GeoZoneRepository geoZoneRepository,
                           ResponderUnitRepository responderRepository,
                           HospitalRepository hospitalRepository,
                           ReliefCenterRepository reliefCenterRepository,
                           EmergencyRepository emergencyRepository,
                           AuditLogService auditLogService,
                           WebSocketEventPublisher eventPublisher) {
        this.geoZoneRepository = geoZoneRepository;
        this.responderRepository = responderRepository;
        this.hospitalRepository = hospitalRepository;
        this.reliefCenterRepository = reliefCenterRepository;
        this.emergencyRepository = emergencyRepository;
        this.auditLogService = auditLogService;
        this.eventPublisher = eventPublisher;
    }

    public GeoZone createZone(GeoZone zone, String creatorName) {
        zone.setStatus("ACTIVE");
        zone.setCreatedAt(Instant.now());
        zone.setCreatedBy(creatorName);

        GeoZone saved = geoZoneRepository.save(zone);

        auditLogService.log("CREATE_GEOZONE", saved.getId(), "GEOZONE",
                "Created zone: " + saved.getZoneName() + " (Radius: " + saved.getRadiusMeters() + "m)");

        eventPublisher.publish("GEOFENCE_ALERT", Map.of(
                "zoneId", saved.getId(),
                "zoneName", saved.getZoneName(),
                "zoneType", saved.getZoneType(),
                "severity", saved.getSeverity(),
                "latitude", saved.getCenterLatitude(),
                "longitude", saved.getCenterLongitude(),
                "radiusMeters", saved.getRadiusMeters()
        ));

        return saved;
    }

    public List<GeoZone> getAllZones(String status) {
        if (status != null && !status.isBlank()) {
            return geoZoneRepository.findByStatus(status, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        return geoZoneRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public GeoZone updateZoneStatus(String zoneId, String status, String actorName) {
        GeoZone zone = geoZoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found: " + zoneId));

        zone.setStatus(status);
        GeoZone saved = geoZoneRepository.save(zone);

        auditLogService.log("UPDATE_GEOZONE", saved.getId(), "GEOZONE", "Status: " + status + " by " + actorName);
        return saved;
    }

    public Map<String, Object> scanResourcesInZone(String zoneId) {
        GeoZone zone = geoZoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found: " + zoneId));

        double centerLat = zone.getCenterLatitude();
        double centerLng = zone.getCenterLongitude();
        double radius = zone.getRadiusMeters();

        // 1. Responders in zone
        List<ResponderUnit> inZoneResponders = responderRepository.findAll().stream()
                .filter(r -> calculateDistanceMeters(centerLat, centerLng, r.getLatitude(), r.getLongitude()) <= radius)
                .collect(Collectors.toList());

        // 2. Hospitals in zone or nearby
        List<Hospital> inZoneHospitals = hospitalRepository.findAll().stream()
                .filter(h -> calculateDistanceMeters(centerLat, centerLng, h.getLatitude(), h.getLongitude()) <= radius)
                .collect(Collectors.toList());

        // 3. Relief Centers in zone
        List<ReliefCenter> inZoneCenters = reliefCenterRepository.findAll().stream()
                .filter(rc -> calculateDistanceMeters(centerLat, centerLng, rc.getLatitude(), rc.getLongitude()) <= radius)
                .collect(Collectors.toList());

        // 4. Incidents in zone
        List<Emergency> inZoneIncidents = emergencyRepository.findAll().stream()
                .filter(e -> e.getStatus() != EmergencyStatus.RESOLVED && e.getStatus() != EmergencyStatus.CANCELLED)
                .filter(e -> calculateDistanceMeters(centerLat, centerLng, e.getLatitude(), e.getLongitude()) <= radius)
                .collect(Collectors.toList());

        return Map.of(
                "zone", zone,
                "responders", inZoneResponders,
                "hospitals", inZoneHospitals,
                "reliefCenters", inZoneCenters,
                "activeIncidents", inZoneIncidents,
                "totalEntitiesWithinRadius", inZoneResponders.size() + inZoneHospitals.size() + inZoneCenters.size() + inZoneIncidents.size()
        );
    }

    public boolean isPointInAnyActiveDangerZone(double lat, double lng) {
        List<GeoZone> activeZones = geoZoneRepository.findByStatus("ACTIVE", Sort.unsorted());
        for (GeoZone zone : activeZones) {
            double distance = calculateDistanceMeters(zone.getCenterLatitude(), zone.getCenterLongitude(), lat, lng);
            if (distance <= zone.getRadiusMeters()) {
                return true;
            }
        }
        return false;
    }

    // Java Haversine Distance Formula
    public static double calculateDistanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }
}
