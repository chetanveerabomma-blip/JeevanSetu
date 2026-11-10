# 🆘 JeevanSetu — Intelligent Emergency Response & Multi-Agency Resource Coordination Platform

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg?style=flat&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen.svg?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![Python 3](https://img.shields.io/badge/Python-3.11+-blue.svg?style=flat&logo=python)](https://python.org/)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.115+-009688.svg?style=flat&logo=fastapi)](https://fastapi.tiangolo.com/)
[![MongoDB](https://img.shields.io/badge/MongoDB-8.0_Ready-green.svg?style=flat&logo=mongodb)](https://www.mongodb.com/)
[![License MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![GitHub Pages](https://img.shields.io/badge/Live_Demo-GitHub_Pages-blueviolet.svg)](https://chetanveerabomma-blip.github.io/JeevanSetu/)

**JeevanSetu** is an advanced, production-grade emergency management and multi-agency coordination system designed for an **Advanced Programming Practice Major Project**. It connects citizens, paramedics, police, fire departments, trauma hospitals, blood banks, disaster shelters, and central operations commanders in real-time.

---

## 🌟 Live Deployment & Quick Links

- **🌐 Live Public Application (GitHub Pages)**: [https://chetanveerabomma-blip.github.io/JeevanSetu/](https://chetanveerabomma-blip.github.io/JeevanSetu/)
- **🏛️ GitHub Source Repository**: [https://github.com/chetanveerabomma-blip/JeevanSetu](https://github.com/chetanveerabomma-blip/JeevanSetu)
- **📐 System Architecture & Diagrams**: [`docs/SYSTEM_ARCHITECTURE.md`](docs/SYSTEM_ARCHITECTURE.md)
- **📋 Postman API Collection**: [`docs/JeevanSetu_Postman_Collection.json`](docs/JeevanSetu_Postman_Collection.json)
- **📖 Deployment & Setup Guide**: [`docs/DEPLOYMENT_GUIDE.md`](docs/DEPLOYMENT_GUIDE.md)
- **🎓 Comprehensive College Project Report**: [`docs/COLLEGE_REPORT.md`](docs/COLLEGE_REPORT.md)

---

## 🏗️ High-Level System Architecture

```
                 JEEVANSETU INTELLIGENT COMMAND PLATFORM
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        │                           │                           │
     CITIZEN                    RESPONDER                    HOSPITAL
  (GPS & Voice)           (Ambulance/Police/Fire)      (ICU & Bed Tracking)
        │                           │                           │
        └───────────────────────────┼───────────────────────────┘
                                    │
                             COMMAND CENTER
                    (Tactical Map & Operations Grid)
                                    │
           ┌────────────────────────┼────────────────────────┐
           │                        │                        │
     PRIORITY QUEUE        RESOURCE ALLOCATION        ROUTING ENGINE
  (Java PriorityQueue)    (Multi-Factor Scoring)    (Dijkstra Algorithm)
           │                        │                        │
           └────────────────────────┼────────────────────────┘
                                    │
                              REAL-TIME SYNC
                         (Spring STOMP WebSockets)
                                    │
                         ┌──────────┴──────────┐
                         │                     │
                      MONGODB               PYTHON AI
                  (2dsphere Index)       (FastAPI NLP)
```

---

## 🧠 Advanced Computer Science Algorithms & 18 Operational Modules

### The 18 Specialized Enterprise Modules Implemented:

| # | Enterprise Module | Core Java / Tech Implementation | Key Features |
|---|---|---|---|
| **1** | **Incident Command Centre** | `IncidentCommandService`, `IncidentAssignmentService`, `IncidentTimelineService` | Central dispatch grid, timeline tracking, remarks, agency assignment. |
| **2** | **Automatic Emergency Escalation** | `EmergencyEscalationScheduler` (`@Scheduled(fixedRate=30000)`) | Auto-escalation of unassigned cases per threshold; overdue alerts & dispatcher ack. |
| **3** | **Volunteer Coordination System** | `VolunteerService`, `VolunteerAssignmentService`, `VolunteerController` | Volunteer registration, profile privacy, skill mapping, non-hazard task proposals & approval. |
| **4** | **Disaster Management & Relief Centre** | `DisasterManagementService`, `DisasterEventRepository`, `ReliefCenterRepository` | Flood/fire/cyclone declarations, relief camp occupancy tracking, relief distribution requests. |
| **5** | **Emergency Resource Inventory** | `InventoryService`, `InventoryItemRepository`, `InventoryTransactionRepository` | Central medical/ration stock tracking, minimum safety thresholds, real-time low-stock alerts. |
| **6** | **Geo-Fencing & Location Alerts** | `GeoFenceService`, `GeoZoneRepository` | Haversine formula geospatial radius checks, danger zones, evacuation alerts. |
| **7** | **Duplicate Emergency Detection** | `DuplicateDetectionService` | Multi-criteria duplicate scanner: distance + category + Dice token similarity + time window. |
| **8** | **Offline Emergency Reporting** | `frontend/js/offline-sync.js` (IndexedDB / localStorage) | Draft preservation during disconnections, network state detection, automatic sync with client UUID. |
| **9** | **Emergency Tactical Communication** | `IncidentChatService`, `IncidentChatMessageRepository`, WebSocket | Incident-specific chat rooms, role & agency tagged messages, read status tracking. |
| **10** | **Smart Resource Allocation Engine** | `ResourceSuitabilityScorer`, `ResourceService` | Multi-factor suitability scoring (distance, equipment, workload, availability) with dispatcher signoff. |
| **11** | **AI Emergency Report Analyzer** | Python FastAPI (`/api/assistant/*`) + Java validation | Natural language parsing, entity/location/urgency extraction, structured fact extraction. |
| **12** | **Emergency Voice Reporting** | Web Speech API STT + TTS | Hands-free reporting in English, Hindi, Telugu, and Tamil with pre-dispatch verbal confirmation. |
| **13** | **Blood Donor & Blood Bank Coordination** | `BloodDonorCoordinationService`, `BloodDonorRepository` | Privacy-masked donor registry within radius, blood group matching, emergency hospital requests. |
| **14** | **Emergency Notification System** | `NotificationService`, `NotificationController` | In-app alerts, WebSocket broadcasts, category filtering, unread counters and receipts. |
| **15** | **Advanced Analytics & Reports** | `AnalyticsService`, `AnalyticsController` | Real database timestamp aggregations, response/dispatch time computation, CSV & executive summary export. |
| **16** | **Ambulance & Vehicle Maintenance** | `VehicleMaintenanceService`, `VehicleRecordRepository` | Fleet maintenance tracking (`ACTIVE`, `MAINTENANCE`, `OUT_OF_SERVICE`); excluded from dispatch queues. |
| **17** | **Advanced Security & Audit System** | `SecurityConfig`, `JwtAuthenticationFilter`, `AuditLogService` | Stateless JWT authentication, role-based access control (RBAC), immutable audit log trail. |
| **18** | **System Administrator Dashboard** | `frontend/admin.html`, `AdminController` | 9-tab operations console managing all 18 modules with real-time live database telemetry. |

---

## 🔬 Core Computer Science Algorithms Implemented

1. **Java Thread-Safe Priority Queue (`EmergencyPriorityQueue`)**:
   - In-memory triage queue backed by `java.util.PriorityQueue` and custom dynamic comparator.
   - Computes weighted triage scores: $\text{Level} \times 10^7 + \text{Casualties} \times 10^4 - \text{Timestamp}$.
2. **Clinical Trauma Triage Engine (`PriorityEngine`)**:
   - Deterministic rule engine calculating authoritative ratings (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`) based on loss of consciousness, arterial bleeding, entrapment, and mass casualties.
3. **Multi-Factor Resource Allocation Scoring (`ResourceSuitabilityScorer`)**:
   $$\text{Score} = \text{distanceScore} + \text{availabilityScore} + \text{compatibilityScore} + \text{capacityScore} + \text{workloadScore}$$
   - Transparent ranking with reasoning badges (e.g. *\"Rank #1: Unit AMB-104, 2.4 km away, ALS equipped, available immediately\"*).
4. **Road Graph Representation & Dijkstra Shortest Path (`DijkstraRouter`)**:
   - Adjacency-list directed graph model with edge weights factoring distance and congestion multipliers. Computes exact route waypoints for map rendering.
5. **Haversine Geospatial Geofence Engine (`GeoFenceService`)**:
   - Spherical trigonometric distance formula calculating danger zones, safe corridors, and proximity alerts without external GIS server dependencies.
6. **Dice Coefficient NLP Token Matcher (`DuplicateDetectionService`)**:
   - String bigram similarity scoring combined with spatial distance thresholding to detect duplicate caller reports.
7. **MongoDB Geospatial Indexing (`2dsphere`)**:
   - Spherical radius search (`$nearSphere`) querying verified trauma centers, blood banks, and responder units within regional radii.

---

## 🤖 AI Assistant & Emergency Intelligence Capabilities

- 🎙️ **AI Voice Assistant**: Dual speech-to-text (STT) and text-to-speech (TTS) voice reporting and playback.
- 🌐 **Multilingual NLP**: Support for **English, Hindi (हिन्दी), Telugu (తెలుగు), and Tamil (தமிழ்)**.
- 🚨 **Emergency SOS Beacon**: 1-click distress trigger with a **3-second safety countdown**, real-time GPS coordinate capture, and automatic multi-agency alerting.
- 📋 **AI Dispatcher Incident Summary**: Automatically differentiates **Confirmed Facts** (scene, casualties, trauma) from **Uncertain Information** requiring on-scene confirmation.
- 📻 **Multi-Agency Tactical Comms**: Role-restricted real-time radio channels between Police, Fire, Ambulance, Hospital, and Command Center.

---

## 🛠️ Technology Stack Breakdown

| Tier | Technologies |
|---|---|
| **Authoritative Backend (85%)** | Java 21, Spring Boot 3.3.4, Spring Security 6 (Stateless JWT), Spring Data MongoDB, Spring WebSocket (STOMP), SpringDoc OpenAPI / Swagger, JJWT. |
| **AI NLP Microservice (15%)** | Python 3.11+, FastAPI, Uvicorn, Pydantic, Scikit-learn, NumPy, Pandas. |
| **Database** | MongoDB (port 27017, database `jeevansetu`) with 14 collections and `2dsphere` indexes. |
| **Frontend** | HTML5, CSS3, Tailwind CSS, Leaflet.js, Chart.js, Font Awesome 6. |

---

## ⚡ Instant One-Click Demo Profiles

When launching `login.html`, click any profile to log in without typing:
- **Command Center**: `command@jeevansetu.gov.in` (Operations Dispatcher)
- **Ambulance Unit AMB-104**: `ambulance@jeevansetu.gov.in` (ALS Pilot)
- **Victoria Trauma Hospital**: `hospital@jeevansetu.gov.in` (ER Director)
- **Red Cross Blood Bank**: `bloodbank@jeevansetu.gov.in` (Inventory Manager)
- **System Admin**: `admin@jeevansetu.gov.in` (System Governance)
- **Citizen**: `citizen1@demo.jeevansetu.in` (Citizen Reporting)

*Default demo password:* `Admin@123` or `Demo@1234`

---

## 📦 How to Run Locally

### All-in-One Docker Launch:
```bash
docker compose up --build -d
```

### Manual Native Launch:
1. **Start MongoDB**: `mongosh jeevansetu database/init_mongo.js`
2. **Start Python AI**:
   ```bash
   cd ai-python && pip install -r requirements.txt && python main.py
   ```
3. **Start Java Spring Boot**:
   ```bash
   cd backend-java && mvn clean spring-boot:run
   ```

---

## 📄 License
This project is licensed under the MIT License - see the [`LICENSE`](LICENSE) file for details.
