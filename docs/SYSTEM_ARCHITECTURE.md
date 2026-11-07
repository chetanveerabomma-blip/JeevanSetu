# 🏗️ JeevanSetu — Technical System Architecture & Algorithmic Designs

This document provides in-depth architectural and algorithmic documentation for the **JeevanSetu** Emergency Response & Multi-Agency Resource Coordination Platform.

---

## 1. High-Level System Architecture

```mermaid
graph TD
    subgraph Clients["Frontend Layer (HTML5, Tailwind, Leaflet, Web Speech)"]
        CitizenPortal["Citizen Portal & Floating AI"]
        CommandCenter["Command Operations Center"]
        ResponderTerminal["Mobile Responder Terminal"]
        HospitalPortal["Hospital Trauma Portal"]
    end

    subgraph Gateway["API & Security Layer"]
        SecurityConfig["Spring Security 6 (Stateless JWT)"]
        WebSocketBroker["Spring STOMP WebSocket Broker"]
    end

    subgraph JavaCore["Authoritative Backend (Java 21 / Spring Boot 3.3.4)"]
        PriorityEngine["Triage Rule Engine"]
        PriorityQueue["PriorityQueue (Triage Memory Queue)"]
        SuitabilityScorer["Multi-Factor Resource Scorer"]
        DijkstraRouter["Dijkstra Road Graph Router"]
        EmergencyService["Emergency & SOS Lifecycle"]
        AgencyCommsService["Multi-Agency Comms Engine"]
        AnalyticsService["Real-Time Analytics Engine"]
    end

    subgraph AIMicroservice["AI Microservice (Python FastAPI)"]
        MultilingualNLP["Multilingual Intent Classifier (EN, HI, TE, TA)"]
        EntityExtractor["Entity & Clinical Urgency Extractor"]
        DispatcherSummary["Incident Summarizer (Facts vs Uncertainties)"]
    end

    subgraph DataLayer["Persistence Layer"]
        MongoDB[(MongoDB 8.0: jeevansetu DB)]
        GeospatialIdx["2dsphere Geospatial Indexing"]
    end

    CitizenPortal -->|REST / WebSocket| Gateway
    CommandCenter -->|REST / WebSocket| Gateway
    ResponderTerminal -->|REST / WebSocket| Gateway
    HospitalPortal -->|REST / WebSocket| Gateway

    Gateway --> JavaCore
    JavaCore -->|HTTP REST Client (JSON)| AIMicroservice
    JavaCore -->|Spring Data MongoDB| DataLayer
```

---

## 2. Emergency Intake & Dispatch Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor Citizen as Citizen / Caller
    participant Chatbot as Floating AI Chatbot
    participant PythonAI as Python NLP Microservice
    participant JavaBackend as Java Spring Boot Core
    participant PriorityQ as PriorityQueue (Memory)
    participant Mongo as MongoDB (jeevansetu)
    participant Operator as Command Center Dispatcher
    participant Responder as Assigned Ambulance / EMS

    Citizen->>Chatbot: "Car crash near MG Road, 2 injured, 1 unconscious"
    Chatbot->>JavaBackend: POST /api/assistant/chat (Lang: en)
    JavaBackend->>PythonAI: POST /ai/chat
    PythonAI-->>JavaBackend: Intent=REPORT_EMERGENCY, Casualties=2, Unconscious=true
    JavaBackend->>JavaBackend: Calculate authoritative triage rating: CRITICAL
    JavaBackend-->>Chatbot: Return Emergency Draft Card
    Citizen->>Chatbot: Tap "Confirm & Dispatch Emergency"
    Chatbot->>JavaBackend: POST /api/assistant/emergency-confirm
    JavaBackend->>Mongo: Insert into 'emergencies' (Code: JS-2026-000187)
    JavaBackend->>PriorityQ: Enqueue incident (Thread-safe PriorityQueue)
    JavaBackend->>Operator: WebSocket Broadcast (/topic/emergencies)
    Operator->>JavaBackend: GET /api/assistant/ambulances/recommend/emg-id
    JavaBackend->>JavaBackend: Score candidate ambulances (Haversine + Equipment ALS + Availability)
    JavaBackend-->>Operator: Recommended: Unit AMB-104 (Rank #1, 2.4 km away)
    Operator->>JavaBackend: POST /api/resources/assign (Dispatcher Authorization)
    JavaBackend->>JavaBackend: Run Dijkstra Algorithm (Shortest Waypoint Corridor)
    JavaBackend->>Mongo: Update unit status to ASSIGNED & save Assignment
    JavaBackend->>Responder: WebSocket Broadcast (/topic/dispatches)
    Responder->>JavaBackend: PATCH /api/resources/assignments/{id}/status (EN_ROUTE)
```

---

## 3. Database Entity-Relationship (ER) Model

```mermaid
erDiagram
    USERS ||--o{ EMERGENCIES : reports
    EMERGENCIES ||--o{ ASSIGNMENTS : receives
    RESPONDER_UNITS ||--o{ ASSIGNMENTS : assigned_to
    EMERGENCIES ||--o{ INCIDENT_UPDATES : tracks
    EMERGENCIES ||--o{ AGENCY_MESSAGES : tactical_comms
    EMERGENCIES }o--|| HOSPITALS : receiving_hospital

    USERS {
        string id PK
        string email UK
        string password
        string fullName
        string phone
        string role
        string assignedUnitId
    }

    EMERGENCIES {
        string id PK
        string incidentCode UK
        string type
        string priority
        string status
        double latitude
        double longitude
        string address
        int injuredCount
        boolean unconscious
        boolean severeBleeding
        boolean fireInvolved
        boolean trapped
        timestamp createdAt
    }

    RESPONDER_UNITS {
        string id PK
        string unitCode UK
        string name
        string type
        string status
        double latitude
        double longitude
        string driverName
        string contactPhone
        string equipmentLevel
    }

    HOSPITALS {
        string id PK
        string name
        string registrationId UK
        double latitude
        double longitude
        int emergencyBeds
        int occupiedEmergencyBeds
        int icuBeds
        int occupiedIcuBeds
        int ventilators
    }

    AGENCY_MESSAGES {
        string id PK
        string incidentId FK
        string incidentCode
        string senderRole
        string senderName
        string agencyType
        string message
        string priority
        timestamp timestamp
    }
```

---

## 4. Multi-Factor Resource Suitability Formula

Candidate responder units are ranked using the deterministic scoring equation implemented in [`ResourceSuitabilityScorer.java`](file:///C:/Users/V%20CHETAN/.gemini/antigravity/scratch/JeevanSetu/backend-java/src/main/java/com/jeevansetu/algorithm/ResourceSuitabilityScorer.java):

$$\text{Total Score} = S_{\text{dist}} + S_{\text{avail}} + S_{\text{compat}} + S_{\text{cap}} + S_{\text{workload}}$$

Where:
- $S_{\text{dist}} = 40 \times \max\left(0, 1 - \frac{d}{25}\right)$ ($d$ is the Haversine spherical distance in kilometers).
- $S_{\text{avail}} = 30$ if unit is `AVAILABLE`, $10$ if `EN_ROUTE`.
- $S_{\text{compat}} = 15$ if vehicle equipment matches emergency trauma tier (e.g. ALS for Critical, BLS for High).
- $S_{\text{cap}} = 10$ if patient capacity $\ge$ casualties reported.
- $S_{\text{workload}} = 5$ if unit has zero prior open assignments in the shift.
