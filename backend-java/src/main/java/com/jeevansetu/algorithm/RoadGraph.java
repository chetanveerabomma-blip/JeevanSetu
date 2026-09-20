package com.jeevansetu.algorithm;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoadGraph {

    private final Map<String, GraphNode> nodes = new HashMap<>();
    private final Map<String, List<GraphEdge>> adjacencyList = new HashMap<>();

    public RoadGraph() {
        initializeDemoCityNetwork();
    }

    private void initializeDemoCityNetwork() {
        // Core City Nodes (Centering around Bangalore / Metropolitian demo coords ~ 12.9716, 77.5946)
        addNode(new GraphNode("N-CENTRAL", "Central Square Junction", 12.9716, 77.5946));
        addNode(new GraphNode("N-HOSP-MAIN", "Apex Multi-Speciality Hospital", 12.9790, 77.5910));
        addNode(new GraphNode("N-HOSP-SOUTH", "LifeCare South Hospital", 12.9550, 77.5880));
        addNode(new GraphNode("N-FIRE-STN1", "Station 1 Central Fire Command", 12.9680, 77.6050));
        addNode(new GraphNode("N-POLICE-HQ", "Metro Police Central Division", 12.9740, 77.6090));
        addNode(new GraphNode("N-RING-NORTH", "North Ring Road Interchange", 12.9920, 77.5950));
        addNode(new GraphNode("N-RING-EAST", "East Expressway Exit 4", 12.9720, 77.6250));
        addNode(new GraphNode("N-TECH-PARK", "Cyber Technology Corridor", 12.9850, 77.6320));
        addNode(new GraphNode("N-METRO-WEST", "West Metro Hub Junction", 12.9650, 77.5750));
        addNode(new GraphNode("N-SOUTH-BRIDGE", "South River Bridge Point", 12.9480, 77.6020));

        // Connect Road Edges (Bidirectional)
        addEdge("N-CENTRAL", "N-HOSP-MAIN", 1.2, 50, 1.1);
        addEdge("N-CENTRAL", "N-FIRE-STN1", 1.5, 45, 1.2);
        addEdge("N-CENTRAL", "N-METRO-WEST", 2.2, 40, 1.3);
        addEdge("N-CENTRAL", "N-POLICE-HQ", 1.6, 50, 1.0);
        addEdge("N-HOSP-MAIN", "N-RING-NORTH", 2.1, 60, 1.1);
        addEdge("N-RING-NORTH", "N-TECH-PARK", 3.8, 70, 1.2);
        addEdge("N-TECH-PARK", "N-RING-EAST", 2.0, 60, 1.4);
        addEdge("N-RING-EAST", "N-POLICE-HQ", 2.3, 55, 1.2);
        addEdge("N-FIRE-STN1", "N-RING-EAST", 2.5, 50, 1.1);
        addEdge("N-METRO-WEST", "N-HOSP-SOUTH", 2.8, 45, 1.3);
        addEdge("N-HOSP-SOUTH", "N-SOUTH-BRIDGE", 1.9, 50, 1.2);
        addEdge("N-SOUTH-BRIDGE", "N-FIRE-STN1", 3.1, 55, 1.3);
    }

    public void addNode(GraphNode node) {
        nodes.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(String from, String to, double distKm, double speedKmH, double trafficWeight) {
        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(new GraphEdge(from, to, distKm, speedKmH, trafficWeight));
        adjacencyList.computeIfAbsent(to, k -> new ArrayList<>()).add(new GraphEdge(to, from, distKm, speedKmH, trafficWeight));
    }

    public Map<String, GraphNode> getNodes() { return nodes; }
    public Map<String, List<GraphEdge>> getAdjacencyList() { return adjacencyList; }

    public GraphNode findNearestNode(double lat, double lon) {
        GraphNode nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (GraphNode node : nodes.values()) {
            double d = ResourceSuitabilityScorer.calculateHaversineDistance(lat, lon, node.getLatitude(), node.getLongitude());
            if (d < minDistance) {
                minDistance = d;
                nearest = node;
            }
        }
        return nearest;
    }
}
