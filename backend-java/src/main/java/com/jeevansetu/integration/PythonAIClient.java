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
    public Map<String, Object> processChat(String message, String userId, String role, String conversationId, Double lat, Double lng) {
        try {
            Map<String, Object> reqBody = new HashMap<>();
            reqBody.put("message", message);
            reqBody.put("userId", userId);
            reqBody.put("role", role);
            reqBody.put("conversationId", conversationId);
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

        return getJavaFallbackChatResponse(message);
    }

    /**
     * Fallback NLP and rule engine implemented in pure Java when Python microservice is unreachable.
     */
    private Map<String, Object> getJavaFallbackChatResponse(String message) {
        Map<String, Object> fallback = new HashMap<>();
        String lower = message != null ? message.toLowerCase() : "";

        String intent = "GENERAL_ASSISTANCE";
        String category = "OTHER";
        String priority = "MEDIUM";
        List<String> recommendedResources = new ArrayList<>();
        Map<String, Object> extracted = new HashMap<>();
        List<String> missing = new ArrayList<>();
        List<String> actions = new ArrayList<>();

        if (lower.contains("accident") || lower.contains("crash") || lower.contains("collision") || lower.contains("hit")) {
            intent = "REPORT_EMERGENCY";
            category = "ROAD_ACCIDENT";
            priority = "HIGH";
            recommendedResources.add("AMBULANCE");
            recommendedResources.add("POLICE");
        } else if (lower.contains("fire") || lower.contains("smoke") || lower.contains("burning") || lower.contains("flame")) {
            intent = "REPORT_EMERGENCY";
            category = "FIRE";
            priority = "CRITICAL";
            recommendedResources.add("FIRE_TRUCK");
            recommendedResources.add("AMBULANCE");
        } else if (lower.contains("heart") || lower.contains("chest") || lower.contains("stroke") || lower.contains("unconscious") || lower.contains("breath")) {
            intent = "REPORT_EMERGENCY";
            category = "MEDICAL";
            priority = "CRITICAL";
            recommendedResources.add("AMBULANCE");
        } else if (lower.contains("hospital") || lower.contains("doctor") || lower.contains("clinic")) {
            intent = "FIND_HOSPITAL";
        } else if (lower.contains("ambulance") || lower.contains("paramedic")) {
            intent = "FIND_AMBULANCE";
        } else if (lower.contains("status") || lower.contains("track") || lower.contains("incident")) {
            intent = "CHECK_STATUS";
        } else if (lower.contains("guideline") || lower.contains("first aid") || lower.contains("help") || lower.contains("cpr")) {
            intent = "EMERGENCY_GUIDELINES";
        } else if (lower.contains("hello") || lower.contains("hi") || lower.contains("namaste")) {
            intent = "GREETING";
        }

        // Casualty checks
        if (lower.contains("unconscious") || lower.contains("unresponsive")) {
            extracted.put("unconscious", true);
            priority = "CRITICAL";
        }
        if (lower.contains("bleeding") || lower.contains("blood loss")) {
            extracted.put("severeBleeding", true);
            if (!priority.equals("CRITICAL")) priority = "HIGH";
        }

        String responseText;
        switch (intent) {
            case "REPORT_EMERGENCY" -> {
                responseText = "🚨 Emergency detected: " + category + " [" + priority + " priority]. Review the draft details below and confirm to notify responders immediately.";
                actions.addAll(List.of("Confirm Emergency Report", "Find Nearest Hospital", "First Aid Protocols"));
            }
            case "FIND_HOSPITAL" -> {
                responseText = "🏥 Searching verified network hospitals near your coordinates...";
                actions.addAll(List.of("Find Available Ambulance", "Report an Emergency", "Emergency Guidelines"));
            }
            case "FIND_AMBULANCE" -> {
                responseText = "🚑 Locating ready and en-route ambulances within the regional dispatch zone...";
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
            case "GREETING" -> {
                responseText = "Hello, I am JeevanSetu AI — your intelligent emergency response coordinator. How can I protect or assist you today?";
                actions.addAll(List.of("Report an Emergency", "Find Nearest Hospital", "Find Available Ambulance", "Emergency Guidelines"));
            }
            default -> {
                responseText = "I am listening. If this is an emergency, describe what happened or select a quick action below.";
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

        return fallback;
    }
}
