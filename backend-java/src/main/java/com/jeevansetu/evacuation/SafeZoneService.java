package com.jeevansetu.evacuation;

import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SafeZoneService {

    private final SafeZoneRepository safeZoneRepository;

    public SafeZoneService(SafeZoneRepository safeZoneRepository) {
        this.safeZoneRepository = safeZoneRepository;
    }

    public List<SafeZone> getAllActiveSafeZones() {
        return safeZoneRepository.findByActiveTrue();
    }

    public Optional<SafeZone> getSafeZoneById(String id) {
        return safeZoneRepository.findById(id);
    }

    public SafeZone registerSafeZone(SafeZone zone) {
        return safeZoneRepository.save(zone);
    }

    public Optional<SafeZone> updateOccupancy(String id, int delta) {
        return safeZoneRepository.findById(id).map(z -> {
            int newOcc = Math.max(0, Math.min(z.getTotalCapacity(), z.getCurrentOccupancy() + delta));
            z.setCurrentOccupancy(newOcc);
            return safeZoneRepository.save(z);
        });
    }

    public List<SafeZone> findNearestSafeZones(double lat, double lng, int limit) {
        List<SafeZone> active = safeZoneRepository.findByActiveTrue();
        return active.stream()
                .filter(z -> z.getAvailableCapacity() > 0)
                .sorted(Comparator.comparingDouble(z -> haversineDistance(lat, lng, z.getLatitude(), z.getLongitude())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
