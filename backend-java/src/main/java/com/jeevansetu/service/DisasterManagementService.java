package com.jeevansetu.service;

import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.DisasterEvent;
import com.jeevansetu.model.ReliefCenter;
import com.jeevansetu.model.ReliefRequest;
import com.jeevansetu.repository.DisasterEventRepository;
import com.jeevansetu.repository.ReliefCenterRepository;
import com.jeevansetu.repository.ReliefRequestRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DisasterManagementService {

    private final DisasterEventRepository disasterRepository;
    private final ReliefCenterRepository reliefCenterRepository;
    private final ReliefRequestRepository reliefRequestRepository;
    private final AuditLogService auditLogService;
    private final WebSocketEventPublisher eventPublisher;

    public DisasterManagementService(DisasterEventRepository disasterRepository,
                                     ReliefCenterRepository reliefCenterRepository,
                                     ReliefRequestRepository reliefRequestRepository,
                                     AuditLogService auditLogService,
                                     WebSocketEventPublisher eventPublisher) {
        this.disasterRepository = disasterRepository;
        this.reliefCenterRepository = reliefCenterRepository;
        this.reliefRequestRepository = reliefRequestRepository;
        this.auditLogService = auditLogService;
        this.eventPublisher = eventPublisher;
    }

    public DisasterEvent createDisasterEvent(DisasterEvent event, String actorName) {
        if (event.getEventCode() == null || event.getEventCode().isBlank()) {
            event.setEventCode("DIS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        event.setStatus("ACTIVE");
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());

        DisasterEvent saved = disasterRepository.save(event);
        auditLogService.log("CREATE_DISASTER_EVENT", saved.getId(), "DISASTER_EVENT",
                "Created event: " + saved.getTitle() + " (" + saved.getDisasterType() + ")");

        eventPublisher.publish("DISASTER_EVENT_ACTIVE", Map.of(
                "eventId", saved.getId(),
                "eventCode", saved.getEventCode(),
                "title", saved.getTitle(),
                "disasterType", saved.getDisasterType(),
                "severity", saved.getSeverity()
        ));

        return saved;
    }

    public List<DisasterEvent> getAllEvents(String status) {
        if (status != null && !status.isBlank()) {
            return disasterRepository.findByStatus(status, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        return disasterRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public DisasterEvent getEventById(String id) {
        return disasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disaster event not found: " + id));
    }

    public DisasterEvent updateEventStatus(String id, String status, String actorName) {
        DisasterEvent event = getEventById(id);
        event.setStatus(status);
        if ("RESOLVED".equalsIgnoreCase(status)) {
            event.setResolvedAt(Instant.now());
        }
        event.setUpdatedAt(Instant.now());
        DisasterEvent saved = disasterRepository.save(event);

        auditLogService.log("UPDATE_DISASTER_STATUS", saved.getId(), "DISASTER_EVENT",
                "Status changed to " + status + " by " + actorName);

        eventPublisher.publish("DISASTER_STATUS_UPDATE", Map.of(
                "eventId", saved.getId(),
                "status", status
        ));

        return saved;
    }

    // Relief Centers
    public ReliefCenter registerReliefCenter(ReliefCenter center, String actorName) {
        center.setLastUpdated(Instant.now());
        ReliefCenter saved = reliefCenterRepository.save(center);

        // Update center count in disaster event
        if (center.getDisasterEventId() != null) {
            disasterRepository.findById(center.getDisasterEventId()).ifPresent(e -> {
                e.setReliefCentersCount(e.getReliefCentersCount() + 1);
                disasterRepository.save(e);
            });
        }

        auditLogService.log("REGISTER_RELIEF_CENTER", saved.getId(), "RELIEF_CENTER", "Registered center: " + saved.getName());
        return saved;
    }

    public List<ReliefCenter> getReliefCenters(String disasterEventId) {
        if (disasterEventId != null && !disasterEventId.isBlank()) {
            return reliefCenterRepository.findByDisasterEventId(disasterEventId);
        }
        return reliefCenterRepository.findAll();
    }

    public ReliefCenter updateCenterOccupancy(String centerId, int occupied, int foodDays, int waterLiters, int medicalKits) {
        ReliefCenter center = reliefCenterRepository.findById(centerId)
                .orElseThrow(() -> new ResourceNotFoundException("Relief center not found: " + centerId));

        center.setCurrentOccupancy(occupied);
        center.setFoodSupplyDays(foodDays);
        center.setWaterSupplyLiters(waterLiters);
        center.setMedicalKitsAvailable(medicalKits);
        center.setStatus(occupied >= center.getTotalCapacity() ? "FULL" : "OPERATIONAL");
        center.setLastUpdated(Instant.now());

        return reliefCenterRepository.save(center);
    }

    // Relief Requests & Supply Distribution
    public ReliefRequest submitReliefRequest(ReliefRequest request) {
        request.setStatus("PENDING");
        request.setCreatedAt(Instant.now());
        ReliefRequest saved = reliefRequestRepository.save(request);

        eventPublisher.publish("RELIEF_REQUEST_CREATED", Map.of(
                "requestId", saved.getId(),
                "requestType", saved.getRequestType(),
                "quantity", saved.getQuantityRequested(),
                "urgency", saved.getUrgency()
        ));

        return saved;
    }

    public ReliefRequest coordinateSupplyDistribution(String requestId, int quantityDispatched, String statusNotes, String coordinatorName) {
        ReliefRequest req = reliefRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Relief request not found: " + requestId));

        req.setQuantityDispatched(quantityDispatched);
        if (quantityDispatched >= req.getQuantityRequested()) {
            req.setStatus("FULFILLED");
            req.setFulfilledAt(Instant.now());
        } else {
            req.setStatus("IN_TRANSIT");
        }

        ReliefRequest saved = reliefRequestRepository.save(req);
        auditLogService.log("DISTRIBUTE_RELIEF_SUPPLY", saved.getId(), "RELIEF_REQUEST",
                "Dispatched " + quantityDispatched + " of " + req.getRequestType() + " by " + coordinatorName);

        return saved;
    }

    public List<ReliefRequest> getReliefRequests(String disasterEventId) {
        if (disasterEventId != null && !disasterEventId.isBlank()) {
            return reliefRequestRepository.findByDisasterEventId(disasterEventId, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        return reliefRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Map<String, Object> getDisasterOverview() {
        long activeEvents = disasterRepository.countByStatus("ACTIVE");
        long totalReliefCenters = reliefCenterRepository.count();
        long operationalCenters = reliefCenterRepository.countByStatus("OPERATIONAL");
        long pendingRequests = reliefRequestRepository.countByStatus("PENDING");

        return Map.of(
                "activeDisasters", activeEvents,
                "totalReliefCenters", totalReliefCenters,
                "operationalCenters", operationalCenters,
                "pendingSupplyRequests", pendingRequests,
                "timestamp", Instant.now()
        );
    }
}
