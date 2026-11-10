package com.jeevansetu.maps;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LocationRecord recordLocation(String entityId, String entityType, double lat, double lng, String address) {
        validateCoordinates(lat, lng);
        LocationRecord rec = new LocationRecord(entityId, entityType, lat, lng, address);
        rec.setTimestamp(Instant.now());
        return locationRepository.save(rec);
    }

    public List<LocationRecord> getEntityHistory(String entityId) {
        return locationRepository.findByEntityId(entityId);
    }

    public List<LocationRecord> getLocationsByType(String entityType) {
        return locationRepository.findByEntityType(entityType);
    }

    public static void validateCoordinates(double lat, double lng) {
        if (lat < -90.0 || lat > 90.0) {
            throw new IllegalArgumentException("Invalid latitude: " + lat + ". Must be between -90 and 90.");
        }
        if (lng < -180.0 || lng > 180.0) {
            throw new IllegalArgumentException("Invalid longitude: " + lng + ". Must be between -180 and 180.");
        }
    }
}
