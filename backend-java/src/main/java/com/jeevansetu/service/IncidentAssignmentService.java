package com.jeevansetu.service;

import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.EmergencyStatus;
import com.jeevansetu.repository.EmergencyRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class IncidentAssignmentService {

    private final EmergencyRepository emergencyRepository;
    private final IncidentTimelineService timelineService;
    private final AuditLogService auditLogService;
    private final WebSocketEventPublisher eventPublisher;

    public IncidentAssignmentService(EmergencyRepository emergencyRepository,
                                    IncidentTimelineService timelineService,
                                    AuditLogService auditLogService,
                                    WebSocketEventPublisher eventPublisher) {
        this.emergencyRepository = emergencyRepository;
        this.timelineService = timelineService;
        this.auditLogService = auditLogService;
        this.eventPublisher = eventPublisher;
    }

    public Emergency assignAgency(String incidentId, String agencyName, String dispatcherName, String remarks) {
        Emergency emergency = emergencyRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Emergency not found: " + incidentId));

        emergency.setAssignedAgency(agencyName);
        if (emergency.getStatus() == EmergencyStatus.REPORTED) {
            emergency.setStatus(EmergencyStatus.PROCESSING);
        }
        if (remarks != null && !remarks.isBlank()) {
            emergency.setDispatcherRemarks(remarks);
        }
        emergency.setUpdatedAt(Instant.now());

        Emergency saved = emergencyRepository.save(emergency);

        // Record Timeline
        timelineService.recordEvent(saved.getId(), saved.getIncidentCode(), "AGENCY_ASSIGNED",
                "Assigned to agency: " + agencyName + (remarks != null && !remarks.isBlank() ? " | Notes: " + remarks : ""),
                dispatcherName != null ? dispatcherName : "Dispatcher", "DISPATCHER");

        // Record Audit
        auditLogService.log("ASSIGN_AGENCY", saved.getId(), "EMERGENCY",
                "Assigned agency: " + agencyName + " by " + dispatcherName);

        // Real-time broadcast
        eventPublisher.publish("INCIDENT_ASSIGNMENT", Map.of(
                "incidentId", saved.getId(),
                "incidentCode", saved.getIncidentCode(),
                "assignedAgency", agencyName,
                "status", saved.getStatus().name()
        ));

        return saved;
    }

    public Emergency addDispatcherRemark(String incidentId, String dispatcherName, String remarks) {
        Emergency emergency = emergencyRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Emergency not found: " + incidentId));

        emergency.setDispatcherRemarks(remarks);
        emergency.setUpdatedAt(Instant.now());
        Emergency saved = emergencyRepository.save(emergency);

        timelineService.recordEvent(saved.getId(), saved.getIncidentCode(), "REMARK_ADDED",
                "Dispatcher remark: " + remarks,
                dispatcherName != null ? dispatcherName : "Dispatcher", "DISPATCHER");

        auditLogService.log("ADD_REMARK", saved.getId(), "EMERGENCY", "Remark added by " + dispatcherName);

        return saved;
    }
}
