package com.jeevansetu.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventPublisher.class);
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishEmergencyCreated(Object emergency) {
        publishEvent("/topic/emergencies", "EMERGENCY_CREATED", emergency);
    }

    public void publishEmergencyUpdated(Object emergency) {
        publishEvent("/topic/emergencies", "EMERGENCY_UPDATED", emergency);
    }

    public void publishDispatchAssigned(Object assignment) {
        publishEvent("/topic/dispatches", "DISPATCH_ASSIGNED", assignment);
    }

    public void publishDispatchStatusChanged(Object assignment) {
        publishEvent("/topic/dispatches", "DISPATCH_STATUS_CHANGED", assignment);
    }

    public void publishHospitalCapacityUpdated(Object hospital) {
        publishEvent("/topic/hospitals", "HOSPITAL_CAPACITY_UPDATED", hospital);
    }

    public void publishNotification(Object notification) {
        publishEvent("/topic/notifications", "NOTIFICATION", notification);
    }

    public void publishAgencyMessage(String incidentId, Object message) {
        publishEvent("/topic/agency-comms", "AGENCY_MESSAGE", message);
        if (incidentId != null && !incidentId.isBlank()) {
            publishEvent("/topic/incidents/" + incidentId + "/comms", "AGENCY_MESSAGE", message);
        }
    }

    public void publishSosAlert(Object sosAlert) {
        publishEvent("/topic/sos-alerts", "SOS_ALERT", sosAlert);
        publishEvent("/topic/emergencies", "EMERGENCY_CREATED", sosAlert);
    }

    private void publishEvent(String destination, String eventType, Object payload) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", eventType);
            message.put("timestamp", System.currentTimeMillis());
            message.put("payload", payload);
            messagingTemplate.convertAndSend(destination, message);
            logger.info("Published WebSocket event [{}] to destination [{}]", eventType, destination);
        } catch (Exception e) {
            logger.warn("Could not broadcast WebSocket event [{}]: {}", eventType, e.getMessage());
        }
    }
}
