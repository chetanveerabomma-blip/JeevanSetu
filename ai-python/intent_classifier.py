import re
from typing import Dict, Any, Tuple

# Multilingual Intent Patterns (English, Hindi, Telugu, Tamil)
INTENT_PATTERNS = {
    "REPORT_EMERGENCY": [
        # English
        r"\b(report|emergency|accident|crash|fire|collision|bleeding|unconscious|trapped|casualty|injured|stroke|heart\s*attack|collapse|drowning)\b",
        r"\b(someone\s+(is\s+)?(hurt|dying|injured|bleeding|unconscious))\b",
        r"\b(there\s+is\s+a\s+(fire|accident|crash|blast|explosion))\b",
        r"\b(need\s+(help|rescue|urgent\s+help|sos))\b",
        # Hindi
        r"(दुर्घटना|हादसा|आग|खून|बेहोश|मदद|घायल|दिल\s*का\s*दौरा|आपातकाल|आपातकालीन|फंसा\s*हुआ|जल\s*रहा)",
        # Telugu
        r"(ప్రమాదం|మంటలు|రక్తం|స్పృహతప్పిన|సహాయం|గాయపడిన|గుండెపోటు|అత్యవసర|చిక్కుకున్న)",
        # Tamil
        r"(விபத்து|தீ|இரத்தம்|மயக்கம்|உதவி|காயமடைந்த|நெஞ்சுவலி|அவசர|சிக்கிய)"
    ],
    "FIND_HOSPITAL": [
        # English
        r"\b(hospital|icu|er|trauma\s*center|clinic|emergency\s*room|bed\s*availability|ventilator)\b",
        r"\b(find|nearest|closest|nearby)\s+hospital\b",
        r"\b(where\s+can\s+i\s+(admit|take)\s+patient)\b",
        # Hindi
        r"(अस्पताल|हॉस्पिटल|दवाखाना|आईसीयू|बेड|इलाज|नजदीकी\s*अस्पताल)",
        # Telugu
        r"(ఆసుపత్రి|హాస్పిటల్|బెడ్|ఐసీయూ|వైద్యం|దగ్గరలోని\s*ఆసుపత్రి)",
        # Tamil
        r"(மருத்துவமனை|ஆஸ்பத்திரி|படுக்கை|சிகிச்சை|அருகிலுள்ள\s*மருத்துவமனை)"
    ],
    "FIND_AMBULANCE": [
        # English
        r"\b(ambulance|als|bls|medical\s*vehicle|paramedic|patient\s*transport)\b",
        r"\b(call|need|dispatch|find|send)\s+(an\s+)?ambulance\b",
        r"\b(ambulance\s+(availability|status|near\s+me))\b",
        # Hindi
        r"(एम्बुलेंस|एंबुलेंस|रोगी\s*वाहन)",
        # Telugu
        r"(అంబులెన్స్|ఆంబులెన్స్|అత్యవసర\s*వాహనం)",
        # Tamil
        r"(ஆம்புலன்ஸ்|அம்புலன்ஸ்)"
    ],
    "CHECK_STATUS": [
        # English
        r"\b(status|track|tracking|check\s*my|incident\s*status)\b",
        r"\bjs-\d{4}-\d+\b",
        r"\b(where\s+is\s+(the\s+)?ambulance|eta|arrival\s+time)\b",
        r"\b(check\s+emergency\s+status)\b",
        # Hindi
        r"(स्थिति|ट्रैक|अपडेट|घटना\s*की\s*स्थिति)",
        # Telugu
        r"(స్థితి|ట్రాక్|కేసు\s*వివరాలు)",
        # Tamil
        r"(நிலை|கண்காணிப்பு|தற்போதைய\s*நிலை)"
    ],
    "EMERGENCY_GUIDELINES": [
        # English
        r"\b(guide|guideline|guidelines|first\s*aid|cpr|what\s+to\s+do|protocol|safety\s*tips|instructions)\b",
        r"\b(how\s+to\s+(handle|treat|stop\s+bleeding|give\s+cpr))\b",
        # Hindi
        r"(प्राथमिक\s*उपचार|दिशानिर्देश|सीपीआर|नियम|सुरक्षा\s*सुझाव)",
        # Telugu
        r"(ప్రథమ\s*చికిత్స|మార్గదర్శకాలు|సీపీఆర్)",
        # Tamil
        r"(முதலுதவி|வழிகாட்டுதல்|சிபிஆர்)"
    ],
    "GREETING": [
        # English
        r"^(hi|hello|hey|greetings|good\s*(morning|afternoon|evening))\b",
        r"\b(who\s+are\s+you|what\s+can\s+you\s+do|help\s*me)\b",
        # Hindi
        r"(नमस्ते|नमस्कार|प्रणाम|हेलो|हाय)",
        # Telugu
        r"(నమస్కారం|హలో|బాగున్నారా)",
        # Tamil
        r"(வணக்கம்|ஹலோ)"
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
                scores[intent] += 1.8 * len(matches)
            elif any(w in cleaned for w in pat.replace(r"\b", "").replace(r"\s*", " ").split("|") if len(w) > 3):
                scores[intent] += 0.4

    # Disambiguation logic
    if scores["REPORT_EMERGENCY"] > 0:
        if any(w in cleaned for w in ["hospital", "clinic", "icu", "अस्पताल", "ఆసుపత్రి", "மருத்துவமனை"]) and not any(w in cleaned for w in ["accident", "fire", "bleeding", "unconscious", "दुर्घटना", "ప్రమాదం", "விபத்து"]):
            scores["FIND_HOSPITAL"] += 2.0
        elif any(w in cleaned for w in ["ambulance", "als", "paramedic", "एम्बुलेंस", "అంబులెన్స్", "ஆம்புலன்ஸ்"]) and any(w in cleaned for w in ["find", "available", "where", "कहां", "ఎక్కడ", "எங்கே"]):
            scores["FIND_AMBULANCE"] += 2.0

    best_intent = max(scores, key=lambda k: scores[k])
    max_score = scores[best_intent]

    if max_score == 0.0:
        return "GENERAL_ASSISTANCE", 0.50

    confidence = min(0.99, max(0.65, round(0.65 + (max_score * 0.1), 2)))
    return best_intent, confidence
