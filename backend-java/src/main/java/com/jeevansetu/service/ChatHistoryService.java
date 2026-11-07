package com.jeevansetu.service;

import com.jeevansetu.model.ChatMessage;
import com.jeevansetu.repository.ChatHistoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;

    public ChatHistoryService(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    public ChatMessage saveMessage(String conversationId, String userId, String userRole,
                                   String sender, String message, String intent,
                                   String category, String priority, Map<String, Object> metadata) {
        ChatMessage msg = new ChatMessage(conversationId, userId, userRole, sender, message, intent);
        msg.setCategory(category);
        msg.setPriority(priority);
        if (metadata != null) {
            msg.setMetadata(metadata);
        }
        msg.setTimestamp(Instant.now());
        return chatHistoryRepository.save(msg);
    }

    public List<ChatMessage> getHistoryByConversation(String conversationId) {
        return chatHistoryRepository.findByConversationId(conversationId, Sort.by(Sort.Direction.ASC, "timestamp"));
    }

    public List<ChatMessage> getHistoryByUser(String userId) {
        return chatHistoryRepository.findByUserId(userId, Sort.by(Sort.Direction.ASC, "timestamp"));
    }

    public void clearHistory(String conversationId, String userId) {
        if (conversationId != null && !conversationId.isBlank()) {
            chatHistoryRepository.deleteByConversationId(conversationId);
        } else if (userId != null && !userId.isBlank()) {
            chatHistoryRepository.deleteByUserId(userId);
        }
    }
}
