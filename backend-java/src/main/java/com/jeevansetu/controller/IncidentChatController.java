package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.IncidentChatMessage;
import com.jeevansetu.service.IncidentChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incident-chat")
@CrossOrigin(origins = "*")
public class IncidentChatController {

    private final IncidentChatService chatService;

    public IncidentChatController(IncidentChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/{incidentId}/message")
    public ResponseEntity<ApiResponse<IncidentChatMessage>> sendMessage(
            @PathVariable String incidentId,
            @RequestBody IncidentChatMessage message) {
        message.setIncidentId(incidentId);
        IncidentChatMessage sent = chatService.sendMessage(message);
        return ResponseEntity.ok(ApiResponse.ok("Message sent", sent));
    }

    @GetMapping("/{incidentId}")
    public ResponseEntity<ApiResponse<List<IncidentChatMessage>>> getMessages(
            @PathVariable String incidentId,
            @RequestParam(required = false, defaultValue = "DISPATCHER") String role) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMessages(incidentId, role)));
    }

    @PutMapping("/{incidentId}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @PathVariable String incidentId,
            @RequestBody Map<String, String> body) {
        String reader = body.getOrDefault("readerName", "Operator");
        chatService.markMessagesAsRead(incidentId, reader);
        return ResponseEntity.ok(ApiResponse.message("Messages marked as read"));
    }
}
