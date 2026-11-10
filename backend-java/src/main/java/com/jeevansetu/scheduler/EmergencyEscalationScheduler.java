package com.jeevansetu.scheduler;

import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.EmergencyPriority;
import com.jeevansetu.model.EmergencyStatus;
import com.jeevansetu.repository.EmergencyRepository;
import com.jeevansetu.service.IncidentCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class EmergencyEscalationScheduler {

    private static final Logger log = LoggerFactory.getLogger(EmergencyEscalationScheduler.class);

    private final EmergencyRepository emergencyRepository;
    private final IncidentCommandService commandService;

    @Value("${jeevansetu.escalation.critical-threshold-sec:180}")
    private long criticalThresholdSec;

    @Value("${jeevansetu.escalation.high-threshold-sec:600}")
    private long highThresholdSec;

    @Value("${jeevansetu.escalation.medium-threshold-sec:1200}")
    private long mediumThresholdSec;

    @Value("${jeevansetu.escalation.low-threshold-sec:2700}")
    private long lowThresholdSec;

    public EmergencyEscalationScheduler(EmergencyRepository emergencyRepository, IncidentCommandService commandService) {
        this.emergencyRepository = emergencyRepository;
        this.commandService = commandService;
    }

    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    public void scanAndEscalateOverdueEmergencies() {
        Instant now = Instant.now();

        // Scan for REPORTED or unassigned incidents
        List<Emergency> activeIncidents = emergencyRepository.findByStatusIn(
                List.of(EmergencyStatus.REPORTED, EmergencyStatus.PROCESSING),
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        for (Emergency emg : activeIncidents) {
            if (emg.isEscalated() && emg.getEscalationLevel() >= 2) {
                continue; // Already at max escalation level
            }

            Instant created = emg.getCreatedAt() != null ? emg.getCreatedAt() : now;
            long elapsedSec = Duration.between(created, now).getSeconds();
            long threshold = getThresholdForPriority(emg.getPriority());

            if (elapsedSec > threshold) {
                String reason = String.format("Auto-escalated: %s incident pending for %d mins without resource assignment (Threshold: %d mins)",
                        emg.getPriority(), elapsedSec / 60, threshold / 60);

                log.warn("[AUTO-ESCALATION] Incident {} ({}) exceeded threshold of {}s. Escalating.",
                        emg.getIncidentCode(), emg.getPriority(), threshold);

                try {
                    commandService.escalateIncident(emg.getId(), reason, "SYSTEM_ESCALATION_MONITOR");
                } catch (Exception ex) {
                    log.error("Failed to escalate incident {}: {}", emg.getId(), ex.getMessage());
                }
            }
        }
    }

    private long getThresholdForPriority(EmergencyPriority priority) {
        if (priority == null) return highThresholdSec;
        switch (priority) {
            case CRITICAL: return criticalThresholdSec;
            case HIGH: return highThresholdSec;
            case MEDIUM: return mediumThresholdSec;
            case LOW: return lowThresholdSec;
            default: return highThresholdSec;
        }
    }
}
