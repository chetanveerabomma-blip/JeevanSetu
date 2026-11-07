from typing import Dict, Any, List

EMERGENCY_GUIDELINES = {
    "ROAD_ACCIDENT": (
        "🚨 **Road Accident Emergency Protocol:**\n"
        "1. **Safety First**: Park your vehicle safely, turn on hazard lights, and avoid standing on active lanes.\n"
        "2. **Check Unresponsiveness**: Do NOT move injured victims unless there is immediate danger of fire or explosion.\n"
        "3. **Control Bleeding**: Apply firm, clean direct pressure with sterile cloth.\n"
        "4. **Keep Airway Clear**: Check breathing; do not remove helmets of motorcycle riders unless breathing is blocked.\n"
        "5. **Call Immediate Emergency Services**: Dial 112 (National Emergency) or 108 (Ambulance)."
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
    ),
    "NATURAL_DISASTER": (
        "🌪️ **Disaster & Flood Safety Protocol:**\n"
        "1. **Floods**: Move immediately to higher ground; never walk or drive through flowing water.\n"
        "2. **Building Collapse**: If trapped, cover face, tap on pipes or walls rather than shouting continuously to conserve oxygen.\n"
        "3. **Power Down**: Disconnect electrical mains and gas supplies before evacuating."
    )
}

def generate_response(intent: str, emergency_data: Dict[str, Any], user_message: str) -> Dict[str, Any]:
    extracted = emergency_data.get("extracted_info", {})
    category = emergency_data.get("category", "MEDICAL")
    priority = emergency_data.get("priority", "MEDIUM")
    resources = emergency_data.get("recommended_resources", ["AMBULANCE"])
    missing = emergency_data.get("missing_info", [])

    message = ""
    suggested_actions: List[str] = []

    if intent == "GREETING":
        message = (
            "Hello! I am **JeevanSetu AI – Emergency Intelligence Assistant**.\n\n"
            "I can assist you with:\n"
            "• **Immediate Emergency Reporting** (Accident, Medical, Fire, Rescue)\n"
            "• **Finding Available Ambulances** with shortest ETA\n"
            "• **Locating Nearest Hospitals & ICU Beds**\n"
            "• **Tracking Active Incident Status**\n"
            "• **Verified First-Aid & Emergency Guidelines**\n\n"
            "How can I assist you right now? If this is a life threat, please state the incident details and location."
        )
        suggested_actions = ["Report an Emergency", "Find Nearest Hospital", "Find Available Ambulance", "Emergency Guidelines"]

    elif intent == "FIND_HOSPITAL":
        message = (
            "🏥 **Hospital Recommendation Engine Active**\n\n"
            "Retrieving verified trauma hospitals and emergency ICU bed counts from our real-time database..."
        )
        suggested_actions = ["Show Nearest Trauma Hospital", "Check ICU Beds", "Report Emergency Instead"]

    elif intent == "FIND_AMBULANCE":
        message = (
            "🚑 **Ambulance Dispatch Locator Active**\n\n"
            "Querying active ALS & BLS ambulance fleet status, vehicle GPS coordinates, and estimated arrival times..."
        )
        suggested_actions = ["Dispatch Nearest Ambulance", "Check Ambulance Availability", "Report Emergency"]

    elif intent == "CHECK_STATUS":
        code = extracted.get("incident_code")
        if code:
            message = f"🔍 **Searching Incident Telemetry for {code}**...\nRetrieving real-time status, assigned units, and hospital coordination details from MongoDB."
        else:
            message = (
                "🔍 **Check Incident Status**\n\n"
                "Please enter your **Incident Code** (e.g. `JS-2026-000184`), or click below to view your active incident."
            )
            suggested_actions = ["JS-2026-000184", "View My Active Emergencies"]

    elif intent == "EMERGENCY_GUIDELINES":
        lower = user_message.lower()
        if "fire" in lower or "burn" in lower:
            message = EMERGENCY_GUIDELINES["FIRE"]
        elif "accident" in lower or "crash" in lower or "road" in lower:
            message = EMERGENCY_GUIDELINES["ROAD_ACCIDENT"]
        elif "flood" in lower or "disaster" in lower or "collapse" in lower:
            message = EMERGENCY_GUIDELINES["NATURAL_DISASTER"]
        else:
            message = EMERGENCY_GUIDELINES["MEDICAL"]
        suggested_actions = ["Road Accident Guide", "Fire Safety Guide", "Medical Emergency Guide", "Report Emergency"]

    elif intent == "REPORT_EMERGENCY":
        loc = extracted.get("location")
        inj = extracted.get("injured_count", 0)
        unconscious = extracted.get("unconscious", False)
        bleeding = extracted.get("severe_bleeding", False)
        trapped = extracted.get("trapped", False)

        prompt_missing = ""
        if "location" in missing:
            prompt_missing += "\n⚠️ *Please specify the exact street, landmark, or area location.*"
        
        triage_note = ""
        if unconscious or bleeding or trapped:
            triage_note = f"\n🚨 **Trauma Alert Detected**: Immediate {priority} priority triage activated."

        message = (
            f"🆘 **Emergency Incident Identified: {category}**\n"
            f"• **Priority Level**: **{priority}**\n"
            f"• **Assigned Agencies**: {', '.join(resources)}\n"
            f"• **Location**: {loc if loc else 'Pending exact landmark'}\n"
            f"• **Casualties**: {inj if inj > 0 else 'Reported'}"
            f"{triage_note}{prompt_missing}\n\n"
            "Would you like me to prepare a formal emergency registration draft for immediate command dispatch?"
        )
        suggested_actions = ["Confirm & Register Incident", "Add More Details", "Call 112 Immediately"]

    else:
        message = (
            "I have logged your query into JeevanSetu Intelligence Grid.\n"
            "If an emergency is occurring, please provide the location and nature of the incident (Medical, Accident, Fire, or Rescue)."
        )
        suggested_actions = ["Report an Emergency", "Find Nearest Hospital", "Emergency Guidelines"]

    return {
        "text": message,
        "suggested_actions": suggested_actions
    }
