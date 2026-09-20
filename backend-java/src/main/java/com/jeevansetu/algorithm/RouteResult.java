package com.jeevansetu.algorithm;

import java.util.ArrayList;
import java.util.List;

public class RouteResult {
    private List<String> pathNodeIds = new ArrayList<>();
    private List<String> pathNodeNames = new ArrayList<>();
    private List<double[]> pathCoordinates = new ArrayList<>(); // [[lng, lat], ...]
    private double totalDistanceKm;
    private int estimatedMinutes;
    private String routeSummary;

    public RouteResult() {}

    public List<String> getPathNodeIds() { return pathNodeIds; }
    public void setPathNodeIds(List<String> pathNodeIds) { this.pathNodeIds = pathNodeIds; }

    public List<String> getPathNodeNames() { return pathNodeNames; }
    public void setPathNodeNames(List<String> pathNodeNames) { this.pathNodeNames = pathNodeNames; }

    public List<double[]> getPathCoordinates() { return pathCoordinates; }
    public void setPathCoordinates(List<double[]> pathCoordinates) { this.pathCoordinates = pathCoordinates; }

    public double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public String getRouteSummary() { return routeSummary; }
    public void setRouteSummary(String routeSummary) { this.routeSummary = routeSummary; }
}
