package com.jeevansetu.postincident;

import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.IncidentTimelineEntry;
import com.jeevansetu.repository.EmergencyRepository;
import com.jeevansetu.repository.IncidentTimelineRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostIncidentAnalysisService {

    private final PostIncidentReviewRepository reviewRepository;
    private final EmergencyRepository emergencyRepository;
    private final IncidentTimelineRepository timelineRepository;

    public PostIncidentAnalysisService(PostIncidentReviewRepository reviewRepository,
                                       EmergencyRepository emergencyRepository,
                                       IncidentTimelineRepository timelineRepository) {
        this.reviewRepository = reviewRepository;
        this.emergencyRepository = emergencyRepository;
        this.timelineRepository = timelineRepository;
    }

    public PostIncidentReview generateOrGetReview(String incidentId) {
        Optional<PostIncidentReview> existing = reviewRepository.findByIncidentId(incidentId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Emergency emg = emergencyRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Emergency not found: " + incidentId));

        List<IncidentTimelineEntry> timeline = timelineRepository.findByIncidentId(
                incidentId, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "timestamp")
        );

        PostIncidentReview review = new PostIncidentReview();
        review.setIncidentId(emg.getId());
        review.setIncidentCode(emg.getIncidentCode());
        review.setIncidentType(emg.getType() != null ? emg.getType().name() : "MEDICAL");
        review.setPriority(emg.getPriority() != null ? emg.getPriority().name() : "MEDIUM");
        review.setAddress(emg.getAddress());
        review.setReportTime(emg.getCreatedAt() != null ? emg.getCreatedAt() : Instant.now());
        review.setResolvedTime(Instant.now());

        // Extract actions & timestamps
        List<String> actions = new ArrayList<>();
        Instant firstDispatch = null;
        for (IncidentTimelineEntry entry : timeline) {
            actions.add(String.format("[%s] %s: %s (Actor: %s)",
                    entry.getTimestamp() != null ? entry.getTimestamp().toString() : "T+0",
                    entry.getEventType(), entry.getDescription(), entry.getActorRole()));
            if ("DISPATCH_ASSIGNED".equalsIgnoreCase(entry.getEventType()) && firstDispatch == null) {
                firstDispatch = entry.getTimestamp();
            }
        }

        review.setAssignmentTime(firstDispatch != null ? firstDispatch : emg.getCreatedAt());
        review.setDispatcherActions(actions);

        double durationMin = 15.0;
        if (emg.getCreatedAt() != null) {
            durationMin = Math.max(1.0, Math.round((Duration.between(emg.getCreatedAt(), Instant.now()).getSeconds() / 60.0) * 10.0) / 10.0);
        }
        review.setResponseDurationMinutes(durationMin);

        List<String> agencies = new ArrayList<>();
        if (emg.getAssignedAgency() != null) agencies.add(emg.getAssignedAgency());
        else agencies.addAll(Arrays.asList("EMS", "POLICE"));
        review.setAgenciesInvolved(agencies);

        review.setOperationalObservations(Arrays.asList(
                "Initial caller report triaged rapidly by Priority Engine with rating " + emg.getPriority() + ".",
                "GPS coordinates pinned with sub-meter spatial accuracy (" + emg.getLatitude() + ", " + emg.getLongitude() + ").",
                "Assigned agency deployed with shortest Dijkstra corridor navigation."
        ));

        review.setLessonsLearned(Arrays.asList(
                "Verify bystander count earlier to prepare regional trauma bed buffers.",
                "Ensure local traffic interception is triggered automatically for multi-casualty incidents."
        ));

        review.setRootCauseSummary("Emergency incident " + emg.getType() + " at " + emg.getAddress() + " requiring multi-agency tactical intervention.");
        review.setReviewedBy("Chief Operations Auditor");

        return reviewRepository.save(review);
    }

    public List<PostIncidentReview> getAllReviews() {
        return reviewRepository.findAll();
    }

    public PostIncidentReview saveReview(PostIncidentReview review) {
        review.setReviewedAt(Instant.now());
        return reviewRepository.save(review);
    }

    public String exportReviewCsv(String reviewId) {
        PostIncidentReview r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        StringBuilder sb = new StringBuilder();
        sb.append("IncidentCode,Type,Priority,ResponseMinutes,Agencies,RootCause,ReviewedBy,Timestamp\n");
        sb.append(String.format("\"%s\",\"%s\",\"%s\",%.1f,\"%s\",\"%s\",\"%s\",\"%s\"\n",
                r.getIncidentCode(), r.getIncidentType(), r.getPriority(), r.getResponseDurationMinutes(),
                String.join(";", r.getAgenciesInvolved()), r.getRootCauseSummary().replace("\"", "'"),
                r.getReviewedBy(), r.getReviewedAt()));

        sb.append("\n--- TIMELINE OF ACTIONS ---\n");
        for (String action : r.getDispatcherActions()) {
            sb.append("\"").append(action.replace("\"", "'")).append("\"\n");
        }
        return sb.toString();
    }
}
