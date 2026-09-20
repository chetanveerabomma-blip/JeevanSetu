package com.jeevansetu.service;

import com.jeevansetu.dto.AIClassificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.Map;

@Service
public class AiIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AiIntegrationService.class);
    private final RestClient restClient;
    private final String aiServiceUrl;

    public AiIntegrationService(@Value("${jeevansetu.ai.service-url:http://localhost:8000}") String aiServiceUrl) {
        this.aiServiceUrl = aiServiceUrl;
        this.restClient = RestClient.builder().baseUrl(aiServiceUrl).build();
    }

    public AIClassificationResponse classifyEmergency(String description) {
        try {
            return restClient.post()
                    .uri("/ai/classify-emergency")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("description", description != null ? description : ""))
                    .retrieve()
                    .body(AIClassificationResponse.class);
        } catch (Exception e) {
            logger.warn("Python AI service unavailable ({}), applying fallback classification", e.getMessage());
            AIClassificationResponse fallback = new AIClassificationResponse();
            fallback.setCategory("GENERAL");
            fallback.setSeverity("MEDIUM");
            fallback.setRecommendedResources(Collections.singletonList("AMBULANCE"));
            fallback.setSummary("Emergency incident reported requiring immediate validation.");
            fallback.setConfidenceScore(0.85);
            return fallback;
        }
    }

    public String summarizeIncident(String incidentCode, String description, int injured, boolean unconscious) {
        try {
            Map<?, ?> res = restClient.post()
                    .uri("/ai/summarize-incident")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "incidentCode", incidentCode,
                            "description", description,
                            "injuredCount", injured,
                            "unconscious", unconscious
                    ))
                    .retrieve()
                    .body(Map.class);
            if (res != null && res.containsKey("summary")) {
                return (String) res.get("summary");
            }
        } catch (Exception e) {
            logger.debug("AI summary fallback invoked: {}", e.getMessage());
        }
        return String.format("Incident %s: %d injured reported. %s", incidentCode, injured,
                unconscious ? "Critical trauma alert with loss of consciousness." : "Emergency assistance required.");
    }
}
