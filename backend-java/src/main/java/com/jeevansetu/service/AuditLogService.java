package com.jeevansetu.service;

import com.jeevansetu.model.AuditLog;
import com.jeevansetu.repository.AuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String email, String role, String action, String incidentId, String incidentCode, String details, String oldStatus, String newStatus) {
        AuditLog log = new AuditLog(
                email != null ? email : "SYSTEM",
                role != null ? role : "SYSTEM",
                action,
                incidentId,
                incidentCode,
                details,
                oldStatus,
                newStatus
        );
        auditLogRepository.save(log);
    }

    public void log(String action, String incidentId, String entityType, String details) {
        logAction("SYSTEM", entityType, action, incidentId, incidentId, details, null, null);
    }

    public List<AuditLog> getRecentLogs(int limit) {
        return auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, Math.min(100, limit)));
    }

    public List<AuditLog> getLogsForIncident(String incidentId) {
        return auditLogRepository.findByIncidentIdOrderByTimestampDesc(incidentId);
    }
}
