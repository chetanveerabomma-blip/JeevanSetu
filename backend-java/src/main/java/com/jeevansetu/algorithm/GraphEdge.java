package com.jeevansetu.algorithm;

public class GraphEdge {
    private String fromNodeId;
    private String toNodeId;
    private double distanceKm;
    private double speedLimitKmH;
    private double trafficWeight; // 1.0 = clear, 1.5 = moderate traffic, 2.0+ = heavy traffic

    public GraphEdge(String fromNodeId, String toNodeId, double distanceKm, double speedLimitKmH, double trafficWeight) {
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.distanceKm = distanceKm;
        this.speedLimitKmH = speedLimitKmH > 0 ? speedLimitKmH : 40.0;
        this.trafficWeight = trafficWeight >= 1.0 ? trafficWeight : 1.0;
    }

    public String getFromNodeId() { return fromNodeId; }
    public String getToNodeId() { return toNodeId; }
    public double getDistanceKm() { return distanceKm; }
    public double getSpeedLimitKmH() { return speedLimitKmH; }
    public double getTrafficWeight() { return trafficWeight; }

    /**
     * Travel time in minutes = (distance / speed) * 60 * trafficWeight
     */
    public double getTravelTimeMinutes() {
        return (distanceKm / speedLimitKmH) * 60.0 * trafficWeight;
    }
}
