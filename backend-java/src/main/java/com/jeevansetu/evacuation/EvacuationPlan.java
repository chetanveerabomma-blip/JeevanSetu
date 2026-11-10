package com.jeevansetu.evacuation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "evacuation_plans")
public class EvacuationPlan {
    @Id
    private String id;
    private String planName;
    private String affectedAreaName;
    private double originLatitude;
    private double originLongitude;
    private double affectedRadiusMeters;
    private String primarySafeZoneId;
    private String primarySafeZoneName;
    private String secondarySafeZoneId;
    private String secondarySafeZoneName;
    private double estimatedDistanceKm;
    private int estimatedWalkMinutes;
    private List<List<Double>> primaryRouteCoordinates = new ArrayList<>(); // [[lng, lat], ...]
    private List<List<Double>> alternativeRouteCoordinates = new ArrayList<>();
    private List<String> evacuationInstructions = new ArrayList<>();
    private List<String> avoidedBlockedRoads = new ArrayList<>();
    private boolean active = true;
    private Instant generatedAt = Instant.now();

    public EvacuationPlan() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public String getAffectedAreaName() { return affectedAreaName; }
    public void setAffectedAreaName(String affectedAreaName) { this.affectedAreaName = affectedAreaName; }

    public double getOriginLatitude() { return originLatitude; }
    public void setOriginLatitude(double originLatitude) { this.originLatitude = originLatitude; }

    public double getOriginLongitude() { return originLongitude; }
    public void setOriginLongitude(double originLongitude) { this.originLongitude = originLongitude; }

    public double getAffectedRadiusMeters() { return affectedRadiusMeters; }
    public void setAffectedRadiusMeters(double affectedRadiusMeters) { this.affectedRadiusMeters = affectedRadiusMeters; }

    public String getPrimarySafeZoneId() { return primarySafeZoneId; }
    public void setPrimarySafeZoneId(String primarySafeZoneId) { this.primarySafeZoneId = primarySafeZoneId; }

    public String getPrimarySafeZoneName() { return primarySafeZoneName; }
    public void setPrimarySafeZoneName(String primarySafeZoneName) { this.primarySafeZoneName = primarySafeZoneName; }

    public String getSecondarySafeZoneId() { return secondarySafeZoneId; }
    public void setSecondarySafeZoneId(String secondarySafeZoneId) { this.secondarySafeZoneId = secondarySafeZoneId; }

    public String getSecondarySafeZoneName() { return secondarySafeZoneName; }
    public void setSecondarySafeZoneName(String secondarySafeZoneName) { this.secondarySafeZoneName = secondarySafeZoneName; }

    public double getEstimatedDistanceKm() { return estimatedDistanceKm; }
    public void setEstimatedDistanceKm(double estimatedDistanceKm) { this.estimatedDistanceKm = estimatedDistanceKm; }

    public int getEstimatedWalkMinutes() { return estimatedWalkMinutes; }
    public void setEstimatedWalkMinutes(int estimatedWalkMinutes) { this.estimatedWalkMinutes = estimatedWalkMinutes; }

    public List<List<Double>> getPrimaryRouteCoordinates() { return primaryRouteCoordinates; }
    public void setPrimaryRouteCoordinates(List<List<Double>> primaryRouteCoordinates) { this.primaryRouteCoordinates = primaryRouteCoordinates; }

    public List<List<Double>> getAlternativeRouteCoordinates() { return alternativeRouteCoordinates; }
    public void setAlternativeRouteCoordinates(List<List<Double>> alternativeRouteCoordinates) { this.alternativeRouteCoordinates = alternativeRouteCoordinates; }

    public List<String> getEvacuationInstructions() { return evacuationInstructions; }
    public void setEvacuationInstructions(List<String> evacuationInstructions) { this.evacuationInstructions = evacuationInstructions; }

    public List<String> getAvoidedBlockedRoads() { return avoidedBlockedRoads; }
    public void setAvoidedBlockedRoads(List<String> avoidedBlockedRoads) { this.avoidedBlockedRoads = avoidedBlockedRoads; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }
}
