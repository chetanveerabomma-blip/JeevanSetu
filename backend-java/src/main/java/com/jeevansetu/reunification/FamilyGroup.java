package com.jeevansetu.reunification;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "family_groups")
public class FamilyGroup {
    @Id
    private String id;
    private String groupName;
    private String primaryContactUserId;
    private String primaryContactPhone;
    private List<String> memberNames = new ArrayList<>();
    private String emergencyMeetingPoint;
    private Instant createdAt = Instant.now();

    public FamilyGroup() {}

    public FamilyGroup(String groupName, String primaryContactUserId, String primaryContactPhone, String emergencyMeetingPoint) {
        this.groupName = groupName;
        this.primaryContactUserId = primaryContactUserId;
        this.primaryContactPhone = primaryContactPhone;
        this.emergencyMeetingPoint = emergencyMeetingPoint;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getPrimaryContactUserId() { return primaryContactUserId; }
    public void setPrimaryContactUserId(String primaryContactUserId) { this.primaryContactUserId = primaryContactUserId; }

    public String getPrimaryContactPhone() { return primaryContactPhone; }
    public void setPrimaryContactPhone(String primaryContactPhone) { this.primaryContactPhone = primaryContactPhone; }

    public List<String> getMemberNames() { return memberNames; }
    public void setMemberNames(List<String> memberNames) { this.memberNames = memberNames; }

    public String getEmergencyMeetingPoint() { return emergencyMeetingPoint; }
    public void setEmergencyMeetingPoint(String emergencyMeetingPoint) { this.emergencyMeetingPoint = emergencyMeetingPoint; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
