package com.jeevansetu.maps;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "RouteHistory")
public class RouteHistory {
    @Id
    private String id;
    private String entityId;
    private String entityType; // AMBULANCE, CITIZEN, EVACUATION
    private double originLatitude;
    private double originLongitude;
    private double destinationLatitude;
    private double destinationLongitude;
    private double distanceKm;
    private int durationMinutes;
    private String routingEngine; // OSRM, DIJKSTRA_GRAPH
    private List<List<Double>> waypoints = new ArrayList<>();
    private Instant calculatedAt = Instant.now();

    public RouteHistory() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public double getOriginLatitude() { return originLatitude; }
    public void setOriginLatitude(double originLatitude) { this.originLatitude = originLatitude; }

    public double getOriginLongitude() { return originLongitude; }
    public void setOriginLongitude(double originLongitude) { this.originLongitude = originLongitude; }

    public double getDestinationLatitude() { return destinationLatitude; }
    public void setDestinationLatitude(double destinationLatitude) { this.destinationLatitude = destinationLatitude; }

    public double getDestinationLongitude() { return destinationLongitude; }
    public void setDestinationLongitude(double destinationLongitude) { this.destinationLongitude = destinationLongitude; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getRoutingEngine() { return routingEngine; }
    public void setRoutingEngine(String routingEngine) { this.routingEngine = routingEngine; }

    public List<List<Double>> getWaypoints() { return waypoints; }
    public void setWaypoints(List<List<Double>> waypoints) { this.waypoints = waypoints; }

    public Instant getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Instant calculatedAt) { this.calculatedAt = calculatedAt; }
}
