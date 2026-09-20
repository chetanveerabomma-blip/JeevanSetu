package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "assignments")
public class Assignment {
    @Id
    private String id;

    @Indexed
    private String incidentId;

    @Indexed
    private String incidentCode;

    @Indexed
    private String resourceId;

    private ResourceType resourceType;
    private String responderName;
    private String responderContact;
    private String unitCode;

    @Indexed
    private ResourceStatus status = ResourceStatus.ASSIGNED; // ASSIGNED, EN_ROUTE, ARRIVED, COMPLETED, CANCELLED

    private double estimatedDistanceKm;
    private int estimatedMinutes;
    private List<double[]> routeCoordinates; // [[lng, lat], ...]
    private String routingSummary;

    private Instant assignedAt = Instant.now();
    private Instant acceptedAt;
    private Instant enRouteAt;
    private Instant arrivedAt;
    private Instant patientHandledAt;
    private Instant hospitalReachedAt;
    private Instant completedAt;

    public Assignment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public ResourceType getResourceType() { return resourceType; }
    public void setResourceType(ResourceType resourceType) { this.resourceType = resourceType; }

    public String getResponderName() { return responderName; }
    public void setResponderName(String responderName) { this.responderName = responderName; }

    public String getResponderContact() { return responderContact; }
    public void setResponderContact(String responderContact) { this.responderContact = responderContact; }

    public String getUnitCode() { return unitCode; }
    public void setUnitCode(String unitCode) { this.unitCode = unitCode; }

    public ResourceStatus getStatus() { return status; }
    public void setStatus(ResourceStatus status) { this.status = status; }

    public double getEstimatedDistanceKm() { return estimatedDistanceKm; }
    public void setEstimatedDistanceKm(double estimatedDistanceKm) { this.estimatedDistanceKm = estimatedDistanceKm; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public List<double[]> getRouteCoordinates() { return routeCoordinates; }
    public void setRouteCoordinates(List<double[]> routeCoordinates) { this.routeCoordinates = routeCoordinates; }

    public String getRoutingSummary() { return routingSummary; }
    public void setRoutingSummary(String routingSummary) { this.routingSummary = routingSummary; }

    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }

    public Instant getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Instant acceptedAt) { this.acceptedAt = acceptedAt; }

    public Instant getEnRouteAt() { return enRouteAt; }
    public void setEnRouteAt(Instant enRouteAt) { this.enRouteAt = enRouteAt; }

    public Instant getArrivedAt() { return arrivedAt; }
    public void setArrivedAt(Instant arrivedAt) { this.arrivedAt = arrivedAt; }

    public Instant getPatientHandledAt() { return patientHandledAt; }
    public void setPatientHandledAt(Instant patientHandledAt) { this.patientHandledAt = patientHandledAt; }

    public Instant getHospitalReachedAt() { return hospitalReachedAt; }
    public void setHospitalReachedAt(Instant hospitalReachedAt) { this.hospitalReachedAt = hospitalReachedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
