package com.jeevansetu.service;

import com.jeevansetu.model.IncidentTimelineEntry;
import com.jeevansetu.repository.IncidentTimelineRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentTimelineService {

    private final IncidentTimelineRepository timelineRepository;

    public IncidentTimelineService(IncidentTimelineRepository timelineRepository) {
        this.timelineRepository = timelineRepository;
    }

    public IncidentTimelineEntry recordEvent(String incidentId, String incidentCode, String eventType, String description, String actorName, String actorRole) {
        IncidentTimelineEntry entry = new IncidentTimelineEntry(incidentId, incidentCode, eventType, description, actorName, actorRole);
        return timelineRepository.save(entry);
    }

    public IncidentTimelineEntry recordStatusChange(String incidentId, String incidentCode, String prevStatus, String newStatus, String actorName, String actorRole, String remarks) {
        IncidentTimelineEntry entry = new IncidentTimelineEntry(incidentId, incidentCode, "STATUS_CHANGE",
                "Status transitioned from " + prevStatus + " to " + newStatus + (remarks != null && !remarks.isBlank() ? " | Remarks: " + remarks : ""),
                actorName, actorRole);
        entry.setPreviousStatus(prevStatus);
        entry.setNewStatus(newStatus);
        return timelineRepository.save(entry);
    }

    public List<IncidentTimelineEntry> getTimeline(String incidentId) {
        return timelineRepository.findByIncidentId(incidentId, Sort.by(Sort.Direction.ASC, "timestamp"));
    }
}
