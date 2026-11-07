package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "chat_messages")
public class ChatMessage {
    @Id
    private String id;

    @Indexed
    private String conversationId;

    @Indexed
    private String userId;

    private String userRole;
    private String sender; // "USER" or "ASSISTANT"
    private String message;
    private String intent;
    private String category;
    private String priority;
    private Map<String, Object> metadata = new HashMap<>();

    @Indexed
    private Instant timestamp = Instant.now();

    public ChatMessage() {}

    public ChatMessage(String conversationId, String userId, String userRole, String sender, String message, String intent) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.userRole = userRole;
        this.sender = sender;
        this.message = message;
        this.intent = intent;
        this.timestamp = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
