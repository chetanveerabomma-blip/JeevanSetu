package com.jeevansetu.maps;

import com.jeevansetu.evacuation.RouteCalculationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service("mapsRouteService")
public class RouteService {

    private final RouteCalculationService routeCalculationService;
    private final RouteHistoryRepository routeHistoryRepository;

    public RouteService(RouteCalculationService routeCalculationService,
                        RouteHistoryRepository routeHistoryRepository) {
        this.routeCalculationService = routeCalculationService;
        this.routeHistoryRepository = routeHistoryRepository;
    }

    public static class RouteResponse {
        private String entityId;
        private String routeType;
        private double originLat;
        private double originLng;
        private double destLat;
        private double destLng;
        private double distanceKm;
        private int durationMinutes;
        private String routingEngine;
        private List<List<Double>> polylineCoordinates;
        private List<String> avoidedBlockedRoads;
        private String disclaimer;

        public RouteResponse() {}

        public String getEntityId() { return entityId; }
        public void setEntityId(String entityId) { this.entityId = entityId; }
        public String getRouteType() { return routeType; }
        public void setRouteType(String routeType) { this.routeType = routeType; }
        public double getOriginLat() { return originLat; }
        public void setOriginLat(double originLat) { this.originLat = originLat; }
        public double getOriginLng() { return originLng; }
        public void setOriginLng(double originLng) { this.originLng = originLng; }
        public double getDestLat() { return destLat; }
        public void setDestLat(double destLat) { this.destLat = destLat; }
        public double getDestLng() { return destLng; }
        public void setDestLng(double destLng) { this.destLng = destLng; }
        public double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
        public int getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
        public String getRoutingEngine() { return routingEngine; }
        public void setRoutingEngine(String routingEngine) { this.routingEngine = routingEngine; }
        public List<List<Double>> getPolylineCoordinates() { return polylineCoordinates; }
        public void setPolylineCoordinates(List<List<Double>> polylineCoordinates) { this.polylineCoordinates = polylineCoordinates; }
        public List<String> getAvoidedBlockedRoads() { return avoidedBlockedRoads; }
        public void setAvoidedBlockedRoads(List<String> avoidedBlockedRoads) { this.avoidedBlockedRoads = avoidedBlockedRoads; }
        public String getDisclaimer() { return disclaimer; }
        public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
    }

    public RouteResponse calculateAndRecordRoute(String entityId, String routeType,
                                                double startLat, double startLng,
                                                double endLat, double endLng,
                                                boolean alternative) {
        LocationService.validateCoordinates(startLat, startLng);
        LocationService.validateCoordinates(endLat, endLng);

        RouteCalculationService.CalculatedRoute calc = routeCalculationService.calculateRoute(
                startLat, startLng, endLat, endLng, alternative
        );

        RouteResponse resp = new RouteResponse();
        resp.setEntityId(entityId);
        resp.setRouteType(routeType != null ? routeType : "EMERGENCY_CORRIDOR");
        resp.setOriginLat(startLat);
        resp.setOriginLng(startLng);
        resp.setDestLat(endLat);
        resp.setDestLng(endLng);
        resp.setDistanceKm(calc.getDistanceKm());
        resp.setDurationMinutes(calc.getDurationMinutes());
        resp.setRoutingEngine(calc.getRoutingEngine());
        resp.setPolylineCoordinates(calc.getCoordinates());
        resp.setAvoidedBlockedRoads(calc.getAvoidedBlockedRoads());
        resp.setDisclaimer("Route estimates calculated via OSRM open road network. Actual transit time subject to dynamic weather and physical on-scene conditions.");

        // Persist to RouteHistory collection
        RouteHistory history = new RouteHistory();
        history.setEntityId(entityId);
        history.setEntityType(routeType);
        history.setOriginLatitude(startLat);
        history.setOriginLongitude(startLng);
        history.setDestinationLatitude(endLat);
        history.setDestinationLongitude(endLng);
        history.setDistanceKm(calc.getDistanceKm());
        history.setDurationMinutes(calc.getDurationMinutes());
        history.setRoutingEngine(calc.getRoutingEngine());
        history.setWaypoints(calc.getCoordinates());
        history.setCalculatedAt(Instant.now());
        routeHistoryRepository.save(history);

        return resp;
    }

    public List<RouteHistory> getRecentRoutes() {
        return routeHistoryRepository.findTop20ByOrderByCalculatedAtDesc();
    }
}
