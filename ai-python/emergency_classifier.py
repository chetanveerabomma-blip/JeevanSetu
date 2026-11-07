import re
from typing import Dict, Any, List, Optional

# Multilingual keywords for emergency category classification
KEYWORDS = {
    "ACCIDENT": [
        "accident", "crash", "collision", "vehicle", "car", "bike", "truck", "overturned", "hit and run", "bus crash", "run over",
        "दुर्घटना", "हादसा", "टक्कर", "गाड़ी टकराई",
        "ప్రమాదం", "ఢీకొంది", "వాహన ప్రమాదం",
        "விபத்து", "மோதியது", "வாகன விபத்து"
    ],
    "FIRE": [
        "fire", "blaze", "smoke", "burning", "explosion", "flames", "cylinder blast", "short circuit", "inferno",
        "आग", "धुआं", "धमाका", "सिलेंडर ब्लास्ट", "जल रहा",
        "మంటలు", "పొగ", "అగ్నిప్రమాదం", "పేలుడు",
        "தீ", "புகை", "தீவிபத்து", "வெடிப்பு"
    ],
    "MEDICAL": [
        "heart attack", "chest pain", "cardiac", "stroke", "unconscious", "breathing", "diabetic", "seizure", "bleeding", "asthma", "poisoning", "fainted",
        "दिल का दौरा", "सीने में दर्द", "बेहोश", "सांस", "चक्कर", "दौरा",
        "గుండెపోటు", "ఛాతీ నొప్పి", "స్పృహతప్పిన", "శ్వాస", "రక్తస్రావం",
        "நெஞ்சுவலி", "மயக்கம்", "மூச்சுத்திணறல்", "மாரடைப்பு"
    ],
    "BUILDING_COLLAPSE": [
        "collapse", "building", "rubble", "crushed", "trapped under debris", "debris", "roof collapsed", "wall fell",
        "इमारत गिर गई", "मलबे", "दीवार गिरी", "छत गिरी",
        "భవనం కూలింది", "శిథిలాలు",
        "கட்டிடம் இடிந்தது", "இடிபாடுகள்"
    ],
    "BLOOD_EMERGENCY": [
        "blood", "transfusion", "platelets", "donor", "rare blood", "o negative", "hemorrhage", "plasma",
        "खून की जरूरत", "रक्तदान", "प्लेटलेट्स",
        "రక్తం కావాలి", "రక్తదానం",
        "இரத்தம் தேவை", "குருதிக்கொடை"
    ],
    "FLOOD": [
        "flood", "water level", "submerged", "stranded in water", "heavy rain", "overflowing", "deluge",
        "बाढ़", "पानी भर गया", "डूबा हुआ",
        "వరదలు", "నీటి ముంపు",
        "வெள்ளம்", "நீர் பெருக்கெடுத்து"
    ],
    "POLICE": [
        "robbery", "theft", "assault", "fight", "violence", "weapon", "burglary", "riot", "attack", "knife", "gun",
        "लूट", "हमला", "मारपीट", "चोरी", "चाकू", "हथियार",
        "దొంగతనం", "దాడి", "తుపాకీ", "కత్తి",
        "திருட்டு", "தாக்குதல்", "ஆயுதம்"
    ],
    "RESCUE": [
        "trapped", "stuck", "borewell", "lift failure", "drowning", "trench", "confined space", "well",
        "फंसा हुआ", "बोरवेल", "डूब रहा", "लिफ्ट बंद",
        "బోరుబావి", "మునిగిపోవడం", "లిఫ్ట్ ఆగిపోయింది",
        "ஆழ்துளை கிணறு", "மூழ்குதல்"
    ]
}

CRITICAL_TRIGGERS = [
    "unconscious", "not breathing", "severe bleeding", "massive blood",
    "head trauma", "cardiac arrest", "collapsed", "trapped inside",
    "explosion", "major fire", "structural failure", "children trapped", "dying",
    "बेहोश", "सांस नहीं ले रहा", "गंभीर खून", "फंसा हुआ", "मर रहा",
    "స్పృహతప్పిన", "శ్వాస ఆడట్లేదు", "తీవ్ర రక్తస్రావం", "ప్రాణాపాయం",
    "மயக்கமடைந்தார்", "மூச்சு இல்லை", "கடுமையான இரத்தப்போக்கு"
]

HIGH_TRIGGERS = [
    "injured", "fracture", "burns", "smoke inhalation", "chest pain",
    "deep cut", "dizzy", "bleeding", "accident", "flames", "multiple cars",
    "घायल", "चोट", "जला हुआ", "दर्द",
    "గాయపడిన", "గాయం", "కాలిన గాయాలు",
    "காயமடைந்த", "தீக்காயம்"
]

def extract_entities(text: str) -> Dict[str, Any]:
    cleaned = text.strip()
    lower = cleaned.lower()

    # 1. Location Extraction
    location = None
    loc_match = re.search(r"\b(?:near|at|in|on|opposite|behind|around|beside|by|के पास|में|వద్ద|దగ్గర|அருகில்)\s+([A-Za-z0-9\u0900-\u097F\u0C00-\u0C7F\u0B80-\u0BFF\s,\.-]+?)(?:\.|\band\b|\bwith\b|\bcall\b|\bthere\b|\bplease\b|$)", cleaned, re.IGNORECASE)
    if loc_match:
        cand = loc_match.group(1).strip()
        if len(cand) > 2 and cand.lower() not in ["the area", "my place", "here", "यहाँ", "ఇక్కడ", "இங்கே"]:
            location = cand

    # 2. Casualty / Injured count
    injured_count = 0
    count_match = re.search(r"(\d+)\s*(?:people|persons|victims|casualties|injured|passengers|लोग|వ్యక్తులు|நபர்கள்)", lower)
    if count_match:
        injured_count = int(count_match.group(1))
    elif any(w in lower for w in ["one person", "a person", "someone", "a man", "a woman", "a child", "एक व्यक्ति", "ఒకరు", "ஒருவர்"]):
        injured_count = 1
    elif any(w in lower for w in ["two people", "both", "दो लोग", "ఇద్దరు", "இருவர்"]):
        injured_count = 2
    elif any(w in lower for w in ["three people", "तीन लोग", "ముగ్గురు", "மூவர்"]):
        injured_count = 3

    # 3. Clinical / Hazard Triage Flags
    unconscious = any(t in lower for t in ["unconscious", "fainted", "unresponsive", "passed out", "not waking", "बेहोश", "స్పృహతప్పిన", "மயக்கம்"])
    severe_bleeding = any(t in lower for t in ["severe bleeding", "massive bleeding", "blood loss", "heavy bleeding", "bleeding profusely", "खून बह रहा", "తీవ్ర రక్తస్రావం", "இரத்தப்போக்கு"])
    trapped = any(t in lower for t in ["trapped", "stuck inside", "pinned", "under debris", "locked inside", "cannot get out", "फंसा हुआ", "చిక్కుకున్న", "சிக்கிய"])
    fire_involved = any(t in lower for t in ["fire", "blaze", "burning", "smoke", "flames", "explosion", "आग", "मంటలు", "தீ"])

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
        best_cat = "MEDICAL" if any(w in lower for w in ["pain", "sick", "doctor", "help", "patient", "दर्द", "నొప్పి", "வலி"]) else "ACCIDENT"

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
    if entities["injured_count"] == 0 and not any(w in lower for w in ["no injury", "no one hurt", "zero injured", "कोई घायल नहीं", "ఎవరూ గాయపడలేదు", "யாரும் காயமடையவில்லை"]):
        missing_info.append("casualty_count")

    return {
        "category": best_cat,
        "priority": priority,
        "confidence": confidence,
        "extracted_info": entities,
        "recommended_resources": resources,
        "missing_info": missing_info
    }
