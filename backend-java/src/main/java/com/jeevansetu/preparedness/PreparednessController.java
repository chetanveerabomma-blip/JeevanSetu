package com.jeevansetu.preparedness;

import com.jeevansetu.dto.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preparedness")
public class PreparednessController {

    private final PreparednessService preparednessService;

    public PreparednessController(PreparednessService preparednessService) {
        this.preparednessService = preparednessService;
    }

    @GetMapping("/guides")
    public ResponseEntity<ApiResponse<List<PreparednessGuide>>> getGuides(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(ApiResponse.ok("Preparedness guides retrieved", preparednessService.getGuides(category, language, query)));
    }

    @GetMapping("/guides/{id}")
    public ResponseEntity<ApiResponse<PreparednessGuide>> getGuide(@PathVariable String id) {
        return preparednessService.getGuideById(id)
                .map(g -> ResponseEntity.ok(ApiResponse.ok("Guide retrieved", g)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/checklists")
    public ResponseEntity<ApiResponse<List<EmergencyChecklist>>> getChecklists(@RequestParam(required = false) String audience) {
        return ResponseEntity.ok(ApiResponse.ok("Emergency checklists retrieved", preparednessService.getChecklists(audience)));
    }

    @GetMapping(value = "/checklists/{id}/download", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> downloadChecklist(@PathVariable String id) {
        String content = preparednessService.generateDownloadableChecklist(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"JeevanSetu_Emergency_Checklist.txt\"")
                .body(content);
    }
}
