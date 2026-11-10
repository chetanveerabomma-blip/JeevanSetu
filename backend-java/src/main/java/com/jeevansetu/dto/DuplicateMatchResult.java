package com.jeevansetu.dto;

public class DuplicateMatchResult {
    private String existingIncidentId;
    private String incidentCode;
    private String category;
    private String status;
    private double matchScore; // 0 to 100%
    private double distanceMeters;
    private long minutesApart;
    private String reason;

    public DuplicateMatchResult() {}

    public DuplicateMatchResult(String existingIncidentId, String incidentCode, String category, String status, double matchScore, double distanceMeters, long minutesApart, String reason) {
        this.existingIncidentId = existingIncidentId;
        this.incidentCode = incidentCode;
        this.category = category;
        this.status = status;
        this.matchScore = matchScore;
        this.distanceMeters = distanceMeters;
        this.minutesApart = minutesApart;
        this.reason = reason;
    }

    public String getExistingIncidentId() { return existingIncidentId; }
    public void setExistingIncidentId(String existingIncidentId) { this.existingIncidentId = existingIncidentId; }

    public String getIncidentCode() { return incidentCode; }
    public void setIncidentCode(String incidentCode) { this.incidentCode = incidentCode; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

    public double getDistanceMeters() { return distanceMeters; }
    public void setDistanceMeters(double distanceMeters) { this.distanceMeters = distanceMeters; }

    public long getMinutesApart() { return minutesApart; }
    public void setMinutesApart(long minutesApart) { this.minutesApart = minutesApart; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
