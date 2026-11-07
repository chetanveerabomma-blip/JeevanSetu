package com.jeevansetu.service;

import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.AgencyMessage;
import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.Role;
import com.jeevansetu.repository.AgencyMessageRepository;
import com.jeevansetu.repository.EmergencyRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AgencyCommsService {

    private static final Logger logger = LoggerFactory.getLogger(AgencyCommsService.class);

    private final AgencyMessageRepository agencyMessageRepository;
    private final EmergencyRepository emergencyRepository;
    private final WebSocketEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    public AgencyCommsService(
            AgencyMessageRepository agencyMessageRepository,
            EmergencyRepository emergencyRepository,
            WebSocketEventPublisher eventPublisher,
            AuditLogService auditLogService) {
        this.agencyMessageRepository = agencyMessageRepository;
        this.emergencyRepository = emergencyRepository;
        this.eventPublisher = eventPublisher;
        this.auditLogService = auditLogService;
    }

    public AgencyMessage postMessage(String incidentId, String message, String priority,
                                     String senderEmail, String senderName, Role senderRole, String agencyType) {
        Emergency emergency = emergencyRepository.findById(incidentId)
                .orElseGet(() -> emergencyRepository.findByIncidentCode(incidentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Emergency not found for ID or Code: " + incidentId)));

        AgencyMessage msg = new AgencyMessage(
                emergency.getId(),
                emergency.getIncidentCode(),
                senderEmail != null ? senderEmail : "system@jeevansetu.gov.in",
                senderName != null ? senderName : "Responder",
                senderRole != null ? senderRole : Role.CONTROL_CENTER,
                agencyType != null ? agencyType : "TACTICAL_COMMS",
                message,
                priority != null ? priority : "ROUTINE"
        );
        msg.setTimestamp(Instant.now());

        AgencyMessage saved = agencyMessageRepository.save(msg);

        // Broadcast real-time message to WebSocket channels
        eventPublisher.publishAgencyMessage(emergency.getId(), saved);

        // Audit log
        auditLogService.logAction(
                senderEmail != null ? senderEmail : "system",
                senderRole != null ? senderRole.name() : "DISPATCH",
                "AGENCY_COMMS_TRANSMIT",
                emergency.getId(),
                emergency.getIncidentCode(),
                String.format("[%s] Transmitted inter-agency message: %s", agencyType, message),
                null,
                priority
        );

        return saved;
    }

    public List<AgencyMessage> getMessagesForIncident(String incidentIdOrCode) {
        if (incidentIdOrCode.toUpperCase().startsWith("JS-")) {
            return agencyMessageRepository.findByIncidentCode(incidentIdOrCode, Sort.by(Sort.Direction.ASC, "timestamp"));
        }
        return agencyMessageRepository.findByIncidentId(incidentIdOrCode, Sort.by(Sort.Direction.ASC, "timestamp"));
    }

    public List<AgencyMessage> getAllRecentMessages(int limit) {
        return agencyMessageRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"))
                .stream().limit(limit > 0 ? limit : 50).toList();
    }
}
