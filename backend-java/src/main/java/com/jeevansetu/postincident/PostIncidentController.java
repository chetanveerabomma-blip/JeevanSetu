package com.jeevansetu.postincident;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-incident")
public class PostIncidentController {

    private final PostIncidentAnalysisService analysisService;

    public PostIncidentController(PostIncidentAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<ApiResponse<PostIncidentReview>> getReviewForIncident(@PathVariable String incidentId) {
        return ResponseEntity.ok(ApiResponse.ok("Post-incident review generated", analysisService.generateOrGetReview(incidentId)));
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<PostIncidentReview>>> getAllReviews() {
        return ResponseEntity.ok(ApiResponse.ok("All finalized post-incident reviews", analysisService.getAllReviews()));
    }

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<PostIncidentReview>> saveReview(@RequestBody PostIncidentReview review) {
        return ResponseEntity.ok(ApiResponse.ok("Post-incident review saved", analysisService.saveReview(review)));
    }

    @GetMapping(value = "/reviews/{id}/export/csv", produces = "text/csv")
    public ResponseEntity<String> exportCsv(@PathVariable String id) {
        String csv = analysisService.exportReviewCsv(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"JeevanSetu_Incident_Review_" + id + ".csv\"")
                .body(csv);
    }
}
