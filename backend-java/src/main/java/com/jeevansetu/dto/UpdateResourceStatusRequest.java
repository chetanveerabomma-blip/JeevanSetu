package com.jeevansetu.dto;

import com.jeevansetu.model.ResourceStatus;

public class UpdateResourceStatusRequest {
    private ResourceStatus status;
    private double latitude;
    private double longitude;

    public UpdateResourceStatusRequest() {}

    public ResourceStatus getStatus() { return status; }
    public void setStatus(ResourceStatus status) { this.status = status; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
