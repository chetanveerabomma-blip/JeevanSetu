package com.jeevansetu.service;

import com.jeevansetu.algorithm.ResourceSuitabilityScorer;
import com.jeevansetu.dto.UpdateHospitalCapacityRequest;
import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.Hospital;
import com.jeevansetu.repository.HospitalRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final WebSocketEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    public HospitalService(HospitalRepository hospitalRepository, WebSocketEventPublisher eventPublisher, AuditLogService auditLogService) {
        this.hospitalRepository = hospitalRepository;
        this.eventPublisher = eventPublisher;
        this.auditLogService = auditLogService;
    }

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public Hospital getHospitalById(String id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found: " + id));
    }

    public List<Hospital> getNearbyHospitals(double lat, double lng, int limit) {
        List<Hospital> all = hospitalRepository.findAll();
        all.sort(Comparator.comparingDouble(h ->
                ResourceSuitabilityScorer.calculateHaversineDistance(lat, lng, h.getLatitude(), h.getLongitude())
        ));
        return all.stream().limit(limit).toList();
    }

    public Hospital updateCapacity(String hospitalId, UpdateHospitalCapacityRequest req, String actorEmail) {
        Hospital hospital = getHospitalById(hospitalId);

        hospital.setEmergencyBeds(req.getEmergencyBeds());
        hospital.setOccupiedEmergencyBeds(req.getOccupiedEmergencyBeds());
        hospital.setIcuBeds(req.getIcuBeds());
        hospital.setOccupiedIcuBeds(req.getOccupiedIcuBeds());
        hospital.setVentilators(req.getVentilators());
        hospital.setOccupiedVentilators(req.getOccupiedVentilators());
        hospital.setOperatingRooms(req.getOperatingRooms());
        hospital.setOccupiedOperatingRooms(req.getOccupiedOperatingRooms());
        if (req.getStatus() != null) {
            hospital.setStatus(req.getStatus());
        }
        hospital.setLastUpdated(Instant.now());

        Hospital saved = hospitalRepository.save(hospital);
        eventPublisher.publishHospitalCapacityUpdated(saved);

        auditLogService.logAction(
                actorEmail, "HOSPITAL", "HOSPITAL_CAPACITY_UPDATED",
                hospital.getId(), hospital.getRegistrationId(),
                String.format("Updated capacity: ICU avail=%d, Emergency avail=%d, Vents avail=%d",
                        hospital.getAvailableIcuBeds(), hospital.getAvailableEmergencyBeds(), hospital.getAvailableVentilators()),
                null, hospital.getStatus()
        );

        return saved;
    }
}
