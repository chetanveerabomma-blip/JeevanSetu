package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.dto.ChatRequest;
import com.jeevansetu.dto.EmergencyDraftRequest;
import com.jeevansetu.model.*;
import com.jeevansetu.repository.UserRepository;
import com.jeevansetu.service.AIAssistantService;
import com.jeevansetu.service.ChatHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/assistant")
@Tag(name = "AI Assistant", description = "Endpoints for JeevanSetu AI Emergency Intelligence Assistant")
public class AIAssistantController {

    private final AIAssistantService assistantService;
    private final ChatHistoryService chatHistoryService;
    private final UserRepository userRepository;

    public AIAssistantController(
            AIAssistantService assistantService,
            ChatHistoryService chatHistoryService,
            UserRepository userRepository) {
        this.assistantService = assistantService;
        this.chatHistoryService = chatHistoryService;
        this.userRepository = userRepository;
    }

    @PostMapping("/chat")
    @Operation(summary = "Send a natural language message to the AI Assistant")
    public ResponseEntity<ApiResponse<AssistantResponse>> chat(
            @RequestBody ChatRequest request,
            Authentication auth) {
        String email = auth != null ? auth.getName() : null;
        Optional<User> userOpt = email != null ? userRepository.findByEmail(email) : Optional.empty();

        String userId = userOpt.map(User::getId).orElse(request.getUserId());
        String userName = userOpt.map(User::getFullName).orElse("Citizen");
        String userRole = userOpt.map(u -> u.getRole().name()).orElse(request.getRole() != null ? request.getRole() : "CITIZEN");

        AssistantResponse response = assistantService.handleChat(request, userId, email, userName, userRole);
        return ResponseEntity.ok(ApiResponse.ok("Assistant response generated", response));
    }

    @PostMapping("/emergency-confirm")
    @Operation(summary = "Confirm draft emergency collected via AI Assistant chatbot")
    public ResponseEntity<ApiResponse<Emergency>> confirmEmergency(
            @RequestBody EmergencyDraftRequest draftRequest,
            Authentication auth) {
        String email = auth != null ? auth.getName() : "citizen@demo.jeevansetu.in";
        Optional<User> userOpt = userRepository.findByEmail(email);

        String userId = userOpt.map(User::getId).orElse(null);
        String userName = userOpt.map(User::getFullName).orElse(draftRequest.getReporterName() != null ? draftRequest.getReporterName() : "Citizen Reporter");
        String userPhone = userOpt.map(User::getPhone).orElse(draftRequest.getContactPhone() != null ? draftRequest.getContactPhone() : "N/A");

        Emergency confirmed = assistantService.confirmDraftEmergency(draftRequest, userId, email, userName, userPhone);
        return ResponseEntity.ok(ApiResponse.ok("Emergency confirmed and dispatched. Code: " + confirmed.getIncidentCode(), confirmed));
    }

    @GetMapping("/emergency-status/{incidentId}")
    @Operation(summary = "Retrieve real-time incident status by ID or incident code")
    public ResponseEntity<ApiResponse<Emergency>> getIncidentStatus(@PathVariable String incidentId) {
        Emergency status = assistantService.getEmergencyStatus(incidentId);
        return ResponseEntity.ok(ApiResponse.ok("Emergency status retrieved", status));
    }

    @GetMapping("/hospitals/nearby")
    @Operation(summary = "Find nearest hospitals to provided coordinates")
    public ResponseEntity<ApiResponse<List<Hospital>>> getNearbyHospitals(
            @RequestParam(defaultValue = "17.3850") double lat,
            @RequestParam(defaultValue = "78.4867") double lng,
            @RequestParam(defaultValue = "5") int limit) {
        List<Hospital> hospitals = assistantService.getNearbyHospitals(lat, lng, limit);
        return ResponseEntity.ok(ApiResponse.ok("Nearby hospitals retrieved", hospitals));
    }

    @GetMapping("/ambulances/available")
    @Operation(summary = "Locate nearby ambulances and their readiness status")
    public ResponseEntity<ApiResponse<List<ResponderUnit>>> getAvailableAmbulances(
            @RequestParam(defaultValue = "17.3850") double lat,
            @RequestParam(defaultValue = "78.4867") double lng,
            @RequestParam(defaultValue = "5") int limit) {
        List<ResponderUnit> ambulances = assistantService.getNearbyAmbulances(lat, lng, limit);
        return ResponseEntity.ok(ApiResponse.ok("Nearby ambulances retrieved", ambulances));
    }

    @GetMapping("/history")
    @Operation(summary = "Retrieve chat history by conversationId or current user")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> getChatHistory(
            @RequestParam(required = false) String conversationId,
            Authentication auth) {
        List<ChatMessage> history;
        if (conversationId != null && !conversationId.isBlank()) {
            history = chatHistoryService.getHistoryByConversation(conversationId);
        } else if (auth != null) {
            Optional<User> userOpt = userRepository.findByEmail(auth.getName());
            history = userOpt.map(user -> chatHistoryService.getHistoryByUser(user.getId())).orElse(List.of());
        } else {
            history = List.of();
        }
        return ResponseEntity.ok(ApiResponse.ok("Chat history retrieved", history));
    }

    @DeleteMapping("/history")
    @Operation(summary = "Clear conversation history")
    public ResponseEntity<ApiResponse<String>> clearChatHistory(
            @RequestParam(required = false) String conversationId,
            Authentication auth) {
        String userId = null;
        if (auth != null) {
            userId = userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
        }
        chatHistoryService.clearHistory(conversationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Chat history cleared", "SUCCESS"));
    }

    @GetMapping("/guidelines")
    @Operation(summary = "Get standard first aid and emergency guidelines")
    public ResponseEntity<ApiResponse<String>> getGuidelines(@RequestParam(defaultValue = "ALL") String category) {
        String guidelines = assistantService.getEmergencyGuidelinesContent(category);
        return ResponseEntity.ok(ApiResponse.ok("Guidelines retrieved", guidelines));
    }
}
