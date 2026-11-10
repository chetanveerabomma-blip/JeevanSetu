package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.Notification;
import com.jeevansetu.model.Role;
import com.jeevansetu.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Role role) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotificationsForUser(userId, role)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Role role) {
        long count = notificationService.getUnreadCount(userId, role);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("unreadCount", count)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Notification>> markRead(@PathVariable String id) {
        Notification updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Marked as read", updated));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Role role) {
        notificationService.markAllAsReadForUser(userId, role);
        return ResponseEntity.ok(ApiResponse.message("All notifications marked as read"));
    }
}
