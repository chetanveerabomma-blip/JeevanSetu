package com.jeevansetu.service;

import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.BloodDonor;
import com.jeevansetu.model.BloodRequest;
import com.jeevansetu.repository.BloodDonorRepository;
import com.jeevansetu.repository.BloodRequestRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BloodDonorCoordinationService {

    private final BloodDonorRepository donorRepository;
    private final BloodRequestRepository requestRepository;
    private final AuditLogService auditLogService;
    private final WebSocketEventPublisher eventPublisher;

    public BloodDonorCoordinationService(BloodDonorRepository donorRepository,
                                         BloodRequestRepository requestRepository,
                                         AuditLogService auditLogService,
                                         WebSocketEventPublisher eventPublisher) {
        this.donorRepository = donorRepository;
        this.requestRepository = requestRepository;
        this.auditLogService = auditLogService;
        this.eventPublisher = eventPublisher;
    }

    public BloodDonor registerDonor(BloodDonor donor) {
        if (donor.getPhone() != null && donorRepository.findByPhone(donor.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Donor already registered with this phone number");
        }
        donor.setAvailable(true);
        donor.setRegisteredAt(Instant.now());

        BloodDonor saved = donorRepository.save(donor);
        auditLogService.log("REGISTER_BLOOD_DONOR", saved.getId(), "BLOOD_DONOR", "Registered: " + saved.getBloodGroup());
        return maskDonor(saved);
    }

    public List<BloodDonor> findMatchingDonors(String bloodGroup, double targetLat, double targetLng, double maxRadiusKm) {
        List<BloodDonor> compatible = donorRepository.findByBloodGroupAndIsAvailableTrue(bloodGroup);

        return compatible.stream()
                .filter(d -> {
                    if (targetLat != 0 && targetLng != 0 && d.getLatitude() != 0 && d.getLongitude() != 0) {
                        double dist = GeoFenceService.calculateDistanceMeters(targetLat, targetLng, d.getLatitude(), d.getLongitude()) / 1000.0;
                        return dist <= maxRadiusKm;
                    }
                    return true;
                })
                .map(this::maskDonor)
                .collect(Collectors.toList());
    }

    public BloodRequest createBloodRequest(BloodRequest request) {
        if (request.getRequestCode() == null || request.getRequestCode().isBlank()) {
            request.setRequestCode("REQ-BLD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        request.setStatus("PENDING");
        request.setCreatedAt(Instant.now());

        BloodRequest saved = requestRepository.save(request);

        eventPublisher.publish("BLOOD_REQUEST_BROADCAST", Map.of(
                "requestCode", saved.getRequestCode(),
                "bloodGroup", saved.getBloodGroup(),
                "hospitalName", saved.getHospitalName(),
                "urgency", saved.getUrgency(),
                "unitsRequired", saved.getUnitsRequired()
        ));

        auditLogService.log("CREATE_BLOOD_REQUEST", saved.getId(), "BLOOD_REQUEST",
                "Hospital: " + saved.getHospitalName() + " (" + saved.getBloodGroup() + ")");

        return saved;
    }

    public BloodRequest fulfillRequest(String requestId, int unitsProvided, String coordinator) {
        BloodRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found: " + requestId));

        req.setUnitsFulfilled(req.getUnitsFulfilled() + unitsProvided);
        if (req.getUnitsFulfilled() >= req.getUnitsRequired()) {
            req.setStatus("FULFILLED");
            req.setFulfilledAt(Instant.now());
        } else {
            req.setStatus("MATCHED");
        }

        BloodRequest saved = requestRepository.save(req);
        auditLogService.log("FULFILL_BLOOD_REQUEST", saved.getId(), "BLOOD_REQUEST",
                "Fulfilled " + unitsProvided + " units by " + coordinator);

        return saved;
    }

    public List<BloodRequest> getAllRequests(String status) {
        if (status != null && !status.isBlank()) {
            return requestRepository.findByStatus(status, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        return requestRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Map<String, Object> getBloodCoordinationOverview() {
        long totalDonors = donorRepository.count();
        long availableDonors = donorRepository.countByIsAvailableTrue();
        long pendingRequests = requestRepository.countByStatus("PENDING");
        long fulfilledRequests = requestRepository.countByStatus("FULFILLED");

        return Map.of(
                "totalRegisteredDonors", totalDonors,
                "availableDonors", availableDonors,
                "pendingRequests", pendingRequests,
                "fulfilledRequests", fulfilledRequests
        );
    }

    private BloodDonor maskDonor(BloodDonor d) {
        BloodDonor copy = new BloodDonor();
        copy.setId(d.getId());
        copy.setFullName(maskName(d.getFullName()));
        copy.setBloodGroup(d.getBloodGroup());
        copy.setCity(d.getCity());
        copy.setLatitude(d.getLatitude());
        copy.setLongitude(d.getLongitude());
        copy.setAvailable(d.isAvailable());
        copy.setTotalDonations(d.getTotalDonations());
        copy.setLastDonationDate(d.getLastDonationDate());
        copy.setVerified(d.isVerified());
        copy.setRegisteredAt(d.getRegisteredAt());

        // Mask phone
        if (d.getPhone() != null && d.getPhone().length() >= 6) {
            copy.setPhone(d.getPhone().substring(0, 3) + "****" + d.getPhone().substring(d.getPhone().length() - 2));
        } else {
            copy.setPhone("****");
        }
        // Mask email
        copy.setEmail("****@protected.jeevansetu");

        return copy;
    }

    private String maskName(String name) {
        if (name == null || name.isBlank()) return "Anonymous Donor";
        String[] parts = name.split(" ");
        if (parts.length > 1) {
            return parts[0] + " " + parts[1].charAt(0) + ".";
        }
        return name.charAt(0) + "***";
    }
}
