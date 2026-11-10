package com.jeevansetu.qrcode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "emergency_qr_profiles")
public class EmergencyQrProfile {
    @Id
    private String id;
    private String userId;
    private String fullName;
    private String qrAccessKey; // Secure UUID access token for URL
    private String bloodGroup;
    private String chronicConditions;
    private String allergies;
    private String medications;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String secondaryContactName;
    private String secondaryContactPhone;

    // Granular User Privacy Controls
    private boolean shareBloodGroup = true;
    private boolean shareAllergies = true;
    private boolean shareConditions = false;
    private boolean shareMedications = false;
    private boolean shareEmergencyContacts = true;

    private boolean active = true;
    private Instant generatedAt = Instant.now();
    private Instant lastAccessedAt;

    public EmergencyQrProfile() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getQrAccessKey() { return qrAccessKey; }
    public void setQrAccessKey(String qrAccessKey) { this.qrAccessKey = qrAccessKey; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getChronicConditions() { return chronicConditions; }
    public void setChronicConditions(String chronicConditions) { this.chronicConditions = chronicConditions; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getMedications() { return medications; }
    public void setMedications(String medications) { this.medications = medications; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public String getSecondaryContactName() { return secondaryContactName; }
    public void setSecondaryContactName(String secondaryContactName) { this.secondaryContactName = secondaryContactName; }

    public String getSecondaryContactPhone() { return secondaryContactPhone; }
    public void setSecondaryContactPhone(String secondaryContactPhone) { this.secondaryContactPhone = secondaryContactPhone; }

    public boolean isShareBloodGroup() { return shareBloodGroup; }
    public void setShareBloodGroup(boolean shareBloodGroup) { this.shareBloodGroup = shareBloodGroup; }

    public boolean isShareAllergies() { return shareAllergies; }
    public void setShareAllergies(boolean shareAllergies) { this.shareAllergies = shareAllergies; }

    public boolean isShareConditions() { return shareConditions; }
    public void setShareConditions(boolean shareConditions) { this.shareConditions = shareConditions; }

    public boolean isShareMedications() { return shareMedications; }
    public void setShareMedications(boolean shareMedications) { this.shareMedications = shareMedications; }

    public boolean isShareEmergencyContacts() { return shareEmergencyContacts; }
    public void setShareEmergencyContacts(boolean shareEmergencyContacts) { this.shareEmergencyContacts = shareEmergencyContacts; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }

    public Instant getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(Instant lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
}
