package com.jeevansetu.evacuation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "blocked_roads")
public class BlockedRoad {
    @Id
    private String id;
    private String roadName;
    private String reason; // e.g. FLOODED, TREE_FALL, STRUCTURAL_DAMAGE, LANDSLIDE
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private double penaltyMultiplier = 100.0; // Graph cost multiplier to prevent routing
    private boolean active = true;
    private String reportedBy;
    private Instant createdAt = Instant.now();

    public BlockedRoad() {}

    public BlockedRoad(String roadName, String reason, double startLat, double startLng, double endLat, double endLng) {
        this.roadName = roadName;
        this.reason = reason;
        this.startLatitude = startLat;
        this.startLongitude = startLng;
        this.endLatitude = endLat;
        this.endLongitude = endLng;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoadName() { return roadName; }
    public void setRoadName(String roadName) { this.roadName = roadName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public double getStartLatitude() { return startLatitude; }
    public void setStartLatitude(double startLatitude) { this.startLatitude = startLatitude; }

    public double getStartLongitude() { return startLongitude; }
    public void setStartLongitude(double startLongitude) { this.startLongitude = startLongitude; }

    public double getEndLatitude() { return endLatitude; }
    public void setEndLatitude(double endLatitude) { this.endLatitude = endLatitude; }

    public double getEndLongitude() { return endLongitude; }
    public void setEndLongitude(double endLongitude) { this.endLongitude = endLongitude; }

    public double getPenaltyMultiplier() { return penaltyMultiplier; }
    public void setPenaltyMultiplier(double penaltyMultiplier) { this.penaltyMultiplier = penaltyMultiplier; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
