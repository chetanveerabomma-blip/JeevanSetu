package com.jeevansetu.service;

import com.jeevansetu.model.Notification;
import com.jeevansetu.model.Role;
import com.jeevansetu.repository.NotificationRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebSocketEventPublisher eventPublisher;

    public NotificationService(NotificationRepository notificationRepository, WebSocketEventPublisher eventPublisher) {
        this.notificationRepository = notificationRepository;
        this.eventPublisher = eventPublisher;
    }

    public Notification createAndSendNotification(String recipientUserId, Role recipientRole, String title, String message, String incidentId, String incidentCode, String type) {
        Notification notification = new Notification(recipientUserId, recipientRole, title, message, incidentId, incidentCode, type);
        Notification saved = notificationRepository.save(notification);

        // Real-time broadcast
        eventPublisher.publish("USER_NOTIFICATION", Map.of(
                "notificationId", saved.getId(),
                "title", saved.getTitle(),
                "message", saved.getMessage(),
                "type", saved.getType(),
                "recipientRole", recipientRole != null ? recipientRole.name() : "ALL",
                "createdAt", saved.getCreatedAt()
        ));

        return saved;
    }

    public List<Notification> getNotificationsForUser(String userId, Role role) {
        if (userId != null && !userId.isBlank()) {
            return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId);
        }
        if (role != null) {
            return notificationRepository.findByRecipientRoleOrderByCreatedAtDesc(role);
        }
        return notificationRepository.findAll();
    }

    public long getUnreadCount(String userId, Role role) {
        if (userId != null && !userId.isBlank()) {
            return notificationRepository.countByRecipientUserIdAndReadFalse(userId);
        }
        if (role != null) {
            return notificationRepository.countByRecipientRoleAndReadFalse(role);
        }
        return 0;
    }

    public Notification markAsRead(String notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        n.setRead(true);
        return notificationRepository.save(n);
    }

    public void markAllAsReadForUser(String userId, Role role) {
        List<Notification> list = getNotificationsForUser(userId, role);
        for (Notification n : list) {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        }
    }
}
