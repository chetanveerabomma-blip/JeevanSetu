package com.jeevansetu.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssistantResponse {
    private String text;
    private String intent;
    private String category;
    private String priority;
    private double confidenceScore;
    private Map<String, Object> extractedInfo = new HashMap<>();
    private List<String> recommendedResources = new ArrayList<>();
    private List<String> missingInfo = new ArrayList<>();
    private List<String> suggestedActions = new ArrayList<>();

    // Structured contextual payloads
    private Emergency emergencyDraft;
    private List<Hospital> hospitals = new ArrayList<>();
    private List<ResponderUnit> ambulances = new ArrayList<>();
    private Emergency incidentStatus;
    private String guidelineContent;
    private Instant timestamp = Instant.now();

    public AssistantResponse() {}

    public AssistantResponse(String text, String intent) {
        this.text = text;
        this.intent = intent;
        this.timestamp = Instant.now();
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public Map<String, Object> getExtractedInfo() { return extractedInfo; }
    public void setExtractedInfo(Map<String, Object> extractedInfo) { this.extractedInfo = extractedInfo; }

    public List<String> getRecommendedResources() { return recommendedResources; }
    public void setRecommendedResources(List<String> recommendedResources) { this.recommendedResources = recommendedResources; }

    public List<String> getMissingInfo() { return missingInfo; }
    public void setMissingInfo(List<String> missingInfo) { this.missingInfo = missingInfo; }

    public List<String> getSuggestedActions() { return suggestedActions; }
    public void setSuggestedActions(List<String> suggestedActions) { this.suggestedActions = suggestedActions; }

    public Emergency getEmergencyDraft() { return emergencyDraft; }
    public void setEmergencyDraft(Emergency emergencyDraft) { this.emergencyDraft = emergencyDraft; }

    public List<Hospital> getHospitals() { return hospitals; }
    public void setHospitals(List<Hospital> hospitals) { this.hospitals = hospitals; }

    public List<ResponderUnit> getAmbulances() { return ambulances; }
    public void setAmbulances(List<ResponderUnit> ambulances) { this.ambulances = ambulances; }

    public Emergency getIncidentStatus() { return incidentStatus; }
    public void setIncidentStatus(Emergency incidentStatus) { this.incidentStatus = incidentStatus; }

    public String getGuidelineContent() { return guidelineContent; }
    public void setGuidelineContent(String guidelineContent) { this.guidelineContent = guidelineContent; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
