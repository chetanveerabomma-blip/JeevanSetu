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

## 🧠 Advanced Computer Science Algorithms Implemented

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
5. **MongoDB Geospatial Indexing (`2dsphere`)**:
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
