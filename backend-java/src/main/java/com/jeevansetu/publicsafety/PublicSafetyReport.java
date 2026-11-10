package com.jeevansetu.publicsafety;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "public_safety_reports")
public class PublicSafetyReport {
    @Id
    private String id;
    private String reportCode;
    private String reporterUserId;
    private String reporterName;
    private String reporterPhone;
    private String category; // FLOODED_ROAD, FALLEN_TREE, ROAD_BLOCKAGE, DAMAGED_BRIDGE, ELECTRICAL_HAZARD, FIRE_HAZARD, DANGEROUS_CONDITIONS, OTHER
    private String description;
    private double latitude;
    private double longitude;
    private String address;
    private String imageAttachmentUrl;
    private String status = "PENDING_VERIFICATION"; // PENDING_VERIFICATION, VERIFIED, IN_PROGRESS, RESOLVED, REJECTED
    private boolean duplicateSuspected = false;
    private String duplicateOfReportId;
    private int upvoteCount = 0;
    private String adminRemarks;
    private String verifiedBy;
    private Instant reportedAt = Instant.now();
    private Instant resolvedAt;

    public PublicSafetyReport() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReportCode() { return reportCode; }
    public void setReportCode(String reportCode) { this.reportCode = reportCode; }

    public String getReporterUserId() { return reporterUserId; }
    public void setReporterUserId(String reporterUserId) { this.reporterUserId = reporterUserId; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getReporterPhone() { return reporterPhone; }
    public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getImageAttachmentUrl() { return imageAttachmentUrl; }
    public void setImageAttachmentUrl(String imageAttachmentUrl) { this.imageAttachmentUrl = imageAttachmentUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isDuplicateSuspected() { return duplicateSuspected; }
    public void setDuplicateSuspected(boolean duplicateSuspected) { this.duplicateSuspected = duplicateSuspected; }

    public String getDuplicateOfReportId() { return duplicateOfReportId; }
    public void setDuplicateOfReportId(String duplicateOfReportId) { this.duplicateOfReportId = duplicateOfReportId; }

    public int getUpvoteCount() { return upvoteCount; }
    public void setUpvoteCount(int upvoteCount) { this.upvoteCount = upvoteCount; }

    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }

    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }

    public Instant getReportedAt() { return reportedAt; }
    public void setReportedAt(Instant reportedAt) { this.reportedAt = reportedAt; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
}
