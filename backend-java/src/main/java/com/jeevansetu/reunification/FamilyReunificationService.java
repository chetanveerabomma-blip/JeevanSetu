package com.jeevansetu.reunification;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FamilyReunificationService {

    private final FamilyGroupRepository familyGroupRepository;
    private final MissingPersonRepository missingPersonRepository;
    private final SafeArrivalRepository safeArrivalRepository;

    public FamilyReunificationService(FamilyGroupRepository familyGroupRepository,
                                     MissingPersonRepository missingPersonRepository,
                                     SafeArrivalRepository safeArrivalRepository) {
        this.familyGroupRepository = familyGroupRepository;
        this.missingPersonRepository = missingPersonRepository;
        this.safeArrivalRepository = safeArrivalRepository;
    }

    public FamilyGroup registerFamilyGroup(FamilyGroup group) {
        return familyGroupRepository.save(group);
    }

    public Optional<FamilyGroup> getFamilyGroup(String id) {
        return familyGroupRepository.findById(id);
    }

    public MissingPersonReport reportMissingPerson(MissingPersonReport report) {
        report.setReportedAt(Instant.now());
        report.setUpdatedAt(Instant.now());
        report.setStatus("REPORTED");
        return missingPersonRepository.save(report);
    }

    /**
     * Search missing persons with strict privacy masking for public users.
     */
    public List<MissingPersonReport> searchMissingPersons(String query, boolean isAuthorizedOfficial) {
        List<MissingPersonReport> reports;
        if (query != null && !query.trim().isEmpty()) {
            reports = missingPersonRepository.findByPersonNameContainingIgnoreCase(query.trim());
        } else {
            reports = missingPersonRepository.findByStatusNot("REUNITED");
        }

        if (isAuthorizedOfficial) {
            return reports;
        }

        // Apply strict privacy redaction for public listing
        return reports.stream()
                .filter(MissingPersonReport::isConsentForPublicListing)
                .map(this::maskSensitiveData)
                .collect(Collectors.toList());
    }

    public Optional<MissingPersonReport> updateVerificationStatus(String id, String status, String officialRemarks, String verifiedBy) {
        return missingPersonRepository.findById(id).map(r -> {
            r.setStatus(status);
            r.setVerifiedByOfficial(true);
            r.setVerifiedBy(verifiedBy);
            r.setOfficialRemarks(officialRemarks);
            r.setUpdatedAt(Instant.now());
            return missingPersonRepository.save(r);
        });
    }

    public SafeArrivalConfirmation confirmSafeArrival(SafeArrivalConfirmation conf) {
        conf.setArrivalTime(Instant.now());
        SafeArrivalConfirmation saved = safeArrivalRepository.save(conf);

        // If person was reported missing, advance status to SIGHTED or LOCATED
        if (conf.getPersonName() != null) {
            List<MissingPersonReport> matching = missingPersonRepository.findByPersonNameContainingIgnoreCase(conf.getPersonName().trim());
            for (MissingPersonReport m : matching) {
                if (!"REUNITED".equals(m.getStatus())) {
                    m.setStatus("LOCATED");
                    m.setOfficialRemarks("Safe arrival confirmed at " + conf.getArrivalLocation());
                    m.setUpdatedAt(Instant.now());
                    missingPersonRepository.save(m);
                }
            }
        }
        return saved;
    }

    public List<SafeArrivalConfirmation> getRecentSafeArrivals() {
        return safeArrivalRepository.findTop50ByOrderByArrivalTimeDesc();
    }

    private MissingPersonReport maskSensitiveData(MissingPersonReport original) {
        MissingPersonReport masked = new MissingPersonReport();
        masked.setId(original.getId());
        masked.setFamilyGroupId(original.getFamilyGroupId());
        masked.setPersonName(maskName(original.getPersonName()));
        masked.setAge(original.getAge());
        masked.setGender(original.getGender());
        masked.setPhysicalDescription(original.getPhysicalDescription());
        masked.setPhotoUrl(original.getPhotoUrl());
        masked.setLastKnownLocation(original.getLastKnownLocation());
        masked.setStatus(original.getStatus());
        masked.setVerifiedByOfficial(original.isVerifiedByOfficial());
        masked.setOfficialRemarks(original.getOfficialRemarks());
        masked.setReportedAt(original.getReportedAt());
        // Mask contact details to protect victims and families
        masked.setContactPersonName("Authorized Official / Guardian");
        masked.setContactPhone("Masked (Contact Helpline 112)");
        return masked;
    }

    private String maskName(String name) {
        if (name == null || name.length() <= 2) return "***";
        String[] parts = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.length() > 1) {
                sb.append(p.charAt(0)).append("*** ");
            } else {
                sb.append(p).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
