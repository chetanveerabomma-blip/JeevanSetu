package com.jeevansetu.dto;

import java.util.List;

public class AIClassificationResponse {
    private String category;
    private String severity;
    private List<String> recommendedResources;
    private String summary;
    private double confidenceScore;

    public AIClassificationResponse() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public List<String> getRecommendedResources() { return recommendedResources; }
    public void setRecommendedResources(List<String> recommendedResources) { this.recommendedResources = recommendedResources; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
}
