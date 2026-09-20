package com.jeevansetu.dto;

import com.jeevansetu.model.ResourceType;

public class AssignResourceRequest {
    private String resourceId;
    private ResourceType resourceType;

    public AssignResourceRequest() {}

    public AssignResourceRequest(String resourceId, ResourceType resourceType) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public ResourceType getResourceType() { return resourceType; }
    public void setResourceType(ResourceType resourceType) { this.resourceType = resourceType; }
}
