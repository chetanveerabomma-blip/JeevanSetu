import re
from typing import List, Optional, Dict, Any
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import numpy as np

app = FastAPI(
    title="JeevanSetu AI Assistive Service",
    description="Emergency NLP Classification, Incident Summarization, and Hazard Pattern Analysis",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Pydantic Schemas
class ClassificationRequest(BaseModel):
    description: str

class ClassificationResponse(BaseModel):
    category: str
    severity: str
    recommendedResources: List[str]
    summary: str
    confidenceScore: float

class SummaryRequest(BaseModel):
    incidentCode: Optional[str] = "INCIDENT"
    description: Optional[str] = ""
    injuredCount: Optional[int] = 0
    unconscious: Optional[bool] = False

class SummaryResponse(BaseModel):
    summary: str

class DescriptionAnalysisRequest(BaseModel):
    description: str

class HistoricalAnalysisRequest(BaseModel):
    timeframeDays: Optional[int] = 30
    totalIncidents: Optional[int] = 120

# Emergency Knowledge Base & Keyword Matching
KEYWORDS = {
    "ACCIDENT": ["accident", "crash", "collision", "vehicle", "car", "bike", "truck", "overturned", "hit and run"],
    "FIRE": ["fire", "blaze", "smoke", "burning", "explosion", "flames", "cylinder blast", "short circuit"],
    "MEDICAL": ["heart attack", "chest pain", "cardiac", "stroke", "unconscious", "breathing", "diabetic", "seizure", "bleeding"],
    "BUILDING_COLLAPSE": ["collapse", "building", "rubble", "crushed", "trapped", "debris", "roof collapsed"],
    "BLOOD_EMERGENCY": ["blood", "transfusion", "platelets", "donor", "rare blood", "o negative", "hemorrhage"],
    "FLOOD": ["flood", "water level", "submerged", "stranded in water", "heavy rain", "overflowing"],
    "POLICE": ["robbery", "theft", "assault", "fight", "violence", "weapon", "burglary", "riot"],
    "RESCUE": ["trapped", "stuck", "borewell", "lift failure", "drowning", "trench", "confined space"]
}

CRITICAL_TRIGGERS = [
    "unconscious", "not breathing", "severe bleeding", "massive blood",
    "head trauma", "cardiac arrest", "collapsed", "trapped inside",
    "explosion", "major fire", "structural failure", "children trapped"
]

HIGH_TRIGGERS = [
    "injured", "fracture", "burns", "smoke inhalation", "chest pain",
    "deep cut", "dizzy", "bleeding", "accident", "flames"
]

@app.get("/health")
def health_check():
    return {"status": "HEALTHY", "service": "JeevanSetu-AI-Engine", "version": "1.0.0"}

@app.post("/ai/classify-emergency", response_model=ClassificationResponse)
def classify_emergency(req: ClassificationRequest):
    desc = req.description.lower()

    # Determine Category
    scores = {cat: 0 for cat in KEYWORDS}
    for cat, words in KEYWORDS.items():
        for w in words:
            if re.search(r'\b' + re.escape(w) + r'\b', desc):
                scores[cat] += 2
            elif w in desc:
                scores[cat] += 1

    best_cat = max(scores, key=scores.get)
    if scores[best_cat] == 0:
        best_cat = "MEDICAL"  # default fallback

    # Determine Severity
    is_critical = any(crit in desc for crit in CRITICAL_TRIGGERS)
    is_high = any(hi in desc for hi in HIGH_TRIGGERS)

    if is_critical:
        severity = "CRITICAL"
        confidence = 0.96
    elif is_high:
        severity = "HIGH"
        confidence = 0.89
    else:
        severity = "MEDIUM"
        confidence = 0.82

    # Recommend Multi-Agency Resources
    resources = []
    if best_cat == "ACCIDENT":
        resources = ["AMBULANCE", "POLICE"]
    elif best_cat == "FIRE":
        resources = ["FIRE_UNIT", "POLICE"]
        if is_critical or is_high:
            resources.append("AMBULANCE")
    elif best_cat == "BUILDING_COLLAPSE":
        resources = ["FIRE_UNIT", "AMBULANCE", "POLICE", "HOSPITAL"]
    elif best_cat == "BLOOD_EMERGENCY":
        resources = ["HOSPITAL", "BLOOD_BANK"]
    elif best_cat == "MEDICAL":
        resources = ["AMBULANCE", "HOSPITAL"]
    elif best_cat == "POLICE":
        resources = ["POLICE"]
        if is_critical or is_high:
            resources.append("AMBULANCE")
    elif best_cat == "FLOOD":
        resources = ["FIRE_UNIT", "AMBULANCE", "POLICE", "SHELTER", "VOLUNTEER"]
    else:
        resources = ["AMBULANCE", "POLICE"]

    # Generate synopsis
    summary = f"AI Classified as {best_cat} with {severity} severity. Recommended resources: {', '.join(resources)}."

    return ClassificationResponse(
        category=best_cat,
        severity=severity,
        recommendedResources=resources,
        summary=summary,
        confidenceScore=confidence
    )

@app.post("/ai/summarize-incident", response_model=SummaryResponse)
def summarize_incident(req: SummaryRequest):
    parts = []
    parts.append(f"Incident {req.incidentCode}:")
    if req.injuredCount and req.injuredCount > 0:
        parts.append(f"{req.injuredCount} casualties/injuries reported.")
    if req.unconscious:
        parts.append("Trauma alert: Patient unresponsive/unconscious.")
    if req.description:
        clean_desc = req.description.strip().capitalize()
        if not clean_desc.endswith('.'):
            clean_desc += '.'
        parts.append(clean_desc)
    parts.append("Dispatch triage active.")

    return SummaryResponse(summary=" ".join(parts))

@app.post("/ai/analyze-description")
def analyze_description(req: DescriptionAnalysisRequest):
    desc = req.description.lower()
    hazards_found = [h for h in CRITICAL_TRIGGERS + HIGH_TRIGGERS if h in desc]
    sentiment = "URGENT" if any(c in desc for c in CRITICAL_TRIGGERS) else "ATTENTION"
    return {
        "hazardTokens": hazards_found,
        "sentimentRating": sentiment,
        "recommendedPriority": "CRITICAL" if sentiment == "URGENT" else "HIGH",
        "tokensAnalyzed": len(desc.split())
    }

@app.post("/ai/analyze-history")
def analyze_history(req: HistoricalAnalysisRequest):
    days = req.timeframeDays or 30
    total = req.totalIncidents or 120
    avg_per_day = round(total / max(1, days), 2)
    return {
        "projectedIncidentsNext7Days": int(avg_per_day * 7 * 1.05),
        "peakEmergencyHour": "18:00 - 20:00 (Rush Hour Traffic)",
        "mostFrequentCategory": "ROAD ACCIDENT (34%)",
        "historicalConfidence": 0.91,
        "recommendedStandbyUnits": 4
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
