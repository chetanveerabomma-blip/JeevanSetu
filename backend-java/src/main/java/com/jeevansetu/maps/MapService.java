package com.jeevansetu.maps;

import com.jeevansetu.evacuation.BlockedRoad;
import com.jeevansetu.evacuation.EvacuationService;
import com.jeevansetu.evacuation.SafeZone;
import com.jeevansetu.evacuation.SafeZoneService;
import com.jeevansetu.model.Emergency;
import com.jeevansetu.repository.EmergencyRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
public class MapService {

    private final EmergencyRepository emergencyRepository;
    private final AmbulanceTrackingService ambulanceTrackingService;
    private final HospitalLocationService hospitalLocationService;
    private final SafeZoneService safeZoneService;
    private final EvacuationService evacuationService;
    private final GeofenceService geofenceService;
    private final RestTemplate restTemplate;

    // In-memory cache for geocoding search to prevent rate-limit flooding against Nominatim
    private final Map<String, List<Map<String, Object>>> geocodeCache = new HashMap<>();

    public MapService(EmergencyRepository emergencyRepository,
                      AmbulanceTrackingService ambulanceTrackingService,
                      HospitalLocationService hospitalLocationService,
                      SafeZoneService safeZoneService,
                      EvacuationService evacuationService,
                      GeofenceService geofenceService,
                      RestTemplateBuilder restTemplateBuilder) {
        this.emergencyRepository = emergencyRepository;
        this.ambulanceTrackingService = ambulanceTrackingService;
        this.hospitalLocationService = hospitalLocationService;
        this.safeZoneService = safeZoneService;
        this.evacuationService = evacuationService;
        this.geofenceService = geofenceService;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(2000))
                .setReadTimeout(Duration.ofMillis(3000))
                .build();
    }

    public Map<String, Object> getComprehensiveMapTelemetry() {
        Map<String, Object> data = new HashMap<>();
        data.put("emergencies", emergencyRepository.findAll());
        data.put("ambulances", ambulanceTrackingService.getActiveAmbulances());
        data.put("hospitals", hospitalLocationService.getVerifiedHospitals());
        data.put("safeZones", safeZoneService.getAllActiveSafeZones());
        data.put("blockedRoads", evacuationService.getActiveBlockedRoads());
        data.put("emergencyZones", geofenceService.getActiveZones());
        data.put("timestamp", System.currentTimeMillis());
        return data;
    }

    /**
     * Rate-limited geocoding query proxy with in-memory caching and fallback.
     */
    public List<Map<String, Object>> searchLocations(String query) {
        if (query == null || query.trim().length() < 2) {
            return Collections.emptyList();
        }

        String key = query.trim().toLowerCase();
        if (geocodeCache.containsKey(key)) {
            return geocodeCache.get(key);
        }

        try {
            String url = String.format("https://nominatim.openstreetmap.org/search?format=json&q=%s&limit=5&countrycodes=in", query.trim());
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List raw = response.getBody();
                List<Map<String, Object>> results = new ArrayList<>();
                for (Object o : raw) {
                    if (o instanceof Map) {
                        Map m = (Map) o;
                        Map<String, Object> item = new HashMap<>();
                        item.put("displayName", m.get("display_name"));
                        item.put("latitude", Double.parseDouble((String) m.get("lat")));
                        item.put("longitude", Double.parseDouble((String) m.get("lon")));
                        item.put("type", m.get("type"));
                        results.add(item);
                    }
                }
                geocodeCache.put(key, results);
                return results;
            }
        } catch (Exception e) {
            // Graceful fallback to verified registered local entities
        }

        // Fallback local search from verified registered locations
        List<Map<String, Object>> fallback = new ArrayList<>();
        for (HospitalLocation h : hospitalLocationService.getVerifiedHospitals()) {
            if (h.getName().toLowerCase().contains(key) || h.getAddress().toLowerCase().contains(key)) {
                Map<String, Object> item = new HashMap<>();
                item.put("displayName", h.getName() + ", " + h.getAddress());
                item.put("latitude", h.getLatitude());
                item.put("longitude", h.getLongitude());
                item.put("type", "hospital");
                fallback.add(item);
            }
        }
        for (SafeZone s : safeZoneService.getAllActiveSafeZones()) {
            if (s.getName().toLowerCase().contains(key) || s.getAddress().toLowerCase().contains(key)) {
                Map<String, Object> item = new HashMap<>();
                item.put("displayName", s.getName() + " (Safe Zone), " + s.getAddress());
                item.put("latitude", s.getLatitude());
                item.put("longitude", s.getLongitude());
                item.put("type", "shelter");
                fallback.add(item);
            }
        }
        return fallback;
    }
}
