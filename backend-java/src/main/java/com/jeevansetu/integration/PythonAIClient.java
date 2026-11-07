package com.jeevansetu.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;

@Component
public class PythonAIClient {

    private static final Logger logger = LoggerFactory.getLogger(PythonAIClient.class);
    private final RestClient restClient;
    private final String aiServiceUrl;

    public PythonAIClient(@Value("${jeevansetu.ai.service-url:http://localhost:8000}") String aiServiceUrl) {
        this.aiServiceUrl = aiServiceUrl;
        this.restClient = RestClient.builder().baseUrl(aiServiceUrl).build();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> processChat(String message, String userId, String role, String conversationId,
                                           Double lat, Double lng, String language) {
        try {
            Map<String, Object> reqBody = new HashMap<>();
            reqBody.put("message", message);
            reqBody.put("userId", userId);
            reqBody.put("role", role);
            reqBody.put("conversationId", conversationId);
            reqBody.put("language", language != null ? language : "en");
            if (lat != null && lng != null) {
                reqBody.put("currentLocation", Map.of("lat", lat, "lng", lng));
            }

            Map<String, Object> result = restClient.post()
                    .uri("/ai/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(reqBody)
                    .retrieve()
                    .body(Map.class);

            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            logger.warn("Python AI service call failed ({}). Activating Java Rule Engine Fallback.", e.getMessage());
        }

        return getJavaFallbackChatResponse(message, language);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getDispatcherSummary(String incidentCode, String description) {
        try {
            Map<String, Object> result = restClient.post()
                    .uri("/ai/dispatcher-summary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("incidentCode", incidentCode != null ? incidentCode : "INCIDENT",
                                 "description", description != null ? description : ""))
                    .retrieve()
                    .body(Map.class);

            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            logger.warn("Python AI summary call failed ({}). Using Java fallback summary.", e.getMessage());
        }

        return getJavaFallbackDispatcherSummary(incidentCode, description);
    }

    private Map<String, Object> getJavaFallbackDispatcherSummary(String incidentCode, String description) {
        Map<String, Object> summary = new HashMap<>();
        String lower = description != null ? description.toLowerCase() : "";

        String category = "MEDICAL";
        String priority = "HIGH";
        List<String> confirmed = new ArrayList<>();
        List<String> uncertain = new ArrayList<>();
        List<String> agencies = new ArrayList<>();

        if (lower.contains("accident") || lower.contains("crash") || lower.contains("hit")) {
            category = "ACCIDENT";
            agencies.addAll(List.of("AMBULANCE", "POLICE"));
        } else if (lower.contains("fire") || lower.contains("burn")) {
            category = "FIRE";
            priority = "CRITICAL";
            agencies.addAll(List.of("FIRE_UNIT", "AMBULANCE", "POLICE"));
        } else {
            agencies.addAll(List.of("AMBULANCE", "HOSPITAL"));
        }

        if (lower.contains("unconscious")) {
            confirmed.add("Patient Condition: Unresponsive / Unconscious (Critical Trauma)");
            priority = "CRITICAL";
        }
        if (lower.contains("bleed")) {
            confirmed.add("Clinical Hazard: Active External Hemorrhage");
        }
        confirmed.add("Caller Narrative Logged: " + (description != null && description.length() > 60 ? description.substring(0, 60) + "..." : description));

        uncertain.add("Exact address landmark unverified in narrative; requires on-scene GPS validation");
        uncertain.add("Witness reported severity; formal medical triage pending EMS arrival");

        summary.put("incident_code", incidentCode != null ? incidentCode : "INCIDENT");
        summary.put("original_report", description);
        summary.put("category", category);
        summary.put("priority", priority);
        summary.put("confirmed_facts", confirmed);
        summary.put("uncertain_information", uncertain);
        summary.put("recommended_agencies", agencies);
        summary.put("triage_summary", String.format("Incident %s triage assessment: Category=%s, Rating=%s.", incidentCode, category, priority));

        return summary;
    }

    private Map<String, Object> getJavaFallbackChatResponse(String message, String language) {
        Map<String, Object> fallback = new HashMap<>();
        String lower = message != null ? message.toLowerCase() : "";
        String lang = language != null ? language.toLowerCase() : "en";

        String intent = "GENERAL_ASSISTANCE";
        String category = "OTHER";
        String priority = "MEDIUM";
        List<String> recommendedResources = new ArrayList<>();
        Map<String, Object> extracted = new HashMap<>();
        List<String> missing = new ArrayList<>();
        List<String> actions = new ArrayList<>();

        if (lower.contains("accident") || lower.contains("crash") || lower.contains("दुर्घटना") || lower.contains("ప్రమాదం") || lower.contains("விபத்து")) {
            intent = "REPORT_EMERGENCY";
            category = "ROAD_ACCIDENT";
            priority = "HIGH";
            recommendedResources.add("AMBULANCE");
            recommendedResources.add("POLICE");
        } else if (lower.contains("fire") || lower.contains("smoke") || lower.contains("आग") || lower.contains("మంటలు") || lower.contains("தீ")) {
            intent = "REPORT_EMERGENCY";
            category = "FIRE";
            priority = "CRITICAL";
            recommendedResources.add("FIRE_TRUCK");
            recommendedResources.add("AMBULANCE");
        } else if (lower.contains("hospital") || lower.contains("अस्पताल") || lower.contains("ఆసుపత్రి") || lower.contains("மருத்துவமனை")) {
            intent = "FIND_HOSPITAL";
        } else if (lower.contains("ambulance") || lower.contains("एम्बुलेंस") || lower.contains("అంబులెన్స్") || lower.contains("ஆம்புலன்ஸ்")) {
            intent = "FIND_AMBULANCE";
        } else if (lower.contains("status") || lower.contains("track") || lower.contains("स्थिति") || lower.contains("స్థితి") || lower.contains("நிலை")) {
            intent = "CHECK_STATUS";
        } else if (lower.contains("guideline") || lower.contains("first aid") || lower.contains("उपचार") || lower.contains("చికిత్స") || lower.contains("முதலுதவி")) {
            intent = "EMERGENCY_GUIDELINES";
        } else if (lower.contains("hello") || lower.contains("hi") || lower.contains("नमस्ते") || lower.contains("నమస్కారం") || lower.contains("வணக்கம்")) {
            intent = "GREETING";
        } else {
            intent = "REPORT_EMERGENCY";
            category = "MEDICAL";
            recommendedResources.add("AMBULANCE");
        }

        String responseText;
        switch (intent) {
            case "REPORT_EMERGENCY" -> {
                if ("hi".equals(lang)) {
                    responseText = "🚨 आपातकाल पहचाना गया: " + category + " [" + priority + "]। विवरण नीचे दिए गए ड्राफ्ट में है, पुष्टि करने के लिए 'Confirm & Dispatch' पर टैप करें।";
                } else if ("te".equals(lang)) {
                    responseText = "🚨 అత్యవసర పరిస్థితి: " + category + " [" + priority + "]. వివరాలు నిర్ధారించి 'Confirm & Dispatch' క్లిక్ చేయండి.";
                } else if ("ta".equals(lang)) {
                    responseText = "🚨 அவசரநிலை: " + category + " [" + priority + "]. 'Confirm & Dispatch' அழுத்தவும்.";
                } else {
                    responseText = "🚨 Emergency detected: " + category + " [" + priority + " priority]. Review the draft details below and confirm to notify responders immediately.";
                }
                actions.addAll(List.of("Confirm Emergency Report", "Find Nearest Hospital", "First Aid Protocols"));
            }
            case "FIND_HOSPITAL" -> {
                responseText = "hi".equals(lang) ? "🏥 नजदीकी सत्यापित आपातकालीन अस्पताल खोजे जा रहे हैं..." :
                               "te".equals(lang) ? "🏥 సమీప ఆసుపత్రులు శోధించబడుతున్నాయి..." :
                               "🏥 Searching verified network hospitals near your coordinates...";
                actions.addAll(List.of("Find Available Ambulance", "Report an Emergency", "Emergency Guidelines"));
            }
            case "FIND_AMBULANCE" -> {
                responseText = "hi".equals(lang) ? "🚑 क्षेत्र में उपलब्ध एम्बुलेंस खोजी जा रही हैं..." :
                               "te".equals(lang) ? "🚑 అందుబాటులోని అంబులెన్స్ వివరాలు..." :
                               "🚑 Locating ready and en-route ambulances within the regional dispatch zone...";
                actions.addAll(List.of("Find Nearest Hospital", "Report an Emergency", "Check Status"));
            }
            case "CHECK_STATUS" -> {
                responseText = "📋 Provide your incident code (e.g. JS-2026-XXXX) to track real-time responder dispatch and unit status.";
                actions.addAll(List.of("Report an Emergency", "Find Nearest Hospital", "Emergency Guidelines"));
            }
            case "EMERGENCY_GUIDELINES" -> {
                responseText = "📖 Emergency Safety Guidelines & First Aid Protocols loaded for immediate reference.";
                actions.addAll(List.of("Report an Emergency", "Find Nearest Hospital", "Find Available Ambulance"));
            }
            default -> {
                responseText = "hi".equals(lang) ? "नमस्ते, मैं जीवनसेतु AI हूँ। मैं आपकी क्या मदद कर सकता हूँ?" :
                               "Hello, I am JeevanSetu AI — your intelligent emergency response coordinator. How can I protect or assist you today?";
                actions.addAll(List.of("Report an Emergency", "Find Nearest Hospital", "Find Available Ambulance", "Emergency Guidelines"));
            }
        }

        fallback.put("detected_intent", intent);
        fallback.put("intent_confidence", 0.90);
        fallback.put("emergency_category", category);
        fallback.put("suggested_priority", priority);
        fallback.put("confidence_score", 0.88);
        fallback.put("extracted_info", extracted);
        fallback.put("recommended_resources", recommendedResources);
        fallback.put("missing_info", missing);
        fallback.put("suggested_response", responseText);
        fallback.put("suggested_actions", actions);
        fallback.put("language", lang);

        return fallback;
    }
}
