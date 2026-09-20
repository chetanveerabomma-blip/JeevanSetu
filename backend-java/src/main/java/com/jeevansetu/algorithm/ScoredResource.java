package com.jeevansetu.algorithm;

import com.jeevansetu.model.ResponderUnit;

import java.util.ArrayList;
import java.util.List;

public class ScoredResource implements Comparable<ScoredResource> {
    private ResponderUnit unit;
    private double totalScore;
    private double distanceKm;
    private double distanceScore;
    private double availabilityScore;
    private double compatibilityScore;
    private double capacityScore;
    private double workloadScore;
    private List<String> reasons = new ArrayList<>();

    public ScoredResource() {}

    public ScoredResource(ResponderUnit unit, double totalScore, double distanceKm) {
        this.unit = unit;
        this.totalScore = totalScore;
        this.distanceKm = distanceKm;
    }

    public ResponderUnit getUnit() { return unit; }
    public void setUnit(ResponderUnit unit) { this.unit = unit; }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public double getDistanceScore() { return distanceScore; }
    public void setDistanceScore(double distanceScore) { this.distanceScore = distanceScore; }

    public double getAvailabilityScore() { return availabilityScore; }
    public void setAvailabilityScore(double availabilityScore) { this.availabilityScore = availabilityScore; }

    public double getCompatibilityScore() { return compatibilityScore; }
    public void setCompatibilityScore(double compatibilityScore) { this.compatibilityScore = compatibilityScore; }

    public double getCapacityScore() { return capacityScore; }
    public void setCapacityScore(double capacityScore) { this.capacityScore = capacityScore; }

    public double getWorkloadScore() { return workloadScore; }
    public void setWorkloadScore(double workloadScore) { this.workloadScore = workloadScore; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }

    public void addReason(String reason) { this.reasons.add(reason); }

    @Override
    public int compareTo(ScoredResource other) {
        // Highest total score first (descending)
        return Double.compare(other.totalScore, this.totalScore);
    }
}
