from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional, Dict, Any

from config import config
from intent_classifier import classify_intent
from emergency_classifier import classify_emergency
from response_generator import generate_response, generate_dispatcher_summary

app = FastAPI(
    title=config.SERVICE_NAME,
    description="Multilingual Emergency NLP, Intent Classification, and Assistant Response Generation for JeevanSetu",
    version=config.VERSION
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Pydantic Request & Response Models
class ChatRequest(BaseModel):
    message: str
    userId: Optional[str] = None
    role: Optional[str] = None
    conversationId: Optional[str] = None
    currentLocation: Optional[Dict[str, float]] = None
    language: Optional[str] = "en"

class IntentClassificationRequest(BaseModel):
    text: str

class EmergencyClassificationRequest(BaseModel):
    description: str

class SummarizeRequest(BaseModel):
    incidentCode: Optional[str] = "INCIDENT"
    description: Optional[str] = ""
    injuredCount: Optional[int] = 0
    unconscious: Optional[bool] = False

class DispatcherSummaryRequest(BaseModel):
    incidentCode: Optional[str] = "INCIDENT"
    description: str

@app.get("/health")
def health_check():
    return {
        "status": "HEALTHY",
        "service": config.SERVICE_NAME,
        "version": config.VERSION,
        "supported_languages": ["en", "hi", "te", "ta"]
    }

@app.post("/ai/classify-intent")
def api_classify_intent(req: IntentClassificationRequest):
    intent, confidence = classify_intent(req.text)
    return {
        "intent": intent,
        "confidence": confidence
    }

@app.post("/ai/classify-emergency")
def api_classify_emergency(req: EmergencyClassificationRequest):
    result = classify_emergency(req.description)
    return result

@app.post("/ai/chat")
def api_chat(req: ChatRequest):
    msg = req.message.strip()
    if not msg:
        raise HTTPException(status_code=400, detail="Message cannot be empty")

    lang = (req.language or "en").lower()

    # 1. Intent Detection
    intent, intent_conf = classify_intent(msg)

    # 2. Emergency Classification & Entity Extraction
    emergency_info = classify_emergency(msg)

    # 3. Response & Actions Generation (Localized)
    response_payload = generate_response(intent, emergency_info, msg, lang=lang)

    return {
        "detected_intent": intent,
        "intent_confidence": intent_conf,
        "emergency_category": emergency_info["category"],
        "suggested_priority": emergency_info["priority"],
        "confidence_score": emergency_info["confidence"],
        "extracted_info": emergency_info["extracted_info"],
        "recommended_resources": emergency_info["recommended_resources"],
        "missing_info": emergency_info["missing_info"],
        "suggested_response": response_payload["text"],
        "suggested_actions": response_payload["suggested_actions"],
        "language": lang
    }

@app.post("/ai/dispatcher-summary")
def api_dispatcher_summary(req: DispatcherSummaryRequest):
    emergency_info = classify_emergency(req.description)
    summary = generate_dispatcher_summary(req.description, emergency_info, req.incidentCode or "INCIDENT")
    return summary

@app.post("/ai/summarize-incident")
def api_summarize_incident(req: SummarizeRequest):
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
    return {"summary": " ".join(parts)}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host=config.HOST, port=config.PORT, reload=False)
