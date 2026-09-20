package com.jeevansetu.repository;

import com.jeevansetu.model.Notification;
import com.jeevansetu.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(String userId);
    List<Notification> findByRecipientRoleOrderByCreatedAtDesc(Role role);
    long countByRecipientUserIdAndReadFalse(String userId);
    long countByRecipientRoleAndReadFalse(Role role);
}
