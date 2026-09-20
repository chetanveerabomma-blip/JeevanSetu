package com.jeevansetu.algorithm;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DijkstraRouter {

    private final RoadGraph roadGraph;

    public DijkstraRouter(RoadGraph roadGraph) {
        this.roadGraph = roadGraph;
    }

    private static class NodeDistance implements Comparable<NodeDistance> {
        String nodeId;
        double travelTimeMinutes;

        NodeDistance(String nodeId, double travelTimeMinutes) {
            this.nodeId = nodeId;
            this.travelTimeMinutes = travelTimeMinutes;
        }

        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(this.travelTimeMinutes, other.travelTimeMinutes);
        }
    }

    /**
     * Executes Dijkstra's algorithm to find the fastest route between two GPS coordinates.
     */
    public RouteResult calculateRoute(double startLat, double startLng, double endLat, double endLng) {
        GraphNode startNode = roadGraph.findNearestNode(startLat, startLng);
        GraphNode endNode = roadGraph.findNearestNode(endLat, endLng);

        RouteResult result = new RouteResult();

        if (startNode == null || endNode == null) {
            // Direct fallback
            double directDist = ResourceSuitabilityScorer.calculateHaversineDistance(startLat, startLng, endLat, endLng);
            result.setTotalDistanceKm(directDist);
            result.setEstimatedMinutes(Math.max(2, (int) Math.round((directDist / 40.0) * 60.0)));
            result.setPathCoordinates(List.of(new double[]{startLng, startLat}, new double[]{endLng, endLat}));
            result.setRouteSummary("Direct emergency transit corridor");
            return result;
        }

        // Dijkstra's Algorithm
        Map<String, Double> minTime = new HashMap<>();
        Map<String, String> predecessor = new HashMap<>();
        Map<String, Double> edgeDistances = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();

        for (String nId : roadGraph.getNodes().keySet()) {
            minTime.put(nId, Double.MAX_VALUE);
        }

        minTime.put(startNode.getId(), 0.0);
        pq.offer(new NodeDistance(startNode.getId(), 0.0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            String u = current.nodeId;

            if (u.equals(endNode.getId())) {
                break; // Found destination
            }

            if (current.travelTimeMinutes > minTime.get(u)) {
                continue;
            }

            List<GraphEdge> neighbors = roadGraph.getAdjacencyList().getOrDefault(u, Collections.emptyList());
            for (GraphEdge edge : neighbors) {
                String v = edge.getToNodeId();
                double newTime = minTime.get(u) + edge.getTravelTimeMinutes();
                if (newTime < minTime.get(v)) {
                    minTime.put(v, newTime);
                    predecessor.put(v, u);
                    edgeDistances.put(v, edge.getDistanceKm());
                    pq.offer(new NodeDistance(v, newTime));
                }
            }
        }

        // Reconstruct path
        List<String> pathIds = new ArrayList<>();
        String curr = endNode.getId();
        if (minTime.get(curr) < Double.MAX_VALUE || curr.equals(startNode.getId())) {
            while (curr != null) {
                pathIds.add(0, curr);
                curr = predecessor.get(curr);
            }
        } else {
            // If disconnected, fallback to start -> end
            pathIds.add(startNode.getId());
            pathIds.add(endNode.getId());
        }

        // Build coordinate sequence
        List<double[]> coordinates = new ArrayList<>();
        // Start origin point
        coordinates.add(new double[]{startLng, startLat});

        double totalDistKm = 0;
        List<String> names = new ArrayList<>();

        for (int i = 0; i < pathIds.size(); i++) {
            GraphNode n = roadGraph.getNodes().get(pathIds.get(i));
            if (n != null) {
                coordinates.add(new double[]{n.getLongitude(), n.getLatitude()});
                names.add(n.getName());
                if (i > 0) {
                    totalDistKm += edgeDistances.getOrDefault(pathIds.get(i), 1.5);
                }
            }
        }

        // Final destination point
        coordinates.add(new double[]{endLng, endLat});
        totalDistKm += ResourceSuitabilityScorer.calculateHaversineDistance(
                startLat, startLng, startNode.getLatitude(), startNode.getLongitude()
        );
        totalDistKm += ResourceSuitabilityScorer.calculateHaversineDistance(
                endNode.getLatitude(), endNode.getLongitude(), endLat, endLng
        );

        double totalMinutes = minTime.getOrDefault(endNode.getId(), 5.0);
        if (totalMinutes == Double.MAX_VALUE || totalMinutes <= 0) {
            totalMinutes = Math.max(3, (totalDistKm / 45.0) * 60.0);
        }

        result.setPathNodeIds(pathIds);
        result.setPathNodeNames(names);
        result.setPathCoordinates(coordinates);
        result.setTotalDistanceKm(Math.round(totalDistKm * 10.0) / 10.0);
        result.setEstimatedMinutes(Math.max(1, (int) Math.round(totalMinutes)));
        result.setRouteSummary(String.format("Dijkstra optimal corridor via %s (%d nodes, %.1f km)",
                names.isEmpty() ? "expressway" : names.get(0), pathIds.size(), result.getTotalDistanceKm()));

        return result;
    }
}
