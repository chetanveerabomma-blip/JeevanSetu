package com.jeevansetu.service;

import com.jeevansetu.algorithm.PriorityEngine;
import com.jeevansetu.algorithm.ResourceSuitabilityScorer;
import com.jeevansetu.dto.ChatRequest;
import com.jeevansetu.dto.EmergencyDraftRequest;
import com.jeevansetu.dto.EmergencyReportRequest;
import com.jeevansetu.integration.PythonAIClient;
import com.jeevansetu.model.*;
import com.jeevansetu.repository.ResponderUnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIAssistantService {

    private static final Logger logger = LoggerFactory.getLogger(AIAssistantService.class);
    private static final Pattern INCIDENT_CODE_PATTERN = Pattern.compile("JS-\\d{4}-\\d{4}", Pattern.CASE_INSENSITIVE);

    private final PythonAIClient pythonAIClient;
    private final ChatHistoryService chatHistoryService;
    private final EmergencyService emergencyService;
    private final HospitalService hospitalService;
    private final ResponderUnitRepository responderUnitRepository;
    private final PriorityEngine priorityEngine;

    public AIAssistantService(
            PythonAIClient pythonAIClient,
            ChatHistoryService chatHistoryService,
            EmergencyService emergencyService,
            HospitalService hospitalService,
            ResponderUnitRepository responderUnitRepository,
            PriorityEngine priorityEngine) {
        this.pythonAIClient = pythonAIClient;
        this.chatHistoryService = chatHistoryService;
        this.emergencyService = emergencyService;
        this.hospitalService = hospitalService;
        this.responderUnitRepository = responderUnitRepository;
        this.priorityEngine = priorityEngine;
    }

    @SuppressWarnings("unchecked")
    public AssistantResponse handleChat(ChatRequest request, String userId, String userEmail, String userName, String role) {
        String message = request.getMessage() != null ? request.getMessage().trim() : "";
        String conversationId = request.getConversationId() != null && !request.getConversationId().isBlank()
                ? request.getConversationId()
                : UUID.randomUUID().toString();

        Double lat = request.getLatitude() != null ? request.getLatitude() : 17.3850;
        Double lng = request.getLongitude() != null ? request.getLongitude() : 78.4867;

        // 1. Record User Message
        chatHistoryService.saveMessage(
                conversationId,
                userId,
                role != null ? role : "CITIZEN",
                "USER",
                message,
                null,
                null,
                null,
                Map.of("lat", lat, "lng", lng)
        );

        // 2. Invoke Python AI Service for NLP & Intent Classification
        Map<String, Object> aiResult = pythonAIClient.processChat(message, userId, role, conversationId, lat, lng);

        String detectedIntent = (String) aiResult.getOrDefault("detected_intent", "GENERAL_ASSISTANCE");
        String categoryStr = (String) aiResult.getOrDefault("emergency_category", "OTHER");
        String suggestedPriorityStr = (String) aiResult.getOrDefault("suggested_priority", "MEDIUM");
        double confidence = aiResult.containsKey("confidence_score")
                ? ((Number) aiResult.get("confidence_score")).doubleValue() : 0.85;

        Map<String, Object> extracted = aiResult.containsKey("extracted_info")
                ? (Map<String, Object>) aiResult.get("extracted_info") : new HashMap<>();
        List<String> recommendedResources = aiResult.containsKey("recommended_resources")
                ? (List<String>) aiResult.get("recommended_resources") : new ArrayList<>();
        List<String> missingInfo = aiResult.containsKey("missing_info")
                ? (List<String>) aiResult.get("missing_info") : new ArrayList<>();
        List<String> suggestedActions = aiResult.containsKey("suggested_actions")
                ? (List<String>) aiResult.get("suggested_actions") : new ArrayList<>();
        String responseText = (String) aiResult.getOrDefault("suggested_response", "How can I assist you with emergency response?");

        AssistantResponse response = new AssistantResponse(responseText, detectedIntent);
        response.setCategory(categoryStr);
        response.setConfidenceScore(confidence);
        response.setExtractedInfo(extracted);
        response.setRecommendedResources(recommendedResources);
        response.setMissingInfo(missingInfo);
        response.setSuggestedActions(suggestedActions);

        // 3. Contextual Data Processing & Enrichment
        EmergencyType emergencyType = parseEmergencyType(categoryStr);

        switch (detectedIntent) {
            case "REPORT_EMERGENCY" -> {
                // Construct authoritative emergency draft
                Emergency draft = new Emergency();
                draft.setType(emergencyType);
                draft.setDescription(message);
                draft.setLatitude(lat);
                draft.setLongitude(lng);
                draft.setAddress((String) extracted.getOrDefault("location", "GPS Coordinates captured: " + lat + ", " + lng));
                draft.setInjuredCount(extracted.containsKey("casualties") ? ((Number) extracted.get("casualties")).intValue() : 0);
                draft.setUnconscious(Boolean.TRUE.equals(extracted.get("unconscious")));
                draft.setSevereBleeding(Boolean.TRUE.equals(extracted.get("severeBleeding")));
                draft.setFireInvolved(Boolean.TRUE.equals(extracted.get("fire")));
                draft.setTrapped(Boolean.TRUE.equals(extracted.get("trapped")));

                // Authoritative Java PriorityEngine computation
                EmergencyPriority computedPriority = priorityEngine.determinePriority(draft);
                draft.setPriority(computedPriority);
                response.setPriority(computedPriority.name());
                response.setEmergencyDraft(draft);

                // Fetch top 3 nearby hospitals & ambulances for immediate awareness
                try {
                    response.setHospitals(hospitalService.getNearbyHospitals(lat, lng, 3));
                    response.setAmbulances(getNearbyAmbulances(lat, lng, 3));
                } catch (Exception e) {
                    logger.debug("Resource preview error: {}", e.getMessage());
                }
            }

            case "FIND_HOSPITAL" -> {
                List<Hospital> hospitals = hospitalService.getNearbyHospitals(lat, lng, 5);
                response.setHospitals(hospitals);
                if (!hospitals.isEmpty()) {
                    response.setText(String.format("Found %d verified emergency hospitals near your location. Tap any facility for directions or call directly.", hospitals.size()));
                }
            }

            case "FIND_AMBULANCE" -> {
                List<ResponderUnit> ambulances = getNearbyAmbulances(lat, lng, 5);
                response.setAmbulances(ambulances);
                long availableCount = ambulances.stream().filter(a -> a.getStatus() == ResourceStatus.AVAILABLE).count();
                response.setText(String.format("Identified %d regional ambulances (%d currently Available, %d En Route). Dispatch authority is active.",
                        ambulances.size(), availableCount, ambulances.size() - availableCount));
            }

            case "CHECK_STATUS" -> {
                Matcher matcher = INCIDENT_CODE_PATTERN.matcher(message);
                if (matcher.find()) {
                    String incidentCode = matcher.group().toUpperCase();
                    try {
                        Emergency emergency = emergencyService.getEmergencyByCode(incidentCode);
                        response.setIncidentStatus(emergency);
                        response.setText(String.format("Status for %s: %s | Priority: %s | Type: %s | Reported: %s",
                                emergency.getIncidentCode(), emergency.getStatus(), emergency.getPriority(),
                                emergency.getType(), emergency.getCreatedAt()));
                    } catch (Exception e) {
                        response.setText("Could not find any incident matching code " + incidentCode + ". Please verify the incident ID.");
                    }
                } else if (userId != null && !userId.isBlank()) {
                    List<Emergency> userEmergencies = emergencyService.getEmergenciesForCitizen(userId);
                    if (!userEmergencies.isEmpty()) {
                        Emergency latest = userEmergencies.get(0);
                        response.setIncidentStatus(latest);
                        response.setText(String.format("Your most recent incident %s is currently '%s' with priority %s.",
                                latest.getIncidentCode(), latest.getStatus(), latest.getPriority()));
                    } else {
                        response.setText("You have no active emergency incidents registered. If you need help, please tell me what is happening or tap 'Report an Emergency'.");
                    }
                }
            }

            case "EMERGENCY_GUIDELINES" -> {
                response.setGuidelineContent(getEmergencyGuidelinesContent(categoryStr));
            }

            default -> {
                response.setPriority(suggestedPriorityStr);
            }
        }

        // 4. Save Assistant Response in History
        chatHistoryService.saveMessage(
                conversationId,
                userId,
                "SYSTEM",
                "ASSISTANT",
                response.getText(),
                detectedIntent,
                response.getCategory(),
                response.getPriority(),
                Map.of("confidence", response.getConfidenceScore())
        );

        return response;
    }

    public Emergency confirmDraftEmergency(EmergencyDraftRequest draftReq, String userId, String userEmail, String userName, String userPhone) {
        EmergencyReportRequest req = new EmergencyReportRequest();
        req.setType(draftReq.getType() != null ? draftReq.getType() : EmergencyType.MEDICAL);
        req.setDescription(draftReq.getDescription());
        req.setLatitude(draftReq.getLatitude() != null ? draftReq.getLatitude() : 17.3850);
        req.setLongitude(draftReq.getLongitude() != null ? draftReq.getLongitude() : 78.4867);
        req.setAddress(draftReq.getAddress() != null ? draftReq.getAddress() : "Reported via JeevanSetu AI Assistant");
        req.setPeopleAffected(draftReq.getPeopleAffected());
        req.setInjuredCount(draftReq.getInjuredCount());
        req.setUnconscious(draftReq.isUnconscious());
        req.setSevereBleeding(draftReq.isSevereBleeding());
        req.setFireInvolved(draftReq.isFireInvolved());
        req.setTrapped(draftReq.isTrapped());
        req.setAdditionalInfo(draftReq.getAdditionalInfo());

        String effectivePhone = (draftReq.getContactPhone() != null && !draftReq.getContactPhone().isBlank())
                ? draftReq.getContactPhone() : userPhone;
        String effectiveName = (draftReq.getReporterName() != null && !draftReq.getReporterName().isBlank())
                ? draftReq.getReporterName() : (userName != null ? userName : "Citizen Reporter");

        // Authoritative registration via EmergencyService
        Emergency created = emergencyService.reportEmergency(req, userId, userEmail, effectiveName, effectivePhone);

        // Record in chat history
        if (draftReq.getConversationId() != null) {
            chatHistoryService.saveMessage(
                    draftReq.getConversationId(),
                    userId,
                    "SYSTEM",
                    "ASSISTANT",
                    "✅ Emergency Registered! Incident Code: " + created.getIncidentCode() +
                            " (Priority: " + created.getPriority() + "). Command center alerted and dispatch triage initiated.",
                    "INCIDENT_CONFIRMED",
                    created.getType().name(),
                    created.getPriority().name(),
                    Map.of("incidentId", created.getId(), "incidentCode", created.getIncidentCode())
            );
        }

        return created;
    }

    public List<Hospital> getNearbyHospitals(Double lat, Double lng, int limit) {
        double dLat = lat != null ? lat : 17.3850;
        double dLng = lng != null ? lng : 78.4867;
        return hospitalService.getNearbyHospitals(dLat, dLng, limit > 0 ? limit : 5);
    }

    public List<ResponderUnit> getNearbyAmbulances(Double lat, Double lng, int limit) {
        double dLat = lat != null ? lat : 17.3850;
        double dLng = lng != null ? lng : 78.4867;
        List<ResponderUnit> ambulances = responderUnitRepository.findByType(ResourceType.AMBULANCE);

        // Sort by distance to user
        ambulances.sort(Comparator.comparingDouble(u ->
                ResourceSuitabilityScorer.calculateHaversineDistance(dLat, dLng, u.getLatitude(), u.getLongitude())
        ));

        int max = limit > 0 ? limit : 5;
        return ambulances.stream().limit(max).toList();
    }

    public Emergency getEmergencyStatus(String incidentIdOrCode) {
        if (incidentIdOrCode.toUpperCase().startsWith("JS-")) {
            return emergencyService.getEmergencyByCode(incidentIdOrCode);
        }
        return emergencyService.getEmergencyById(incidentIdOrCode);
    }

    public String getEmergencyGuidelinesContent(String topic) {
        return """
                ### 🚨 JeevanSetu Immediate Emergency Guidelines
                
                **1. Cardio-Pulmonary Resuscitation (CPR)**
                - Check consciousness and breathing.
                - Place hands on the center of the chest.
                - Push hard and fast (100–120 compressions/min, 2 inches deep).
                - Continue until paramedics arrive.
                
                **2. Severe Bleeding Control**
                - Apply direct, firm pressure on the wound using a clean cloth or gauze.
                - Keep pressure constant; do not remove soaked bandages, add more on top.
                - Elevate the injured limb above heart level if possible.
                
                **3. Burns Management**
                - Cool burn under clean, cool running water for at least 10–15 minutes.
                - Do NOT apply ice, oil, butter, or paste.
                - Cover loosely with sterile, non-adherent dressing.
                
                **4. Road Accident First Steps**
                - Turn on hazard lights; ensure your own safety before approaching.
                - Do NOT move an injured person unless immediate risk of fire or explosion.
                - Keep patient calm and warm; do NOT remove helmets for motorcycle crash victims unless airway is blocked.
                
                **Emergency Helpline:** 112 (National Unified) | 108 (Ambulance) | 101 (Fire) | 100 (Police)
                """;
    }

    private EmergencyType parseEmergencyType(String categoryStr) {
        if (categoryStr == null) return EmergencyType.OTHER;
        String upper = categoryStr.toUpperCase();
        if (upper.contains("ACCIDENT")) return EmergencyType.ACCIDENT;
        if (upper.contains("FIRE")) return EmergencyType.FIRE;
        if (upper.contains("MEDICAL") || upper.contains("HEALTH")) return EmergencyType.MEDICAL;
        if (upper.contains("POLICE") || upper.contains("CRIME") || upper.contains("SECURITY")) return EmergencyType.POLICE;
        if (upper.contains("RESCUE")) return EmergencyType.RESCUE;
        if (upper.contains("FLOOD")) return EmergencyType.FLOOD;
        if (upper.contains("COLLAPSE")) return EmergencyType.BUILDING_COLLAPSE;
        if (upper.contains("BLOOD")) return EmergencyType.BLOOD_EMERGENCY;
        if (upper.contains("DISASTER")) return EmergencyType.NATURAL_DISASTER;
        return EmergencyType.OTHER;
    }
}
