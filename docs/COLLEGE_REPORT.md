# Project Report — JeevanSetu: Intelligent Emergency Response & Multi-Agency Resource Coordination Platform

**Course**: Advanced Programming Practice (APP) Major Project  
**Platform**: JeevanSetu  
**Target Domain**: Public Safety, Disaster Management & Emergency Health Services

---

## 1. Problem Statement
Existing municipal emergency response mechanisms are heavily fragmented across isolated control centers (108 for ambulance, 100/112 for police, 101 for fire, independent blood bank registries, and isolated hospital bed tracking). This fragmentation leads to:
- Delayed dispatch and inaccurate location transfer.
- Sub-optimal resource selection (e.g. dispatching distant units or units lacking required trauma gear).
- Critical bottleneck delays at hospital emergency intake where receiving trauma teams have zero advance telemetry.

## 2. System Objectives
1. **Centralized Incident Triage**: Unified multi-hazard intake (Medical, Road Accidents, Fire, Structural Collapses, Blood Shortages, Floods).
2. **Deterministic Priority Ranking**: Real-time algorithmic triage using Java `PriorityQueue` and a deterministic clinical trauma rule engine.
3. **Multi-Factor Resource Allocation**: Mathematical scoring of candidate emergency vehicles based on distance, availability, equipment capability, casualty capacity, and prior workload.
4. **Graph-Based Shortest/Fastest Routing**: Road network representation with Dijkstra's algorithm calculating route coordinates and traffic-weighted ETAs.
5. **Real-Time Agency Coordination**: Bi-directional STOMP WebSocket communication connecting first responders, hospitals, blood banks, and citizens.
6. **Assistive Artificial Intelligence**: Python FastAPI microservice utilizing NLP keyword extraction and text summarization to augment dispatch decisions.

---

## 3. Algorithmic Formulations

### 3.1 Resource Suitability Scoring Formula
$$\text{Score} = w_1 S_{\text{dist}} + w_2 S_{\text{avail}} + w_3 S_{\text{compat}} + w_4 S_{\text{cap}} + w_5 S_{\text{workload}}$$

Where:
- $S_{\text{dist}} = 40 \cdot \max\left(0, 1 - \frac{d_{\text{km}}}{30}\right)$ (Haversine spherical distance)
- $S_{\text{avail}} = 30$ if `AVAILABLE`, $12$ if `EN_ROUTE`, $0$ if busy
- $S_{\text{compat}} = 15$ if Advanced Life Support / Trauma kit matches critical severity
- $S_{\text{cap}} = 10$ if vehicle capacity $\ge$ casualties
- $S_{\text{workload}} = \max(0, 5 - 0.5 \cdot \text{tripsCompleted})$

### 3.2 Dijkstra's Routing
Given graph $G = (V, E)$ where edge weight $w(u, v) = \frac{\text{dist}_{uv}}{\text{speedLimit}} \cdot 60 \cdot \text{trafficWeight}$, Dijkstra calculates:
$$\text{dist}[v] = \min_{u \in V} (\text{dist}[u] + w(u, v))$$
yielding the optimal traversal path and coordinate waypoints for polyline rendering.

---

## 4. Database Schema
18 collections implemented in MongoDB:
- `users`, `emergencies`, `responders`, `ambulances`, `police_units`, `fire_units`, `hospitals`, `hospital_resources`, `blood_banks`, `blood_inventory`, `volunteers`, `shelters`, `assignments`, `routes`, `incident_updates`, `notifications`, `audit_logs`, `analytics`.

---

## 5. Security and Governance
- Stateless JWT authentication (`HMAC-SHA256`).
- Role-Based Access Control (`CITIZEN`, `AMBULANCE`, `POLICE`, `FIRE`, `HOSPITAL`, `BLOOD_BANK`, `VOLUNTEER`, `CONTROL_CENTER`, `ADMIN`).
- Universal immutable audit log documenting all state transitions and dispatch events.
