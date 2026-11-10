package com.jeevansetu.publicsafety;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PublicSafetyService {

    private final PublicSafetyReportRepository reportRepository;

    public PublicSafetyService(PublicSafetyReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public PublicSafetyReport submitReport(PublicSafetyReport report) {
        report.setReportCode("PSR-" + System.currentTimeMillis() % 1000000);
        report.setReportedAt(Instant.now());
        report.setStatus("PENDING_VERIFICATION");

        // Spatial & Category Duplicate Detection (within 300m and last 24h)
        List<PublicSafetyReport> recentActive = reportRepository.findByStatusIn(Arrays.asList("PENDING_VERIFICATION", "VERIFIED", "IN_PROGRESS"));
        Instant dayAgo = Instant.now().minus(24, ChronoUnit.HOURS);

        for (PublicSafetyReport existing : recentActive) {
            if (existing.getReportedAt() != null && existing.getReportedAt().isAfter(dayAgo)) {
                if (existing.getCategory() != null && existing.getCategory().equalsIgnoreCase(report.getCategory())) {
                    double distKm = haversineDistance(report.getLatitude(), report.getLongitude(),
                            existing.getLatitude(), existing.getLongitude());
                    if (distKm <= 0.3) { // 300 meters
                        report.setDuplicateSuspected(true);
                        report.setDuplicateOfReportId(existing.getId());
                        break;
                    }
                }
            }
        }

        return reportRepository.save(report);
    }

    public List<PublicSafetyReport> getAllReports(String status, String category) {
        if (status != null && !status.isEmpty()) {
            return reportRepository.findByStatus(status);
        }
        if (category != null && !category.isEmpty()) {
            return reportRepository.findByCategory(category);
        }
        return reportRepository.findTop50ByOrderByReportedAtDesc();
    }

    public List<PublicSafetyReport> getActiveMapHazards() {
        return reportRepository.findByStatusIn(Arrays.asList("VERIFIED", "IN_PROGRESS"));
    }

    public Optional<PublicSafetyReport> verifyReport(String id, String status, String remarks, String verifier) {
        return reportRepository.findById(id).map(r -> {
            r.setStatus(status);
            r.setAdminRemarks(remarks);
            r.setVerifiedBy(verifier);
            if ("RESOLVED".equals(status)) {
                r.setResolvedAt(Instant.now());
            }
            return reportRepository.save(r);
        });
    }

    public Optional<PublicSafetyReport> upvoteReport(String id) {
        return reportRepository.findById(id).map(r -> {
            r.setUpvoteCount(r.getUpvoteCount() + 1);
            return reportRepository.save(r);
        });
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
