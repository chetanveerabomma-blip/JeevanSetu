# 🚀 JeevanSetu — Production Deployment & Local Setup Guide

This guide covers running the JeevanSetu full-stack platform locally on Windows/Linux or via Docker Compose.

---

## 📋 Prerequisites

| Component | Minimum Version | Recommended |
|---|---|---|
| **Java Development Kit (JDK)** | OpenJDK 21 | Eclipse Temurin 21 |
| **Apache Maven** | 3.8+ | 3.9.9 |
| **Python** | 3.10+ | 3.11 / 3.14 |
| **MongoDB** | 6.0+ | MongoDB 7.0 / 8.0 Community |
| **Docker & Docker Compose** | Optional | v2.20+ |

---

## Option 1: Quickstart with Docker Compose (One-Command Launch)

From the root repository directory:

```bash
docker compose up --build -d
```

This single command provisions:
1. **MongoDB Container (`jeevansetu-mongodb`)** on port `27017` with automatic schema initialization from `database/init_mongo.js`.
2. **Python FastAPI AI Microservice (`jeevansetu-ai`)** on port `8000`.
3. **Java Spring Boot Backend & Frontend (`jeevansetu-backend`)** on port `8080`.

Open your browser to:
- **Web Application**: `http://localhost:8080/`
- **Swagger API Docs**: `http://localhost:8080/swagger-ui.html`
- **Python AI Swagger Docs**: `http://localhost:8000/docs`

---

## Option 2: Running Native Services Locally

### Step 1: Start MongoDB
Ensure MongoDB service is running locally on port `27017`.
Initialize sample seed data and indexes:
```bash
mongosh jeevansetu database/init_mongo.js
```

### Step 2: Start the Python AI Microservice
```bash
cd ai-python
python -m venv venv
# On Windows:
.\venv\Scripts\activate
# On Linux/macOS:
source venv/bin/activate

pip install -r requirements.txt
python main.py
```
*Service will start listening on `http://localhost:8000`.*

### Step 3: Start the Java Spring Boot Backend
```bash
cd backend-java
mvn clean spring-boot:run
```
*Application will bind to `http://localhost:8080`.*

---

## 🔐 Demonstration Credentials

| Role | Email | Password | Access Portal |
|---|---|---|---|
| **Commander (Control Center)** | `command@jeevansetu.gov.in` | `Admin@123` | `command-center.html` |
| **Ambulance Pilot (AMB-104)** | `ambulance@jeevansetu.gov.in` | `Admin@123` | `responder.html` |
| **Victoria Trauma Hospital** | `hospital@jeevansetu.gov.in` | `Admin@123` | `hospital.html` |
| **Red Cross Blood Bank** | `bloodbank@jeevansetu.gov.in` | `Admin@123` | `bloodbank.html` |
| **Citizen Reporter** | `citizen@jeevansetu.gov.in` | `Admin@123` | `citizen.html` |
| **Administrator** | `admin@jeevansetu.gov.in` | `Admin@123` | `analytics.html` |

---

## 🧪 Running Automated Verification Tests

### Test Java Backend Compilation:
```bash
cd backend-java
mvn test-compile
```

### Test Python AI Microservice:
```bash
cd ai-python
python -c "import main; print('FastAPI Loaded:', main.app.title)"
```
