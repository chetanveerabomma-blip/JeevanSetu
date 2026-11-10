package com.jeevansetu.postincident;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "post_incident_reviews")
public class PostIncidentReview {
    @Id
    private String id;
    private String incidentId;
    private String incidentCode;
    private String incidentType;
    private String priority;
    private String address;
    private Instant reportTime;
    private Instant assignmentTime;
    private Instant resolvedTime;
    private double responseDurationMinutes;
    private List<String> agenciesInvolved = new ArrayList<>();
    private List<String> dispatcherActions = new ArrayList<>();
    private List<String> operationalObservations = new ArrayList<>();
    private List<String> lessonsLearned = new ArrayList<>();
    private String rootCauseSummary;
    private String status = "FINALIZED"; // DRAFT, FINALIZED
    private String reviewedBy;
    private Instant reviewedAt = Instant.now();

    public PostIncidentReview() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Instant getReportTime() { return reportTime; }
    public void setReportTime(Instant reportTime) { this.reportTime = reportTime; }

    public Instant getAssignmentTime() { return assignmentTime; }
    public void setAssignmentTime(Instant assignmentTime) { this.assignmentTime = assignmentTime; }

    public Instant getResolvedTime() { return resolvedTime; }
    public void setResolvedTime(Instant resolvedTime) { this.resolvedTime = resolvedTime; }

    public double getResponseDurationMinutes() { return responseDurationMinutes; }
    public void setResponseDurationMinutes(double responseDurationMinutes) { this.responseDurationMinutes = responseDurationMinutes; }

    public List<String> getAgenciesInvolved() { return agenciesInvolved; }
    public void setAgenciesInvolved(List<String> agenciesInvolved) { this.agenciesInvolved = agenciesInvolved; }

    public List<String> getDispatcherActions() { return dispatcherActions; }
    public void setDispatcherActions(List<String> dispatcherActions) { this.dispatcherActions = dispatcherActions; }

    public List<String> getOperationalObservations() { return operationalObservations; }
    public void setOperationalObservations(List<String> operationalObservations) { this.operationalObservations = operationalObservations; }

    public List<String> getLessonsLearned() { return lessonsLearned; }
    public void setLessonsLearned(List<String> lessonsLearned) { this.lessonsLearned = lessonsLearned; }

    public String getRootCauseSummary() { return rootCauseSummary; }
    public void setRootCauseSummary(String rootCauseSummary) { this.rootCauseSummary = rootCauseSummary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public Instant getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Instant reviewedAt) { this.reviewedAt = reviewedAt; }
}
