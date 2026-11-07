from typing import Dict, Any, List

EMERGENCY_GUIDELINES = {
    "en": {
        "ROAD_ACCIDENT": (
            "🚨 **Road Accident Emergency Protocol:**\n"
            "1. **Safety First**: Park your vehicle safely, turn on hazard lights, and avoid standing on active lanes.\n"
            "2. **Check Unresponsiveness**: Do NOT move injured victims unless there is immediate danger of fire or explosion.\n"
            "3. **Control Bleeding**: Apply firm, clean direct pressure with sterile cloth.\n"
            "4. **Keep Airway Clear**: Check breathing; do not remove helmets of motorcycle riders unless airway is blocked.\n"
            "5. **Call Immediate Emergency Services**: Dial 112 (National Unified) or 108 (Ambulance)."
        ),
        "FIRE": (
            "🔥 **Fire & Burn Emergency Protocol:**\n"
            "1. **Evacuate Immediately**: Sound alarms, use stairs (NEVER elevators), stay low under smoke.\n"
            "2. **Stop, Drop, and Roll**: If clothes catch fire, smother flames immediately.\n"
            "3. **Cool Minor Burns**: Use clean running room-temperature water for 10-15 minutes (no ice, butter, or paste).\n"
            "4. **Do Not Inhale Toxic Fumes**: Cover mouth and nose with a damp cloth.\n"
            "5. **Call Fire Response**: Dial 101 or 112 immediately."
        ),
        "MEDICAL": (
            "🚑 **Critical Medical Emergency Protocol:**\n"
            "1. **Chest Pain / Heart Attack**: Keep patient calm, seated with knees bent; loosen tight clothing.\n"
            "2. **Loss of Consciousness**: Place in recovery position on their side to keep airway open if breathing.\n"
            "3. **Choking**: Perform 5 back blows followed by 5 abdominal thrusts (Heimlich maneuver).\n"
            "4. **Seizure**: Clear surroundings of sharp objects; do NOT force anything into their mouth.\n"
            "5. **Official Dispatch**: Dial 108 or 112 for immediate paramedic dispatch."
        )
    },
    "hi": {
        "ROAD_ACCIDENT": (
            "🚨 **सड़क दुर्घटना आपातकालीन प्रोटोकॉल:**\n"
            "1. **सुरक्षा पहले**: वाहन को सुरक्षित पार्क करें और हैजर्ड लाइट चालू करें।\n"
            "2. **घायलों को हिलाएं नहीं**: आग या विस्फोट के खतरे के बिना घायल को न हिलाएं।\n"
            "3. **रक्तस्राव रोकें**: साफ कपड़े से घाव पर सीधा दबाव बनाएं।\n"
            "4. **हेलमेट न हटाएं**: जब तक सांस में रुकावट न हो, हेलमेट न उतारें।\n"
            "5. **हेल्पलाइन**: 112 या 108 पर तुरंत कॉल करें।"
        ),
        "FIRE": (
            "🔥 **आग और जलन आपातकालीन प्रोटोकॉल:**\n"
            "1. **तुरंत बाहर निकलें**: लिफ्ट का उपयोग न करें, सीढ़ियों से जाएं।\n"
            "2. **रुको, झुको और लुढ़को**: कपड़ों में आग लगने पर जमीन पर लुढ़कें।\n"
            "3. **ठंडा पानी डालें**: जले हुए हिस्से पर 10-15 मिनट साफ ठंडा पानी डालें (बर्फ न लगाएं)।\n"
            "4. **फायर ब्रिगेड**: 101 या 112 पर तुरंत कॉल करें।"
        ),
        "MEDICAL": (
            "🚑 **गंभीर चिकित्सा आपातकालीन प्रोटोकॉल:**\n"
            "1. **सीने में दर्द / दिल का दौरा**: मरीज को शांति से बैठाएं, तंग कपड़े ढीले करें।\n"
            "2. **बेहोशी**: मरीज को एक करवट लिटाएं ताकि सांस की नली खुली रहे।\n"
            "3. **सीपीआर**: अगर सांस नहीं चल रही है तो 100-120 प्रति मिनट की गति से सीने पर दबाव दें।\n"
            "4. **एम्बुलेंस**: तुरंत 108 या 112 डायल करें।"
        )
    },
    "te": {
        "ROAD_ACCIDENT": (
            "🚨 **రోడ్డు ప్రమాద అత్యవసర మార్గదర్శకాలు:**\n"
            "1. **భద్రత ముఖ్యం**: వాహనాన్ని పక్కకు ఆపి వార్నింగ్ లైట్లు ఆన్ చేయండి.\n"
            "2. **గాయపడిన వారిని కదల్చవద్దు**: అగ్నిప్రమాద ప్రమాదం లేకపోతే క్షతగాత్రులను కదల్చకండి.\n"
            "3. **రక్తస్రావం ఆపండి**: శుభ్రమైన గుడ్డతో గాయంపై గట్టిగా ఒత్తిడి ఉంచండి.\n"
            "4. **హెల్మెట్ తీయవద్దు**: శ్వాస ఆడితే హెల్మెట్ తీయకండి.\n"
            "5. **అత్యవసర కాల్**: వెంటనే 112 లేదా 108 కు కాల్ చేయండి."
        ),
        "FIRE": (
            "🔥 **అగ్నిప్రమాద మార్గదర్శకాలు:**\n"
            "1. **వెంటనే బయటకు రండి**: లిఫ్ట్ వాడవద్దు, మెట్లు మాత్రమే ఉపయోగించండి.\n"
            "2. **కాలిన గాయాలపై చల్లని నీరు**: 10-15 నిమిషాలు చల్లటి నీరు పోయండి.\n"
            "3. **ఫైర్ సర్వీస్**: 101 లేదా 112 కి కాల్ చేయండి."
        ),
        "MEDICAL": (
            "🚑 **వైద్య అత్యవసర మార్గదర్శకాలు:**\n"
            "1. **గుండెపోటు లక్షణాలు**: రోగిని ప్రశాంతంగా కూర్చోబెట్టి వదులుగా ఉండే బట్టలు ఉంచండి.\n"
            "2. **స్పృహతప్పితే**: రోగిని ఒక పక్కకు తిప్పి పడుకోబెట్టండి.\n"
            "3. **అంబులెన్స్**: వెంటనే 108 లేదా 112 కి కాల్ చేయండి."
        )
    },
    "ta": {
        "ROAD_ACCIDENT": (
            "🚨 **சாலை விபத்து அவசர நெறிமுறை:**\n"
            "1. **பாதுகாப்பு முதலில்**: வாகனத்தை பாதுகாப்பாக நிறுத்தி எச்சரிக்கை விளக்குகளை போடவும்.\n"
            "2. **காயமடைந்தவரை அசைக்காதீர்கள்**: தீ விபத்து அபாயம் இல்லாத வரை பாதிக்கப்பட்டவரை நகர்த்த வேண்டாம்.\n"
            "3. **இரத்தப்போக்கை கட்டுப்படுத்துங்கள்**: சுத்தமான துணியால் காயத்தின் மீது அழுத்தம் கொடுக்கவும்.\n"
            "4. **ஹெல்மெட்டை கழற்றாதீர்கள்**: மூச்சுத் திணறல் இல்லாவிட்டால் ஹெல்மெட்டை கழற்ற வேண்டாம்.\n"
            "5. **அவசர உதவி**: 112 அல்லது 108 ஐ உடனடியாக அழைக்கவும்."
        ),
        "FIRE": (
            "🔥 **தீ விபத்து அவசர நெறிமுறை:**\n"
            "1. **உடனடியாக வெளியேறுங்கள்**: லிப்ட் பயன்படுத்தாதீர்கள், படிக்கட்டுகளைப் பயன்படுத்துங்கள்.\n"
            "2. **தீக்காயங்களுக்கு குளிர்ந்த நீர்**: 10-15 நிமிடங்கள் குளிர்ந்த நீரில் கழுவவும்.\n"
            "3. **தீயணைப்பு துறை**: 101 அல்லது 112 ஐ அழைக்கவும்."
        ),
        "MEDICAL": (
            "🚑 **மருத்துவ அவசர நெறிமுறை:**\n"
            "1. **மாரடைப்பு / நெஞ்சுவலி**: நோயாளியை அமைதியாக உட்கார வைக்கவும்.\n"
            "2. **மயக்கமடைந்தால்**: சுவாசப்பாதை திறந்திருக்க ஒரு பக்கமாக படுக்க வைக்கவும்.\n"
            "3. **ஆம்புலன்ஸ்**: உடனடியாக 108 அல்லது 112 ஐ அழைக்கவும்."
        )
    }
}

LOCALIZED_GREETINGS = {
    "en": "Hello! I am **JeevanSetu AI – Emergency Intelligence Assistant**.\n\nI can assist you with:\n• **Immediate Emergency Reporting**\n• **Finding Available Ambulances**\n• **Locating Nearest Hospitals & ICU Beds**\n• **Tracking Active Incident Status**\n• **First-Aid Protocols**\n\nHow can I help right now?",
    "hi": "नमस्ते! मैं **जीवनसेतु AI – आपातकालीन खुफिया सहायक** हूँ।\n\nमैं आपकी इसमें सहायता कर सकता हूँ:\n• **आपातकाल की तुरंत रिपोर्टिंग**\n• **उपलब्ध एम्बुलेंस की खोज**\n• **निकटतम अस्पताल और आईसीयू बेड**\n• **घटना की स्थिति ट्रैकिंग**\n• **प्राथमिक चिकित्सा नियम**\n\nमैं आपकी क्या मदद कर सकता हूँ?",
    "te": "నమస్కారం! నేను **జీవన్‌సేతు AI – అత్యవసర సహాయకుని**.\n\nనేను మీకు సహాయం చేయగలను:\n• **అత్యవసర సంఘటనల నమోదు**\n• **సమీప అంబులెన్స్ శోధన**\n• **హాస్పిటల్స్ & ఐసీయూ బెడ్స్ సమాచారం**\n• **కేసు స్థితి ట్రాకింగ్**\n• **ప్రథమ చికిత్స మార్గదర్శకాలు**\n\nనేను మీకు ఎలా సహాయపడగలను?",
    "ta": "வணக்கம்! நான் **ஜீவன்சேது AI – அவசர புலனாய்வு உதவியாளர்**.\n\nநான் உதவ முடியும்:\n• **உடனடி அவசர அறிக்கை**\n• **ஆம்புலன்ஸ் இருப்பு விவரம்**\n• **அருகிலுள்ள மருத்துவமனைகள் & ஐசியூ படுக்கைகள்**\n• **விபத்து நிலை கண்காணிப்பு**\n• **முதலுதவி நெறிமுறைகள்**\n\nநான் உங்களுக்கு எவ்வாறு உதவ முடியும்?"
}

LOCALIZED_ACTIONS = {
    "en": ["Report an Emergency", "Find Nearest Hospital", "Find Available Ambulance", "Emergency Guidelines"],
    "hi": ["आपातकाल रिपोर्ट करें", "निकटतम अस्पताल", "उपलब्ध एम्बुलेंस", "प्राथमिक उपचार नियम"],
    "te": ["అత్యవసర రిపోర్ట్", "సమీప ఆసుపత్రి", "అందుబాటులోని అంబులెన్స్", "ప్రథమ చికిత్స"],
    "ta": ["அவசர அறிக்கை", "அருகிலுள்ள மருத்துவமனை", "கிடைக்கும் ஆம்புலன்ஸ்", "முதலுதவி வழிகாட்டுதல்"]
}

def generate_response(intent: str, emergency_data: Dict[str, Any], user_message: str, lang: str = "en") -> Dict[str, Any]:
    lang = lang.lower() if lang in ["en", "hi", "te", "ta"] else "en"
    extracted = emergency_data.get("extracted_info", {})
    category = emergency_data.get("category", "MEDICAL")
    priority = emergency_data.get("priority", "MEDIUM")
    resources = emergency_data.get("recommended_resources", ["AMBULANCE"])
    missing = emergency_data.get("missing_info", [])

    suggested_actions = LOCALIZED_ACTIONS.get(lang, LOCALIZED_ACTIONS["en"])

    if intent == "GREETING":
        message = LOCALIZED_GREETINGS.get(lang, LOCALIZED_GREETINGS["en"])

    elif intent == "FIND_HOSPITAL":
        if lang == "hi":
            message = "🏥 आपके स्थान के निकटतम आपातकालीन अस्पतालों की सूची नीचे दी गई है। वास्तविक आईसीयू और इमरजेंसी बेड की उपलब्धता सत्यापित है।"
        elif lang == "te":
            message = "🏥 మీ ప్రాంతంలోని సమీప అత్యవసర ఆసుపత్రులు కింద ఇవ్వబడ్డాయి. అందుబాటులోని ఐసీయూ మరియు ఎమర్జెన్సీ బెడ్స్ వివరాలు ధృవీకరించబడ్డాయి."
        elif lang == "ta":
            message = "🏥 உங்கள் இருப்பிடத்திற்கு அருகிலுள்ள அவசர சிகிச்சை மருத்துவமனைகள் கீழே பட்டியலிடப்பட்டுள்ளன. படுக்கை இருப்பு சரிபார்க்கப்பட்டது."
        else:
            message = "🏥 Verified network hospitals near your location are listed below with live ICU and emergency bed availability."

    elif intent == "FIND_AMBULANCE":
        if lang == "hi":
            message = "🚑 आपके क्षेत्रीय क्षेत्र में तैयार एम्बुलेंस इकाइयां खोजी जा रही हैं। आपातकालीन नियंत्रण केंद्र त्वरित प्रेषण के लिए सक्रिय है।"
        elif lang == "te":
            message = "🚑 మీ ప్రాంతంలో అందుబాటులో ఉన్న అంబులెన్స్ యూనిట్లు గుర్తించబడ్డాయి. కంట్రోల్ రూమ్ ద్వారా తక్షణ రవాణా సాధ్యం."
        elif lang == "ta":
            message = "🚑 உங்கள் பகுதியில் உள்ள தயார் நிலை ஆம்புலன்ஸ் பிரிவுகள் கண்டறியப்பட்டுள்ளன."
        else:
            message = f"🚑 Locating ready ambulance units within regional zone. Identified recommended emergency response: {', '.join(resources)}."

    elif intent == "CHECK_STATUS":
        inc = extracted.get("incident_code")
        if inc:
            if lang == "hi":
                message = f"📋 घटना **{inc}** का वास्तविक समय का विवरण प्राप्त हो रहा है। रिस्पॉन्डर का लाइव स्टेटस और ईटीఏ ट्रैक किया जा रहा है।"
            elif lang == "te":
                message = f"📋 సంఘటన **{inc}** వివరాలు పొందబడుతున్నాయి. రెస్పాండర్ యూనిట్ లైవ్ స్టేటస్ ట్రాక్ చేయబడుతోంది."
            elif lang == "ta":
                message = f"📋 சம்பவம் **{inc}** இன் நேரடி நிலை கண்காணிக்கப்படுகிறது."
            else:
                message = f"📋 Retrieving live operational telemetry for incident **{inc}**. Responder progress and ETA are actively tracked."
        else:
            if lang == "hi":
                message = "📋 कृपया अपनी घटना का कोड (उदा. JS-2026-XXXX) दर्ज करें ताकि लाइव स्थिति जांची जा सके।"
            elif lang == "te":
                message = "📋 మీ కేసు కోడ్ (ఉదా. JS-2026-XXXX) తెలపండి."
            elif lang == "ta":
                message = "📋 உங்கள் விபத்து குறியீட்டை (எ.கா. JS-2026-XXXX) குறிப்பிடவும்."
            else:
                message = "📋 Please provide your incident code (e.g. JS-2026-XXXX) or select an active emergency from your dashboard."

    elif intent == "EMERGENCY_GUIDELINES":
        guidelines_for_lang = EMERGENCY_GUIDELINES.get(lang, EMERGENCY_GUIDELINES["en"])
        protocol_key = "ROAD_ACCIDENT" if category == "ACCIDENT" else ("FIRE" if category == "FIRE" else "MEDICAL")
        message = guidelines_for_lang.get(protocol_key, guidelines_for_lang["MEDICAL"])

    elif intent == "REPORT_EMERGENCY":
        loc_text = extracted.get("location") or ("GPS Location Captured" if lang == "en" else "जीपीएस स्थान दर्ज किया गया")
        inj = extracted.get("injured_count", 0)

        if lang == "hi":
            message = (
                f"🚨 **आपातकालीन घटना दर्ज की जा रही है ({category})**\n\n"
                f"प्राथमिकता स्तर: **{priority}**\n"
                f"स्थान: **{loc_text}**\n"
                f"घायल संख्या: **{inj}**\n\n"
                "विवरण नीचे दिए गए ड्राफ्ट कार्ड में तैयार है। कमांड सेंटर और एम्बुलेंस को तुरंत सूचित करने के लिए **पुष्टि करें (Confirm & Dispatch)** पर टैप करें।"
            )
        elif lang == "te":
            message = (
                f"🚨 **అత్యవసర సంఘటన గుర్తింపు ({category})**\n\n"
                f"ప్రాధాన్యత: **{priority}**\n"
                f"ప్రదేశం: **{loc_text}**\n"
                f"బాధితుల సంఖ్య: **{inj}**\n\n"
                "దయచేసి కింద ఉన్న డ్రాఫ్ట్ పరిశీలించి, కంట్రోల్ రూమ్‌కు పంపడానికి **Confirm & Dispatch** పై క్లిక్ చేయండి."
            )
        elif lang == "ta":
            message = (
                f"🚨 **அவசர சம்பவம் கண்டறியப்பட்டது ({category})**\n\n"
                f"முன்னுரிமை: **{priority}**\n"
                f"இடம்: **{loc_text}**\n"
                f"பாதிக்கப்பட்டோர்: **{inj}**\n\n"
                "விவரங்களை உறுதிசெய்து அனுப்ப **Confirm & Dispatch** பொத்தானை அழுத்தவும்."
            )
        else:
            message = (
                f"🚨 **Emergency Detected ({category}) — Priority [{priority}]**\n\n"
                f"• **Location:** {loc_text}\n"
                f"• **Casualties:** {inj}\n"
                f"• **Recommended Dispatch:** {', '.join(resources)}\n\n"
                "Review the triage draft below and tap **Confirm & Dispatch Emergency** to immediately alert regional responders."
            )

    else:
        if lang == "hi":
            message = "मैं आपकी मदद के लिए तैयार हूँ। यदि कोई आपात स्थिति है, तो बताएं क्या हुआ है या नीचे दिए गए विकल्पों में से चुनें।"
        elif lang == "te":
            message = "నేను సహాయం చేయడానికి సిద్ధంగా ఉన్నాను. ఏదైనా అత్యవసర పరిస్థితి ఉంటే వివరాలు తెలియజేయండి."
        elif lang == "ta":
            message = "நான் உதவ தயாராக உள்ளேன். ஏதேனும் அவசரநிலை இருந்தால் தெரிவிக்கவும்."
        else:
            message = "I am ready to assist. If this is an emergency, describe the situation or select a quick option below."

    return {
        "text": message,
        "suggested_actions": suggested_actions
    }

def generate_dispatcher_summary(original_text: str, emergency_data: Dict[str, Any], incident_code: str = "INCIDENT") -> Dict[str, Any]:
    extracted = emergency_data.get("extracted_info", {})
    category = emergency_data.get("category", "MEDICAL")
    priority = emergency_data.get("priority", "HIGH")

    confirmed_facts = []
    if extracted.get("location"):
        confirmed_facts.append(f"Reported Location/Landmark: {extracted['location']}")
    if extracted.get("injured_count", 0) > 0:
        confirmed_facts.append(f"Reported Casualties: {extracted['injured_count']} person(s)")
    if extracted.get("unconscious"):
        confirmed_facts.append("Clinical State: Unresponsive / Unconscious (Critical Trauma Priority)")
    if extracted.get("severe_bleeding"):
        confirmed_facts.append("Clinical Hazard: Active Severe Hemorrhage")
    if extracted.get("fire_involved"):
        confirmed_facts.append("Scene Hazard: Active Fire / Smoke Condition")
    if extracted.get("trapped"):
        confirmed_facts.append("Rescue Requirement: Trapped / Extrication Required")

    uncertain_info = []
    if not extracted.get("location"):
        uncertain_info.append("Precise landmark omitted in message text; relying on raw GPS coordinates (requires on-scene validation)")
    if extracted.get("injured_count", 0) == 0:
        uncertain_info.append("Exact number of affected individuals unstated; pending primary triage")
    uncertain_info.append("Report based on citizen witness communication; vital signs pending EMS assessment")

    return {
        "incident_code": incident_code,
        "original_report": original_text,
        "category": category,
        "priority": priority,
        "confirmed_facts": confirmed_facts,
        "uncertain_information": uncertain_info,
        "recommended_agencies": emergency_data.get("recommended_resources", ["AMBULANCE", "POLICE"]),
        "triage_summary": f"Incident {incident_code}: Category={category}, Priority={priority}. Required Agencies={', '.join(emergency_data.get('recommended_resources', ['AMBULANCE']))}."
    }
