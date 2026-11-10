package com.jeevansetu.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.*;

@Service
public class EmergencyQrProfileService {

    private final EmergencyQrProfileRepository profileRepository;
    private final QrAccessLogRepository logRepository;

    public EmergencyQrProfileService(EmergencyQrProfileRepository profileRepository,
                                     QrAccessLogRepository logRepository) {
        this.profileRepository = profileRepository;
        this.logRepository = logRepository;
    }

    public static class QrProfileView {
        private EmergencyQrProfile profile;
        private String qrCodeBase64;
        private String accessUrl;

        public QrProfileView(EmergencyQrProfile profile, String qrCodeBase64, String accessUrl) {
            this.profile = profile;
            this.qrCodeBase64 = qrCodeBase64;
            this.accessUrl = accessUrl;
        }

        public EmergencyQrProfile getProfile() { return profile; }
        public String getQrCodeBase64() { return qrCodeBase64; }
        public String getAccessUrl() { return accessUrl; }
    }

    public QrProfileView getOrCreateProfile(String userId, String userFullName) {
        EmergencyQrProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    EmergencyQrProfile p = new EmergencyQrProfile();
                    p.setUserId(userId);
                    p.setFullName(userFullName != null ? userFullName : "Citizen User");
                    p.setQrAccessKey(UUID.randomUUID().toString());
                    p.setBloodGroup("O_POSITIVE");
                    p.setAllergies("Penicillin");
                    p.setChronicConditions("Asthma");
                    p.setEmergencyContactName("Primary Guardian");
                    p.setEmergencyContactPhone("98450-11223");
                    p.setActive(true);
                    return profileRepository.save(p);
                });

        String accessUrl = "https://chetanveerabomma-blip.github.io/JeevanSetu/emergency-profile.html?key=" + profile.getQrAccessKey();
        String qrImage = generateQrImageBase64(accessUrl, 260, 260);
        return new QrProfileView(profile, qrImage, accessUrl);
    }

    public EmergencyQrProfile updatePermissions(String userId, Map<String, Boolean> toggles) {
        EmergencyQrProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        if (toggles.containsKey("shareBloodGroup")) profile.setShareBloodGroup(toggles.get("shareBloodGroup"));
        if (toggles.containsKey("shareAllergies")) profile.setShareAllergies(toggles.get("shareAllergies"));
        if (toggles.containsKey("shareConditions")) profile.setShareConditions(toggles.get("shareConditions"));
        if (toggles.containsKey("shareMedications")) profile.setShareMedications(toggles.get("shareMedications"));
        if (toggles.containsKey("shareEmergencyContacts")) profile.setShareEmergencyContacts(toggles.get("shareEmergencyContacts"));

        return profileRepository.save(profile);
    }

    public QrProfileView regenerateAccessKey(String userId) {
        EmergencyQrProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        // Revoke old key by generating new random UUID
        profile.setQrAccessKey(UUID.randomUUID().toString());
        profile.setGeneratedAt(Instant.now());
        profileRepository.save(profile);

        String accessUrl = "https://chetanveerabomma-blip.github.io/JeevanSetu/emergency-profile.html?key=" + profile.getQrAccessKey();
        String qrImage = generateQrImageBase64(accessUrl, 260, 260);
        return new QrProfileView(profile, qrImage, accessUrl);
    }

    /**
     * Resolves emergency profile for an authorized on-scene paramedic/responder.
     * Enforces strict data-minimization: only returns fields authorized by citizen toggles.
     */
    public Map<String, Object> scanAndFetchPermittedData(String qrAccessKey, String scannerRole, String scannerIp) {
        EmergencyQrProfile profile = profileRepository.findByQrAccessKey(qrAccessKey)
                .orElseThrow(() -> new IllegalArgumentException("Invalid, expired, or revoked emergency QR code."));

        if (!profile.isActive()) {
            throw new IllegalStateException("This emergency QR profile has been deactivated by the user.");
        }

        // Record Access Log for user audit trail
        QrAccessLog logEntry = new QrAccessLog(qrAccessKey, profile.getUserId(),
                scannerRole != null ? scannerRole : "FIRST_RESPONDER", scannerIp != null ? scannerIp : "127.0.0.1");
        logRepository.save(logEntry);

        profile.setLastAccessedAt(Instant.now());
        profileRepository.save(profile);

        Map<String, Object> safePayload = new LinkedHashMap<>();
        safePayload.put("fullName", profile.getFullName());
        safePayload.put("profileActive", true);
        safePayload.put("scanTimestamp", Instant.now().toString());

        // Selective Disclosure Filter
        if (profile.isShareBloodGroup()) {
            safePayload.put("bloodGroup", profile.getBloodGroup());
        }
        if (profile.isShareAllergies()) {
            safePayload.put("allergies", profile.getAllergies());
        }
        if (profile.isShareConditions()) {
            safePayload.put("chronicConditions", profile.getChronicConditions());
        }
        if (profile.isShareMedications()) {
            safePayload.put("medications", profile.getMedications());
        }
        if (profile.isShareEmergencyContacts()) {
            Map<String, String> contacts = new HashMap<>();
            contacts.put("primaryName", profile.getEmergencyContactName());
            contacts.put("primaryPhone", profile.getEmergencyContactPhone());
            if (profile.getSecondaryContactName() != null) {
                contacts.put("secondaryName", profile.getSecondaryContactName());
                contacts.put("secondaryPhone", profile.getSecondaryContactPhone());
            }
            safePayload.put("emergencyContacts", contacts);
        }

        return safePayload;
    }

    public List<QrAccessLog> getAuditLogsForUser(String userId) {
        return logRepository.findByProfileUserIdOrderByAccessedAtDesc(userId);
    }

    private String generateQrImageBase64(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }
}
