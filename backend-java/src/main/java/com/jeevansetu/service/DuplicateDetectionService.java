package com.jeevansetu.service;

import com.jeevansetu.dto.DuplicateMatchResult;
import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.EmergencyStatus;
import com.jeevansetu.model.EmergencyType;
import com.jeevansetu.repository.EmergencyRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class DuplicateDetectionService {

    private final EmergencyRepository emergencyRepository;

    public DuplicateDetectionService(EmergencyRepository emergencyRepository) {
        this.emergencyRepository = emergencyRepository;
    }

    public List<DuplicateMatchResult> checkPotentialDuplicates(double lat, double lng, EmergencyType type, String description, Instant timestamp) {
        List<DuplicateMatchResult> matches = new ArrayList<>();
        Instant checkTime = timestamp != null ? timestamp : Instant.now();

        // Check active / recent incidents reported within last 2 hours
        Instant cutoff = checkTime.minus(Duration.ofHours(2));
        List<Emergency> recents = emergencyRepository.findByStatusNotIn(
                List.of(EmergencyStatus.RESOLVED, EmergencyStatus.CANCELLED),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        for (Emergency existing : recents) {
            if (existing.getCreatedAt() != null && existing.getCreatedAt().isBefore(cutoff)) {
                continue;
            }

            double distanceMeters = GeoFenceService.calculateDistanceMeters(lat, lng, existing.getLatitude(), existing.getLongitude());
            long minutesApart = existing.getCreatedAt() != null
                    ? Math.abs(Duration.between(existing.getCreatedAt(), checkTime).toMinutes())
                    : 0;

            // Only evaluate if within 1500 meters
            if (distanceMeters <= 1500) {
                double score = 0;
                List<String> reasons = new ArrayList<>();

                // Distance factor (up to 40 pts)
                if (distanceMeters <= 100) {
                    score += 40;
                    reasons.add(String.format("Exact location proximity (%.0fm)", distanceMeters));
                } else if (distanceMeters <= 500) {
                    score += 30;
                    reasons.add(String.format("Very close proximity (%.0fm)", distanceMeters));
                } else {
                    score += 15;
                    reasons.add(String.format("Nearby vicinity (%.0fm)", distanceMeters));
                }

                // Category factor (up to 30 pts)
                if (existing.getType() == type) {
                    score += 30;
                    reasons.add("Matching emergency category: " + type);
                }

                // Time factor (up to 20 pts)
                if (minutesApart <= 15) {
                    score += 20;
                    reasons.add(String.format("Reported %d mins ago", minutesApart));
                } else if (minutesApart <= 45) {
                    score += 10;
                    reasons.add(String.format("Reported %d mins ago", minutesApart));
                }

                // Text Description similarity (up to 20 pts)
                double textSim = computeDiceCoefficient(description, existing.getDescription());
                if (textSim >= 0.4) {
                    score += Math.min(20, textSim * 25);
                    reasons.add(String.format("Narrative description similarity (%.0f%%)", textSim * 100));
                }

                // If combined confidence >= 50%, flag as potential duplicate
                if (score >= 50) {
                    matches.add(new DuplicateMatchResult(
                            existing.getId(),
                            existing.getIncidentCode(),
                            existing.getType() != null ? existing.getType().name() : "GENERAL",
                            existing.getStatus() != null ? existing.getStatus().name() : "REPORTED",
                            Math.min(100.0, score),
                            distanceMeters,
                            minutesApart,
                            String.join(" • ", reasons)
                    ));
                }
            }
        }

        matches.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        return matches;
    }

    private double computeDiceCoefficient(String s1, String s2) {
        if (s1 == null || s2 == null || s1.isBlank() || s2.isBlank()) return 0.0;

        Set<String> words1 = extractWords(s1.toLowerCase());
        Set<String> words2 = extractWords(s2.toLowerCase());

        if (words1.isEmpty() || words2.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        return (2.0 * intersection.size()) / (words1.size() + words2.size());
    }

    private Set<String> extractWords(String text) {
        String[] tokens = text.split("[^a-zA-Z0-9]+");
        Set<String> set = new HashSet<>();
        for (String token : tokens) {
            if (token.length() > 2) { // Skip short stopwords
                set.add(token);
            }
        }
        return set;
    }
}
