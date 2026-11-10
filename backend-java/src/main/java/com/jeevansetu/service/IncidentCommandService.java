package com.jeevansetu.service;

import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.EmergencyPriority;
import com.jeevansetu.model.EmergencyStatus;
import com.jeevansetu.model.EmergencyType;
import com.jeevansetu.repository.EmergencyRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IncidentCommandService {

    private final EmergencyRepository emergencyRepository;
    private final IncidentTimelineService timelineService;
    private final AuditLogService auditLogService;
    private final WebSocketEventPublisher eventPublisher;

    public IncidentCommandService(EmergencyRepository emergencyRepository,
                                  IncidentTimelineService timelineService,
                                  AuditLogService auditLogService,
                                  WebSocketEventPublisher eventPublisher) {
        this.emergencyRepository = emergencyRepository;
        this.timelineService = timelineService;
        this.auditLogService = auditLogService;
        this.eventPublisher = eventPublisher;
    }

    public List<Emergency> getFilteredIncidents(EmergencyStatus status,
                                                EmergencyPriority priority,
                                                EmergencyType type,
                                                String agency,
                                                Boolean escalated,
                                                Instant startDate,
                                                Instant endDate) {
        List<Emergency> all = emergencyRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        return all.stream()
                .filter(e -> status == null || e.getStatus() == status)
                .filter(e -> priority == null || e.getPriority() == priority)
                .filter(e -> type == null || e.getType() == type)
                .filter(e -> agency == null || agency.isBlank() || (e.getAssignedAgency() != null && e.getAssignedAgency().equalsIgnoreCase(agency)))
                .filter(e -> escalated == null || e.isEscalated() == escalated)
                .filter(e -> startDate == null || (e.getCreatedAt() != null && !e.getCreatedAt().isBefore(startDate)))
                .filter(e -> endDate == null || (e.getCreatedAt() != null && !e.getCreatedAt().isAfter(endDate)))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getCommandOverview() {
        long total = emergencyRepository.count();
        long active = emergencyRepository.findByStatusNotIn(List.of(EmergencyStatus.RESOLVED, EmergencyStatus.CANCELLED), Sort.unsorted()).size();
        long critical = emergencyRepository.countByPriority(EmergencyPriority.CRITICAL);
        long resolved = emergencyRepository.countByStatus(EmergencyStatus.RESOLVED);
        long escalated = emergencyRepository.findByEscalatedTrueAndStatusNotIn(List.of(EmergencyStatus.RESOLVED, EmergencyStatus.CANCELLED)).size();

        return Map.of(
                "totalIncidents", total,
                "activeIncidents", active,
                "criticalIncidents", critical,
                "resolvedIncidents", resolved,
                "escalatedIncidents", escalated,
                "systemStatus", "OPERATIONAL",
                "timestamp", Instant.now()
        );
    }

    public Emergency escalateIncident(String incidentId, String reason, String dispatcherName) {
        Emergency emergency = emergencyRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        emergency.setEscalated(true);
        emergency.setEscalatedAt(Instant.now());
        emergency.setEscalationLevel(emergency.getEscalationLevel() + 1);
        emergency.setEscalationAcknowledged(false);
        emergency.setUpdatedAt(Instant.now());

        Emergency saved = emergencyRepository.save(emergency);

        timelineService.recordEvent(saved.getId(), saved.getIncidentCode(), "ESCALATED",
                "Incident escalated to Level " + saved.getEscalationLevel() + " | Reason: " + reason,
                dispatcherName != null ? dispatcherName : "System", "DISPATCHER");

        auditLogService.log("ESCALATE_INCIDENT", saved.getId(), "EMERGENCY", "Reason: " + reason);

        eventPublisher.publish("INCIDENT_ESCALATION", Map.of(
                "incidentId", saved.getId(),
                "incidentCode", saved.getIncidentCode(),
                "priority", saved.getPriority().name(),
                "escalationLevel", saved.getEscalationLevel(),
                "reason", reason
        ));

        return saved;
    }

    public Emergency acknowledgeEscalation(String incidentId, String dispatcherName) {
        Emergency emergency = emergencyRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        emergency.setEscalationAcknowledged(true);
        emergency.setEscalationAcknowledgedBy(dispatcherName);
        emergency.setUpdatedAt(Instant.now());

        Emergency saved = emergencyRepository.save(emergency);

        timelineService.recordEvent(saved.getId(), saved.getIncidentCode(), "ACKNOWLEDGED",
                "Escalation acknowledged by: " + dispatcherName, dispatcherName, "DISPATCHER");

        auditLogService.log("ACKNOWLEDGE_ESCALATION", saved.getId(), "EMERGENCY", "Acknowledged by " + dispatcherName);

        eventPublisher.publish("ESCALATION_ACKNOWLEDGED", Map.of(
                "incidentId", saved.getId(),
                "incidentCode", saved.getIncidentCode(),
                "acknowledgedBy", dispatcherName
        ));

        return saved;
    }

    public Emergency resolveIncident(String incidentId, String resolutionNotes, String dispatcherName) {
        Emergency emergency = emergencyRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        EmergencyStatus prevStatus = emergency.getStatus();
        emergency.setStatus(EmergencyStatus.RESOLVED);
        emergency.setResolvedAt(Instant.now());
        emergency.setUpdatedAt(Instant.now());
        if (resolutionNotes != null && !resolutionNotes.isBlank()) {
            emergency.setAdditionalInfo((emergency.getAdditionalInfo() != null ? emergency.getAdditionalInfo() + " | " : "") + "Resolution: " + resolutionNotes);
        }

        Emergency saved = emergencyRepository.save(emergency);

        timelineService.recordStatusChange(saved.getId(), saved.getIncidentCode(),
                prevStatus.name(), EmergencyStatus.RESOLVED.name(),
                dispatcherName != null ? dispatcherName : "Dispatcher", "DISPATCHER", resolutionNotes);

        auditLogService.log("RESOLVE_INCIDENT", saved.getId(), "EMERGENCY", "Resolved with notes: " + resolutionNotes);

        eventPublisher.publish("INCIDENT_RESOLVED", Map.of(
                "incidentId", saved.getId(),
                "incidentCode", saved.getIncidentCode(),
                "resolvedAt", saved.getResolvedAt()
        ));

        return saved;
    }
}
