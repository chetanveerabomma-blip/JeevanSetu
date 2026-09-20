# 🆘 JeevanSetu — Intelligent Emergency Response & Multi-Agency Resource Coordination Platform

**JeevanSetu** is a production-style, multi-agency emergency coordination and response management platform engineered for an **Advanced Programming Practice Major Project**.

---

## 🚀 Key Highlights & Architecture

```
                 JEEVANSETU COMMAND PLATFORM
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
     CITIZEN              RESPONDER              HOSPITAL
  (GPS Reporting)    (Ambulance/Police/Fire) (ICU & Bed Tracking)
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              │
                      COMMAND CENTER
              (Tactical Map & Operations Grid)
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
   PRIORITY QUEUE     RESOURCE ALLOCATION     ROUTING ENGINE
(Java PriorityQueue) (Multi-Factor Scoring) (Dijkstra Algorithm)
          │                   │                   │
          └───────────────────┼───────────────────┘
                              │
                        REAL-TIME SYNC
                     (Spring STOMP WebSockets)
                              │
                    ┌─────────┴─────────┐
                    │                   │
                 MONGODB            PYTHON AI
             (2dsphere Index)    (FastAPI NLP)
```

---

## 🧠 Major Computer Science Algorithms Implemented

1. **Priority Queue (`java.util.PriorityQueue` & `Comparator<Emergency>`)**:
   - Thread-safe operational triage engine ranking incidents by clinical trauma severity (`CRITICAL > HIGH > MEDIUM > LOW`), victim count, and FIFO timestamps.
2. **Deterministic Priority Rule Engine**:
   - Evaluates clinical trauma indicators (unconsciousness, hemorrhage, structural trapping, mass casualties) to assign authoritative triage levels.
3. **Multi-Factor Resource Allocation Scoring**:
   $$\text{Score} = \text{distanceScore} + \text{availabilityScore} + \text{compatibilityScore} + \text{capacityScore} + \text{workloadScore}$$
   - Generates transparent audit reasoning badges (e.g. *\"Recommended Unit A-104: 2.4 km away, ALS equipped, capacity matched\"*).
4. **Road Graph & Dijkstra's Algorithm**:
   - Custom Graph representation with weighted directed edges taking into account physical road distance, speed limits, and traffic multipliers to calculate the fastest transit corridor and precise waypoint coordinates for map polylines.
5. **MongoDB Geospatial Indexing (`2dsphere`)**:
   - Spherical radius search (`$nearSphere`) locating nearest trauma hospitals, blood banks with active stock, and responder units.

---

## 🛠️ Technology Stack

- **Authoritative Backend**: Java 21+, Spring Boot 3.3.4, Spring Web, Spring Security, Spring Data MongoDB, Spring WebSocket (STOMP), JJWT 0.12.6, SpringDoc OpenAPI / Swagger.
- **AI Microservice**: Python 3.11+, FastAPI, Uvicorn, Pydantic, Scikit-learn, NumPy, Pandas.
- **Database**: MongoDB (port 27017, database `jeevansetu`) with 18 dedicated collections and 2dsphere indexing.
- **Frontend**: HTML5, CSS3, Tailwind CSS, Leaflet.js, Chart.js, FontAwesome 6.

---

## 🌐 Live Demos & Localhost Access

| Service | Port / URL | Description |
| :--- | :--- | :--- |
| **Web Application** | `http://localhost:8080/` | Main portal & public landing page |
| **Command Center** | `http://localhost:8080/command-center.html` | Tactical dispatch operations room |
| **Citizen Portal** | `http://localhost:8080/citizen.html` | Multi-step emergency reporting |
| **Responder Terminal**| `http://localhost:8080/responder.html` | Ambulance / Police / Fire dashboard |
| **Hospital Portal** | `http://localhost:8080/hospital.html` | ICU beds & trauma admission manager |
| **Blood Bank Portal**| `http://localhost:8080/bloodbank.html` | Blood stock & emergency locator |
| **Analytics & Heatmap**| `http://localhost:8080/analytics.html` | Chart.js metrics & density heatmap |
| **AI Swagger Docs** | `http://localhost:8000/docs` | FastAPI interactive API documentation |

---

## ⚡ Instant One-Click Demo Profiles

When launching `login.html`, click any of the single-click profiles:
- **Command Center**: `command@jeevansetu.gov.in` (Operations Dispatcher)
- **Ambulance Unit A-104**: `ambulance@jeevansetu.gov.in` (ALS Pilot)
- **Victoria Trauma Hospital**: `hospital@jeevansetu.gov.in` (ER Director)
- **Red Cross Blood Bank**: `bloodbank@jeevansetu.gov.in` (Inventory Manager)
- **System Admin**: `admin@jeevansetu.gov.in` (System Governance)
- **Citizen**: `citizen1@demo.jeevansetu.in` (Citizen Reporting)

*Default demo password:* `Demo@1234`

---

## 📦 How to Run Locally

### 1. Database (MongoDB)
Ensure MongoDB is running on port `27017`. Database `jeevansetu` will automatically be seeded upon initial startup.

### 2. Python AI Service
```bash
cd ai-python
pip install -r requirements.txt
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

### 3. Java Spring Boot Backend
```bash
cd backend-java
mvn clean spring-boot:run
```

### 4. Docker Compose (Alternative All-In-One)
```bash
docker-compose up --build
```
