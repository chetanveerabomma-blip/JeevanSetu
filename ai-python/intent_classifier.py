import re
from typing import Dict, Any, Tuple

INTENT_PATTERNS = {
    "REPORT_EMERGENCY": [
        r"\b(report|emergency|accident|crash|fire|collision|bleeding|unconscious|trapped|casualty|injured|stroke|heart\s*attack|collapse|drowning)\b",
        r"\b(someone\s+(is\s+)?(hurt|dying|injured|bleeding|unconscious))\b",
        r"\b(there\s+is\s+a\s+(fire|accident|crash|blast|explosion))\b",
        r"\b(need\s+(help|rescue|urgent\s+help))\b"
    ],
    "FIND_HOSPITAL": [
        r"\b(hospital|icu|er|trauma\s*center|clinic|emergency\s*room|bed\s*availability|ventilator)\b",
        r"\b(find|nearest|closest|nearby)\s+hospital\b",
        r"\b(where\s+can\s+i\s+(admit|take)\s+patient)\b"
    ],
    "FIND_AMBULANCE": [
        r"\b(ambulance|als|bls|medical\s*vehicle|paramedic|patient\s*transport)\b",
        r"\b(call|need|dispatch|find|send)\s+(an\s+)?ambulance\b",
        r"\b(ambulance\s+(availability|status|near\s+me))\b"
    ],
    "CHECK_STATUS": [
        r"\b(status|track|tracking|check\s*my|incident\s*status)\b",
        r"\bjs-\d{4}-\d+\b",
        r"\b(where\s+is\s+(the\s+)?ambulance|eta|arrival\s+time)\b",
        r"\b(check\s+emergency\s+status)\b"
    ],
    "EMERGENCY_GUIDELINES": [
        r"\b(guide|guideline|guidelines|first\s*aid|cpr|what\s+to\s+do|protocol|safety\s*tips|instructions)\b",
        r"\b(how\s+to\s+(handle|treat|stop\s+bleeding|give\s+cpr))\b"
    ],
    "GREETING": [
        r"^(hi|hello|hey|greetings|good\s*(morning|afternoon|evening))\b",
        r"\b(who\s+are\s+you|what\s+can\s+you\s+do|help\s*me)\b"
    ]
}

def classify_intent(text: str) -> Tuple[str, float]:
    cleaned = text.strip().lower()
    
    # Priority check for explicit incident codes (e.g., JS-2026-000184)
    if re.search(r"\bjs-\d{4}-\d+\b", cleaned):
        return "CHECK_STATUS", 0.98

    # Match scores
    scores: Dict[str, float] = {k: 0.0 for k in INTENT_PATTERNS}
    for intent, patterns in INTENT_PATTERNS.items():
        for pat in patterns:
            matches = re.findall(pat, cleaned)
            if matches:
                scores[intent] += 1.5 * len(matches)
            elif any(w in cleaned for w in pat.replace(r"\b", "").replace(r"\s*", " ").split("|") if len(w) > 3):
                scores[intent] += 0.4

    # Disambiguation logic
    if scores["REPORT_EMERGENCY"] > 0:
        if any(w in cleaned for w in ["hospital", "clinic", "icu"]) and not any(w in cleaned for w in ["accident", "fire", "bleeding", "unconscious"]):
            scores["FIND_HOSPITAL"] += 2.0
        elif any(w in cleaned for w in ["ambulance", "als", "paramedic"]) and any(w in cleaned for w in ["find", "available", "where"]):
            scores["FIND_AMBULANCE"] += 2.0

    best_intent = max(scores, key=lambda k: scores[k])
    max_score = scores[best_intent]

    if max_score == 0.0:
        return "GENERAL_ASSISTANCE", 0.50

    confidence = min(0.99, max(0.65, round(0.65 + (max_score * 0.1), 2)))
    return best_intent, confidence
