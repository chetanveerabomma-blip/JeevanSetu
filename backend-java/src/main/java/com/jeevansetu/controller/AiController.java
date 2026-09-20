package com.jeevansetu.controller;

import com.jeevansetu.dto.AIClassificationResponse;
import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.service.AiIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Assistive Engine", description = "Endpoints exposing AI NLP emergency analysis and triage summaries")
public class AiController {

    private final AiIntegrationService aiService;

    public AiController(AiIntegrationService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/classify")
    @Operation(summary = "Classify emergency text description via NLP service")
    public ResponseEntity<ApiResponse<AIClassificationResponse>> classifyEmergency(@RequestBody Map<String, String> body) {
        String description = body.get("description");
        AIClassificationResponse result = aiService.classifyEmergency(description);
        return ResponseEntity.ok(ApiResponse.ok("Classification completed", result));
    }

    @PostMapping("/summarize")
    @Operation(summary = "Generate concise executive synopsis of an emergency incident")
    public ResponseEntity<ApiResponse<String>> summarizeIncident(@RequestBody Map<String, Object> body) {
        String incidentCode = (String) body.getOrDefault("incidentCode", "INCIDENT");
        String description = (String) body.getOrDefault("description", "");
        int injured = ((Number) body.getOrDefault("injuredCount", 0)).intValue();
        boolean unconscious = Boolean.TRUE.equals(body.get("unconscious"));

        String summary = aiService.summarizeIncident(incidentCode, description, injured, unconscious);
        return ResponseEntity.ok(ApiResponse.ok("Summary generated", summary));
    }
}
