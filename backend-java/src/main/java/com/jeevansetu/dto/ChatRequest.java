package com.jeevansetu.dto;

import java.util.Map;

public class ChatRequest {
    private String message;
    private String conversationId;
    private Double latitude;
    private Double longitude;
    private String userId;
    private String role;
    private Map<String, Object> context;

    public ChatRequest() {}

    public ChatRequest(String message, String conversationId, Double latitude, Double longitude) {
        this.message = message;
        this.conversationId = conversationId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
}
