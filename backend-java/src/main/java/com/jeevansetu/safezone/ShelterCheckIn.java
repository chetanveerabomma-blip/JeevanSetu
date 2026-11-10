package com.jeevansetu.safezone;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "shelter_checkins")
public class ShelterCheckIn {
    @Id
    private String id;
    private String checkInCode; // e.g. CHK-5821
    private String shelterId;
    private String shelterName;
    private String personName;
    private String contactPhone;
    private int familyMembersCount = 1;
    private String status = "ARRIVED"; // ARRIVED, SAFE, DEPARTED
    private boolean verifiedIdentity = false; // Never claim proof of ID unless physically checked
    private String verifiedByIdMethod; // GOVT_ID, BIOMETRIC, UNVERIFIED_SELF_REPORT
    private String specialMedicalNeeds;
    private Instant arrivalTime = Instant.now();
    private Instant departureTime;

    public ShelterCheckIn() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCheckInCode() { return checkInCode; }
    public void setCheckInCode(String checkInCode) { this.checkInCode = checkInCode; }

    public String getShelterId() { return shelterId; }
    public void setShelterId(String shelterId) { this.shelterId = shelterId; }

    public String getShelterName() { return shelterName; }
    public void setShelterName(String shelterName) { this.shelterName = shelterName; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public int getFamilyMembersCount() { return familyMembersCount; }
    public void setFamilyMembersCount(int familyMembersCount) { this.familyMembersCount = familyMembersCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isVerifiedIdentity() { return verifiedIdentity; }
    public void setVerifiedIdentity(boolean verifiedIdentity) { this.verifiedIdentity = verifiedIdentity; }

    public String getVerifiedByIdMethod() { return verifiedByIdMethod; }
    public void setVerifiedByIdMethod(String verifiedByIdMethod) { this.verifiedByIdMethod = verifiedByIdMethod; }

    public String getSpecialMedicalNeeds() { return specialMedicalNeeds; }
    public void setSpecialMedicalNeeds(String specialMedicalNeeds) { this.specialMedicalNeeds = specialMedicalNeeds; }

    public Instant getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Instant arrivalTime) { this.arrivalTime = arrivalTime; }

    public Instant getDepartureTime() { return departureTime; }
    public void setDepartureTime(Instant departureTime) { this.departureTime = departureTime; }
}
