package com.jeevansetu.service;

import com.jeevansetu.model.IncidentChatMessage;
import com.jeevansetu.repository.IncidentChatMessageRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class IncidentChatService {

    private final IncidentChatMessageRepository chatRepository;
    private final WebSocketEventPublisher eventPublisher;

    public IncidentChatService(IncidentChatMessageRepository chatRepository, WebSocketEventPublisher eventPublisher) {
        this.chatRepository = chatRepository;
        this.eventPublisher = eventPublisher;
    }

    public IncidentChatMessage sendMessage(IncidentChatMessage msg) {
        msg.setTimestamp(Instant.now());
        if (msg.getTargetAgency() == null || msg.getTargetAgency().isBlank()) {
            msg.setTargetAgency("ALL");
        }
        IncidentChatMessage saved = chatRepository.save(msg);

        // Broadcast to WebSocket subscribers for this incident
        eventPublisher.publish("INCIDENT_CHAT_MESSAGE", Map.of(
                "incidentId", saved.getIncidentId(),
                "messageId", saved.getId(),
                "senderName", saved.getSenderName(),
                "senderRole", saved.getSenderRole(),
                "targetAgency", saved.getTargetAgency(),
                "messageText", saved.getMessageText(),
                "attachmentUrl", saved.getAttachmentUrl() != null ? saved.getAttachmentUrl() : "",
                "timestamp", saved.getTimestamp()
        ));

        return saved;
    }

    public List<IncidentChatMessage> getMessages(String incidentId, String userRole) {
        return chatRepository.findByIncidentId(incidentId, Sort.by(Sort.Direction.ASC, "timestamp"));
    }

    public void markMessagesAsRead(String incidentId, String readerName) {
        List<IncidentChatMessage> unread = chatRepository.findByIncidentId(incidentId, Sort.unsorted());
        boolean changed = false;
        Instant now = Instant.now();
        for (IncidentChatMessage m : unread) {
            if (!m.isRead()) {
                m.setRead(true);
                m.setReadBy(readerName);
                m.setReadAt(now);
                chatRepository.save(m);
                changed = true;
            }
        }
        if (changed) {
            eventPublisher.publish("INCIDENT_CHAT_READ", Map.of(
                    "incidentId", incidentId,
                    "readBy", readerName,
                    "timestamp", now
            ));
        }
    }
}
