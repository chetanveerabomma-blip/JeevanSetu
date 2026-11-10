package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "volunteer_tasks")
public class VolunteerTask {
    @Id
    private String id;

    @Indexed
    private String taskCode;

    @Indexed
    private String incidentId;
    private String incidentCode;

    private String title;
    private String description;
    private String requiredSkill;
    private String locationAddress;
    private double latitude;
    private double longitude;

    @Indexed
    private String assignedVolunteerId;
    private String assignedVolunteerName;

    @Indexed
    private String status = "PROPOSED"; // PROPOSED, APPROVED, ACCEPTED, REJECTED, COMPLETED, CANCELLED

    private boolean dispatcherApproved = false;
    private String approvedByDispatcher;

    private Instant createdAt = Instant.now();
    private Instant acceptedAt;
    private Instant completedAt;
    private String completionNotes;

    public VolunteerTask() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTaskCode() { return taskCode; }
    public void setTaskCode(String taskCode) { this.taskCode = taskCode; }

    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequiredSkill() { return requiredSkill; }
    public void setRequiredSkill(String requiredSkill) { this.requiredSkill = requiredSkill; }

    public String getLocationAddress() { return locationAddress; }
    public void setLocationAddress(String locationAddress) { this.locationAddress = locationAddress; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getAssignedVolunteerId() { return assignedVolunteerId; }
    public void setAssignedVolunteerId(String assignedVolunteerId) { this.assignedVolunteerId = assignedVolunteerId; }

    public String getAssignedVolunteerName() { return assignedVolunteerName; }
    public void setAssignedVolunteerName(String assignedVolunteerName) { this.assignedVolunteerName = assignedVolunteerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isDispatcherApproved() { return dispatcherApproved; }
    public void setDispatcherApproved(boolean dispatcherApproved) { this.dispatcherApproved = dispatcherApproved; }

    public String getApprovedByDispatcher() { return approvedByDispatcher; }
    public void setApprovedByDispatcher(String approvedByDispatcher) { this.approvedByDispatcher = approvedByDispatcher; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Instant acceptedAt) { this.acceptedAt = acceptedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public String getCompletionNotes() { return completionNotes; }
    public void setCompletionNotes(String completionNotes) { this.completionNotes = completionNotes; }
}
