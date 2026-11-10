package com.jeevansetu.sla;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sla_configs")
public class SlaConfig {
    @Id
    private String id;
    private String priority; // CRITICAL, HIGH, MEDIUM, LOW
    private int targetAckSeconds; // Target time for dispatcher acknowledgement
    private int targetAssignSeconds; // Target time to assign agency/responder
    private int targetResolveSeconds; // Target time to resolve incident
    private boolean active = true;

    public SlaConfig() {}

    public SlaConfig(String priority, int targetAckSeconds, int targetAssignSeconds, int targetResolveSeconds) {
        this.priority = priority;
        this.targetAckSeconds = targetAckSeconds;
        this.targetAssignSeconds = targetAssignSeconds;
        this.targetResolveSeconds = targetResolveSeconds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public int getTargetAckSeconds() { return targetAckSeconds; }
    public void setTargetAckSeconds(int targetAckSeconds) { this.targetAckSeconds = targetAckSeconds; }

    public int getTargetAssignSeconds() { return targetAssignSeconds; }
    public void setTargetAssignSeconds(int targetAssignSeconds) { this.targetAssignSeconds = targetAssignSeconds; }

    public int getTargetResolveSeconds() { return targetResolveSeconds; }
    public void setTargetResolveSeconds(int targetResolveSeconds) { this.targetResolveSeconds = targetResolveSeconds; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
