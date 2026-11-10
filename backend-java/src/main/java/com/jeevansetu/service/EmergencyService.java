package com.jeevansetu.service;

import com.jeevansetu.algorithm.EmergencyPriorityQueue;
import com.jeevansetu.algorithm.PriorityEngine;
import com.jeevansetu.algorithm.RequiredResourceEngine;
import com.jeevansetu.dto.AIClassificationResponse;
import com.jeevansetu.dto.EmergencyReportRequest;
import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.*;
import com.jeevansetu.repository.EmergencyRepository;
import com.jeevansetu.repository.IncidentUpdateRepository;
import com.jeevansetu.repository.NotificationRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class EmergencyService {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyService.class);
    private static final Random random = new Random();

    private final EmergencyRepository emergencyRepository;
    private final IncidentUpdateRepository incidentUpdateRepository;
    private final NotificationRepository notificationRepository;
    private final PriorityEngine priorityEngine;
    private final RequiredResourceEngine requiredResourceEngine;
    private final EmergencyPriorityQueue priorityQueue;
    private final AiIntegrationService aiIntegrationService;
    private final WebSocketEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    public EmergencyService(
            EmergencyRepository emergencyRepository,
            IncidentUpdateRepository incidentUpdateRepository,
            NotificationRepository notificationRepository,
            PriorityEngine priorityEngine,
            RequiredResourceEngine requiredResourceEngine,
            EmergencyPriorityQueue priorityQueue,
            AiIntegrationService aiIntegrationService,
            WebSocketEventPublisher eventPublisher,
            AuditLogService auditLogService) {
        this.emergencyRepository = emergencyRepository;
        this.incidentUpdateRepository = incidentUpdateRepository;
        this.notificationRepository = notificationRepository;
        this.priorityEngine = priorityEngine;
        this.requiredResourceEngine = requiredResourceEngine;
        this.priorityQueue = priorityQueue;
        this.aiIntegrationService = aiIntegrationService;
        this.eventPublisher = eventPublisher;
        this.auditLogService = auditLogService;
    }

    public Emergency reportEmergency(EmergencyReportRequest req, String userId, String userEmail, String userName, String userPhone) {
        // Idempotency check for offline synchronization
        if (req.getClientGeneratedId() != null && !req.getClientGeneratedId().isBlank()) {
            Optional<Emergency> existing = emergencyRepository.findByClientGeneratedId(req.getClientGeneratedId());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Emergency emergency = new Emergency();
        emergency.setClientGeneratedId(req.getClientGeneratedId());
        emergency.setIncidentCode(generateIncidentCode());
        emergency.setType(req.getType() != null ? req.getType() : EmergencyType.MEDICAL);
        emergency.setDescription(req.getDescription());
        emergency.setLatitude(req.getLatitude());
        emergency.setLongitude(req.getLongitude());
        emergency.setAddress(req.getAddress() != null ? req.getAddress() : "Location coordinates captured");
        emergency.setPeopleAffected(req.getPeopleAffected());
        emergency.setInjuredCount(req.getInjuredCount());
        emergency.setUnconscious(req.isUnconscious());
        emergency.setSevereBleeding(req.isSevereBleeding());
        emergency.setFireInvolved(req.isFireInvolved());
        emergency.setTrapped(req.isTrapped());
        emergency.setAdditionalInfo(req.getAdditionalInfo());

        emergency.setReporterId(userId);
        emergency.setReporterName(userName != null ? userName : "Citizen Reporter");
        emergency.setReporterPhone(userPhone != null ? userPhone : "N/A");

        // 1. Authoritative Java Priority Engine calculation
        EmergencyPriority priority = priorityEngine.determinePriority(emergency);
        emergency.setPriority(priority);
        emergency.setStatus(EmergencyStatus.REPORTED);

        // 2. Required Resources Calculation
        List<ResourceType> required = requiredResourceEngine.determineRequiredResources(emergency);
        emergency.setRequiredResources(required);

        // Save to MongoDB
        Emergency saved = emergencyRepository.save(emergency);

        // 3. Enqueue into thread-safe Java PriorityQueue
        priorityQueue.add(saved);

        // 4. Initial Timeline entries
        createTimelineEntry(saved.getId(), saved.getIncidentCode(), "REPORTED",
                "Emergency reported by citizen. Coordinates and triage data verified.", "CITIZEN", saved.getReporterName());
        createTimelineEntry(saved.getId(), saved.getIncidentCode(), "PRIORITY_CALCULATED",
                String.format("Priority Engine computed triage rating: %s. Identified required agencies: %s",
                        priority, required), "SYSTEM", "JeevanSetu Core Engine");

        // 5. Create Command Center notification
        Notification notif = new Notification(
                null,
                Role.CONTROL_CENTER,
                "🚨 New " + priority + " Emergency: " + saved.getIncidentCode(),
                String.format("%s at %s (%d injured)", saved.getType(), saved.getAddress(), saved.getInjuredCount()),
                saved.getId(),
                saved.getIncidentCode(),
                "EMERGENCY_ALERT"
        );
        notificationRepository.save(notif);

        // 6. Broadcast Real-time WebSocket Event
        eventPublisher.publishEmergencyCreated(saved);
        eventPublisher.publishNotification(notif);

        // 7. Audit Log
        auditLogService.logAction(
                userEmail,
                "CITIZEN",
                "EMERGENCY_REPORTED",
                saved.getId(),
                saved.getIncidentCode(),
                String.format("Reported %s emergency with priority %s", saved.getType(), priority),
                null,
                "REPORTED"
        );

        // 8. Asynchronously invoke Python AI service for assistive NLP analysis without blocking
        CompletableFuture.runAsync(() -> {
            try {
                AIClassificationResponse ai = aiIntegrationService.classifyEmergency(saved.getDescription());
                String summary = aiIntegrationService.summarizeIncident(
                        saved.getIncidentCode(), saved.getDescription(), saved.getInjuredCount(), saved.isUnconscious()
                );
                saved.setAiCategory(ai.getCategory());
                saved.setAiSeverity(ai.getSeverity());
                saved.setAiSummary(summary);
                emergencyRepository.save(saved);
                eventPublisher.publishEmergencyUpdated(saved);
            } catch (Exception e) {
                logger.debug("Async AI NLP enrichment skipped: {}", e.getMessage());
            }
        });

        return saved;
    }

    public Emergency reportSosEmergency(double latitude, double longitude, String address, String reporterEmail, String reporterName, String reporterPhone) {
        EmergencyReportRequest req = new EmergencyReportRequest();
        req.setType(EmergencyType.MEDICAL);
        req.setDescription("🚨 EMERGENCY SOS PANIC TRIGGER: Instant high-priority distress signal activated. Immediate emergency response demanded.");
        req.setLatitude(latitude);
        req.setLongitude(longitude);
        req.setAddress(address != null && !address.isBlank() ? address : "SOS GPS Coordinates: " + latitude + ", " + longitude);
        req.setPeopleAffected(1);
        req.setInjuredCount(1);
        req.setUnconscious(false);
        req.setSevereBleeding(false);
        req.setFireInvolved(false);
        req.setTrapped(false);
        req.setAdditionalInfo("One-touch citizen SOS panic broadcast.");

        Emergency emergency = reportEmergency(req, null, reporterEmail, reporterName != null ? reporterName : "SOS Citizen Caller", reporterPhone);
        emergency.setPriority(EmergencyPriority.CRITICAL);
        emergency.setIncidentCode(emergency.getIncidentCode().replace("JS-", "JS-SOS-"));
        Emergency saved = emergencyRepository.save(emergency);

        createTimelineEntry(saved.getId(), saved.getIncidentCode(), "SOS_PANIC_ALERT",
                "Emergency SOS broadcast received with verified GPS fix. Multi-agency panic protocol engaged.",
                "CITIZEN_SOS", saved.getReporterName());

        eventPublisher.publishSosAlert(saved);
        return saved;
    }

    public List<Emergency> getActiveEmergenciesPriorityOrdered() {
        // First check memory PriorityQueue; if empty, load from MongoDB
        if (priorityQueue.size() == 0) {
            List<Emergency> active = emergencyRepository.findByStatusIn(
                    List.of(EmergencyStatus.REPORTED, EmergencyStatus.PROCESSING, EmergencyStatus.ASSIGNED,
                            EmergencyStatus.EN_ROUTE, EmergencyStatus.ARRIVED, EmergencyStatus.PATIENT_HANDLED,
                            EmergencyStatus.REACHED_HOSPITAL),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
            priorityQueue.addAll(active);
        }
        return priorityQueue.getSortedList();
    }

    public List<Emergency> getAllEmergencies() {
        return emergencyRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Emergency getEmergencyById(String id) {
        return emergencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency not found with id: " + id));
    }

    public Emergency getEmergencyByCode(String code) {
        return emergencyRepository.findByIncidentCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency not found with code: " + code));
    }

    public List<Emergency> getEmergenciesForCitizen(String reporterId) {
        return emergencyRepository.findByReporterId(reporterId, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Emergency updateStatus(String id, EmergencyStatus newStatus, String actorEmail, String actorRole, String note) {
        Emergency emergency = getEmergencyById(id);
        EmergencyStatus oldStatus = emergency.getStatus();
        emergency.setStatus(newStatus);
        emergency.setUpdatedAt(Instant.now());

        if (newStatus == EmergencyStatus.RESOLVED || newStatus == EmergencyStatus.CANCELLED) {
            emergency.setResolvedAt(Instant.now());
            priorityQueue.remove(emergency.getId());
        } else {
            priorityQueue.add(emergency);
        }

        Emergency updated = emergencyRepository.save(emergency);

        createTimelineEntry(
                emergency.getId(),
                emergency.getIncidentCode(),
                newStatus.name(),
                note != null ? note : "Status transitioned from " + oldStatus + " to " + newStatus,
                actorRole,
                actorEmail
        );

        auditLogService.logAction(
                actorEmail,
                actorRole,
                "STATUS_UPDATED",
                emergency.getId(),
                emergency.getIncidentCode(),
                note,
                oldStatus.name(),
                newStatus.name()
        );

        eventPublisher.publishEmergencyUpdated(updated);
        return updated;
    }

    public void createTimelineEntry(String incidentId, String incidentCode, String stage, String message, String actorRole, String actorName) {
        IncidentUpdate update = new IncidentUpdate(incidentId, incidentCode, stage, message, actorRole, actorName);
        incidentUpdateRepository.save(update);
    }

    public List<IncidentUpdate> getIncidentTimeline(String incidentId) {
        return incidentUpdateRepository.findByIncidentIdOrderByTimestampAsc(incidentId);
    }

    private String generateIncidentCode() {
        int currentYear = Year.now().getValue();
        int seq = 100000 + random.nextInt(900000);
        return String.format("JS-%d-%d", currentYear, seq);
    }
}
