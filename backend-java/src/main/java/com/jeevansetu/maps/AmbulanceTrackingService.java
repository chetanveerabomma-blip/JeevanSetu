package com.jeevansetu.maps;

import com.jeevansetu.websocket.WebSocketEventPublisher;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AmbulanceTrackingService {

    private final AmbulanceLocationRepository ambulanceRepository;
    private final LocationService locationService;
    private final WebSocketEventPublisher eventPublisher;

    public AmbulanceTrackingService(AmbulanceLocationRepository ambulanceRepository,
                                    LocationService locationService,
                                    WebSocketEventPublisher eventPublisher) {
        this.ambulanceRepository = ambulanceRepository;
        this.locationService = locationService;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void seedInitialAmbulances() {
        if (ambulanceRepository.count() == 0) {
            double baseLat = 12.9716;
            double baseLng = 77.5946;

            AmbulanceLocation a1 = new AmbulanceLocation("amb-101", "AMB-104 (ALS Trauma)", baseLat + 0.008, baseLng - 0.007, "AVAILABLE");
            a1.setOrganization("National Ambulance Service");
            ambulanceRepository.save(a1);

            AmbulanceLocation a2 = new AmbulanceLocation("amb-102", "AMB-105 (BLS City)", baseLat + 0.012, baseLng + 0.006, "AVAILABLE");
            a2.setOrganization("National Ambulance Service");
            ambulanceRepository.save(a2);

            AmbulanceLocation a3 = new AmbulanceLocation("amb-103", "AMB-106 (Red Cross Cardiac)", baseLat - 0.011, baseLng - 0.009, "AVAILABLE");
            a3.setOrganization("Red Cross Ambulance Wing");
            ambulanceRepository.save(a3);
        }
    }

    public List<AmbulanceLocation> getActiveAmbulances() {
        return ambulanceRepository.findAll();
    }

    public Optional<AmbulanceLocation> getAmbulanceById(String id) {
        return ambulanceRepository.findByAmbulanceId(id);
    }

    /**
     * Updates real GPS coordinates sent from on-board tracking mobile device/GPS unit.
     */
    public AmbulanceLocation updateGpsLocation(String ambulanceId, double lat, double lng, double speedKmH, double heading, String status) {
        LocationService.validateCoordinates(lat, lng);

        AmbulanceLocation record = ambulanceRepository.findByAmbulanceId(ambulanceId)
                .orElseGet(() -> {
                    AmbulanceLocation a = new AmbulanceLocation();
                    a.setAmbulanceId(ambulanceId);
                    a.setUnitCode("AMB-" + ambulanceId);
                    return a;
                });

        record.setLatitude(lat);
        record.setLongitude(lng);
        record.setSpeedKmH(speedKmH);
        record.setHeadingDegrees(heading);
        if (status != null && !status.isEmpty()) {
            record.setStatus(status);
        }
        record.setLastUpdated(Instant.now());

        AmbulanceLocation saved = ambulanceRepository.save(record);

        // Record history log
        locationService.recordLocation(ambulanceId, "AMBULANCE", lat, lng, "Live GPS Broadcast");

        // Broadcast to WebSocket clients
        try {
            eventPublisher.publish("AMBULANCE_LOCATION_UPDATE", saved);
        } catch (Exception ignored) {}

        return saved;
    }
}
