import re
from typing import Dict, Any, List, Optional

KEYWORDS = {
    "ACCIDENT": ["accident", "crash", "collision", "vehicle", "car", "bike", "truck", "overturned", "hit and run", "bus crash", "run over"],
    "FIRE": ["fire", "blaze", "smoke", "burning", "explosion", "flames", "cylinder blast", "short circuit", "inferno"],
    "MEDICAL": ["heart attack", "chest pain", "cardiac", "stroke", "unconscious", "breathing", "diabetic", "seizure", "bleeding", "asthma", "poisoning", "fainted"],
    "BUILDING_COLLAPSE": ["collapse", "building", "rubble", "crushed", "trapped under debris", "debris", "roof collapsed", "wall fell"],
    "BLOOD_EMERGENCY": ["blood", "transfusion", "platelets", "donor", "rare blood", "o negative", "hemorrhage", "plasma"],
    "FLOOD": ["flood", "water level", "submerged", "stranded in water", "heavy rain", "overflowing", "deluge"],
    "POLICE": ["robbery", "theft", "assault", "fight", "violence", "weapon", "burglary", "riot", "attack", "knife", "gun"],
    "RESCUE": ["trapped", "stuck", "borewell", "lift failure", "drowning", "trench", "confined space", "well"]
}

CRITICAL_TRIGGERS = [
    "unconscious", "not breathing", "severe bleeding", "massive blood",
    "head trauma", "cardiac arrest", "collapsed", "trapped inside",
    "explosion", "major fire", "structural failure", "children trapped", "dying"
]

HIGH_TRIGGERS = [
    "injured", "fracture", "burns", "smoke inhalation", "chest pain",
    "deep cut", "dizzy", "bleeding", "accident", "flames", "multiple cars"
]

def extract_entities(text: str) -> Dict[str, Any]:
    cleaned = text.strip()
    lower = cleaned.lower()

    # 1. Location Extraction
    location = None
    loc_match = re.search(r"\b(?:near|at|in|on|opposite|behind|around|beside|by)\s+([A-Za-z0-9\s,\.-]+?)(?:\.|\band\b|\bwith\b|\bcall\b|\bthere\b|\bplease\b|$)", cleaned, re.IGNORECASE)
    if loc_match:
        cand = loc_match.group(1).strip()
        if len(cand) > 2 and cand.lower() not in ["the area", "my place", "here"]:
            location = cand

    # 2. Casualty / Injured count
    injured_count = 0
    count_match = re.search(r"(\d+)\s*(?:people|persons|victims|casualties|injured|passengers)", lower)
    if count_match:
        injured_count = int(count_match.group(1))
    elif any(w in lower for w in ["one person", "a person", "someone", "a man", "a woman", "a child"]):
        injured_count = 1
    elif any(w in lower for w in ["two people", "both"]):
        injured_count = 2

    # 3. Clinical / Hazard Triage Flags
    unconscious = any(t in lower for t in ["unconscious", "fainted", "unresponsive", "passed out", "not waking"])
    severe_bleeding = any(t in lower for t in ["severe bleeding", "massive bleeding", "blood loss", "heavy bleeding", "bleeding profusely"])
    trapped = any(t in lower for t in ["trapped", "stuck inside", "pinned", "under debris", "locked inside", "cannot get out"])
    fire_involved = any(t in lower for t in ["fire", "blaze", "burning", "smoke", "flames", "explosion"])

    # 4. Incident Code (if mentioned)
    incident_code_match = re.search(r"\b(JS-\d{4}-\d+)\b", text, re.IGNORECASE)
    incident_code = incident_code_match.group(1).upper() if incident_code_match else None

    return {
        "location": location,
        "injured_count": injured_count,
        "unconscious": unconscious,
        "severe_bleeding": severe_bleeding,
        "trapped": trapped,
        "fire_involved": fire_involved,
        "incident_code": incident_code
    }

def classify_emergency(text: str) -> Dict[str, Any]:
    lower = text.lower()
    entities = extract_entities(text)

    # Determine Category
    scores = {cat: 0 for cat in KEYWORDS}
    for cat, words in KEYWORDS.items():
        for w in words:
            if re.search(r"\b" + re.escape(w) + r"\b", lower):
                scores[cat] += 2
            elif w in lower:
                scores[cat] += 1

    best_cat = max(scores, key=lambda k: scores[k])
    if scores[best_cat] == 0:
        best_cat = "MEDICAL" if any(w in lower for w in ["pain", "sick", "doctor", "help", "patient"]) else "ACCIDENT"

    # Suggested Priority
    is_critical = (
        entities["unconscious"] or
        entities["severe_bleeding"] or
        entities["trapped"] or
        best_cat == "BUILDING_COLLAPSE" or
        (best_cat == "FIRE" and (entities["injured_count"] > 0 or entities["trapped"])) or
        entities["injured_count"] >= 3 or
        any(t in lower for t in CRITICAL_TRIGGERS)
    )

    is_high = (
        entities["injured_count"] >= 1 or
        best_cat in ["ACCIDENT", "FIRE", "FLOOD"] or
        any(t in lower for t in HIGH_TRIGGERS)
    )

    if is_critical:
        priority = "CRITICAL"
        confidence = 0.96
    elif is_high:
        priority = "HIGH"
        confidence = 0.88
    else:
        priority = "MEDIUM"
        confidence = 0.80

    # Recommended Resources
    resources = []
    if best_cat == "ACCIDENT":
        resources = ["AMBULANCE", "POLICE"]
        if is_critical:
            resources.append("HOSPITAL")
    elif best_cat == "FIRE":
        resources = ["FIRE_UNIT", "POLICE"]
        if is_critical or entities["injured_count"] > 0:
            resources.append("AMBULANCE")
    elif best_cat == "BUILDING_COLLAPSE":
        resources = ["FIRE_UNIT", "AMBULANCE", "POLICE", "HOSPITAL"]
    elif best_cat == "BLOOD_EMERGENCY":
        resources = ["HOSPITAL", "BLOOD_BANK"]
    elif best_cat == "MEDICAL":
        resources = ["AMBULANCE", "HOSPITAL"]
    elif best_cat == "POLICE":
        resources = ["POLICE"]
        if is_critical or entities["injured_count"] > 0:
            resources.append("AMBULANCE")
    elif best_cat == "FLOOD":
        resources = ["FIRE_UNIT", "AMBULANCE", "POLICE", "SHELTER", "VOLUNTEER"]
    else:
        resources = ["AMBULANCE", "POLICE"]

    # Identify Missing Information
    missing_info = []
    if not entities["location"]:
        missing_info.append("location")
    if entities["injured_count"] == 0 and not any(w in lower for w in ["no injury", "no one hurt", "zero injured"]):
        missing_info.append("casualty_count")

    return {
        "category": best_cat,
        "priority": priority,
        "confidence": confidence,
        "extracted_info": entities,
        "recommended_resources": resources,
        "missing_info": missing_info
    }
