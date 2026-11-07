package com.jeevansetu.repository;

import com.jeevansetu.model.ChatMessage;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByConversationId(String conversationId, Sort sort);
    List<ChatMessage> findByUserId(String userId, Sort sort);
    void deleteByConversationId(String conversationId);
    void deleteByUserId(String userId);
}
