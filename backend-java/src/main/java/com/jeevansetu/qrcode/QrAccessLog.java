package com.jeevansetu.qrcode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "qr_access_logs")
public class QrAccessLog {
    @Id
    private String id;
    private String qrAccessKey;
    private String profileUserId;
    private String scannerRole;
    private String scannerIp;
    private String userAgent;
    private Instant accessedAt = Instant.now();

    public QrAccessLog() {}

    public QrAccessLog(String qrAccessKey, String profileUserId, String scannerRole, String scannerIp) {
        this.qrAccessKey = qrAccessKey;
        this.profileUserId = profileUserId;
        this.scannerRole = scannerRole;
        this.scannerIp = scannerIp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQrAccessKey() { return qrAccessKey; }
    public void setQrAccessKey(String qrAccessKey) { this.qrAccessKey = qrAccessKey; }

    public String getProfileUserId() { return profileUserId; }
    public void setProfileUserId(String profileUserId) { this.profileUserId = profileUserId; }

    public String getScannerRole() { return scannerRole; }
    public void setScannerRole(String scannerRole) { this.scannerRole = scannerRole; }

    public String getScannerIp() { return scannerIp; }
    public void setScannerIp(String scannerIp) { this.scannerIp = scannerIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Instant getAccessedAt() { return accessedAt; }
    public void setAccessedAt(Instant accessedAt) { this.accessedAt = accessedAt; }
}
