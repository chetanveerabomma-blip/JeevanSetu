package com.jeevansetu.maps;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HospitalLocationService {

    private final HospitalLocationRepository hospitalRepository;

    public HospitalLocationService(HospitalLocationRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    @PostConstruct
    public void seedInitialHospitals() {
        if (hospitalRepository.count() == 0) {
            double baseLat = 12.9716;
            double baseLng = 77.5946;

            HospitalLocation h1 = new HospitalLocation("hosp-1", "Government Victoria Trauma Hospital",
                    "Medical College Campus, Fort Road", baseLat + 0.007, baseLng - 0.008, 18, 40);
            h1.setEmergencyContactPhone("080-22001001");
            h1.setSpecializedServices(Arrays.asList("Level 1 Trauma Center", "Burn ICU", "Cardiology"));
            hospitalRepository.save(h1);

            HospitalLocation h2 = new HospitalLocation("hosp-2", "Apex Multi-Speciality Medical Center",
                    "Central Ring Road, Sector 2", baseLat - 0.006, baseLng + 0.011, 14, 35);
            h2.setEmergencyContactPhone("080-22001002");
            h2.setSpecializedServices(Arrays.asList("Neurology", "Trauma ER", "Stroke Unit"));
            hospitalRepository.save(h2);

            HospitalLocation h3 = new HospitalLocation("hosp-3", "Fortis Emergency & Trauma Institute",
                    "Bannerghatta Tech Corridor", baseLat - 0.018, baseLng - 0.005, 20, 50);
            h3.setEmergencyContactPhone("080-22001003");
            h3.setSpecializedServices(Arrays.asList("Pediatric ICU", "Cardiothoracic Surgery"));
            hospitalRepository.save(h3);
        }
    }

    public List<HospitalLocation> getVerifiedHospitals() {
        return hospitalRepository.findAll();
    }

    public Optional<HospitalLocation> getHospitalById(String id) {
        return hospitalRepository.findByHospitalId(id);
    }

    public List<HospitalLocation> findNearestHospitals(double lat, double lng, int limit) {
        List<HospitalLocation> all = hospitalRepository.findAll();
        return all.stream()
                .sorted(Comparator.comparingDouble(h -> haversineDistance(lat, lng, h.getLatitude(), h.getLongitude())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public HospitalLocation updateBedTelemetry(String hospitalId, int icuBeds, int erBeds) {
        HospitalLocation h = hospitalRepository.findByHospitalId(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found: " + hospitalId));
        h.setIcuBedsAvailable(Math.max(0, icuBeds));
        h.setEmergencyBedsAvailable(Math.max(0, erBeds));
        h.setLastUpdated(Instant.now());
        return hospitalRepository.save(h);
    }

    public double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
