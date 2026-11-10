package com.jeevansetu.reunification;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "missing_person_reports")
public class MissingPersonReport {
    @Id
    private String id;
    private String familyGroupId;
    private String personName;
    private int age;
    private String gender;
    private String physicalDescription;
    private String photoUrl;
    private String lastKnownLocation;
    private Double lastKnownLatitude;
    private Double lastKnownLongitude;
    private String contactPersonName;
    private String contactPhone;
    private String status = "REPORTED"; // REPORTED, SIGHTED, LOCATED, REUNITED
    private boolean verifiedByOfficial = false;
    private String verifiedBy;
    private String officialRemarks;
    private boolean consentForPublicListing = true;
    private Instant reportedAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public MissingPersonReport() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFamilyGroupId() { return familyGroupId; }
    public void setFamilyGroupId(String familyGroupId) { this.familyGroupId = familyGroupId; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhysicalDescription() { return physicalDescription; }
    public void setPhysicalDescription(String physicalDescription) { this.physicalDescription = physicalDescription; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getLastKnownLocation() { return lastKnownLocation; }
    public void setLastKnownLocation(String lastKnownLocation) { this.lastKnownLocation = lastKnownLocation; }

    public Double getLastKnownLatitude() { return lastKnownLatitude; }
    public void setLastKnownLatitude(Double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; }

    public Double getLastKnownLongitude() { return lastKnownLongitude; }
    public void setLastKnownLongitude(Double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; }

    public String getContactPersonName() { return contactPersonName; }
    public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isVerifiedByOfficial() { return verifiedByOfficial; }
    public void setVerifiedByOfficial(boolean verifiedByOfficial) { this.verifiedByOfficial = verifiedByOfficial; }

    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }

    public String getOfficialRemarks() { return officialRemarks; }
    public void setOfficialRemarks(String officialRemarks) { this.officialRemarks = officialRemarks; }

    public boolean isConsentForPublicListing() { return consentForPublicListing; }
    public void setConsentForPublicListing(boolean consentForPublicListing) { this.consentForPublicListing = consentForPublicListing; }

    public Instant getReportedAt() { return reportedAt; }
    public void setReportedAt(Instant reportedAt) { this.reportedAt = reportedAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
