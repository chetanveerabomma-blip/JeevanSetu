package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.Volunteer;
import com.jeevansetu.model.VolunteerTask;
import com.jeevansetu.service.VolunteerAssignmentService;
import com.jeevansetu.service.VolunteerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/volunteers")
@CrossOrigin(origins = "*")
public class VolunteerController {

    private final VolunteerService volunteerService;
    private final VolunteerAssignmentService assignmentService;

    public VolunteerController(VolunteerService volunteerService, VolunteerAssignmentService assignmentService) {
        this.volunteerService = volunteerService;
        this.assignmentService = assignmentService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Volunteer>> register(@RequestBody Volunteer volunteer) {
        Volunteer registered = volunteerService.registerVolunteer(volunteer);
        return ResponseEntity.ok(ApiResponse.ok("Volunteer registered successfully", registered));
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok(volunteerService.getVolunteerOverview()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Volunteer>>> getAll(
            @RequestParam(defaultValue = "true") boolean maskSensitive) {
        return ResponseEntity.ok(ApiResponse.ok(volunteerService.getAllVolunteers(maskSensitive)));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Volunteer>>> getAvailable(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radiusKm) {
        return ResponseEntity.ok(ApiResponse.ok(volunteerService.getAvailableVolunteers(skill, lat, lng, radiusKm)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Volunteer>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(volunteerService.getVolunteerById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Volunteer>> updateProfile(
            @PathVariable String id,
            @RequestBody Volunteer updated) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", volunteerService.updateProfile(id, updated)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Volunteer>> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "AVAILABLE");
        return ResponseEntity.ok(ApiResponse.ok("Status updated", volunteerService.updateStatus(id, status)));
    }

    // Task & Assignment Endpoints
    @PostMapping("/tasks/request")
    public ResponseEntity<ApiResponse<VolunteerTask>> createRequest(@RequestBody Map<String, Object> req) {
        String incidentId = (String) req.get("incidentId");
        String incidentCode = (String) req.get("incidentCode");
        String title = (String) req.get("title");
        String description = (String) req.get("description");
        String skill = (String) req.get("requiredSkill");
        String location = (String) req.get("locationAddress");
        double lat = req.get("latitude") != null ? Double.parseDouble(req.get("latitude").toString()) : 0.0;
        double lng = req.get("longitude") != null ? Double.parseDouble(req.get("longitude").toString()) : 0.0;

        VolunteerTask task = assignmentService.createVolunteerRequest(incidentId, incidentCode, title, description, skill, location, lat, lng);
        return ResponseEntity.ok(ApiResponse.ok("Volunteer request created", task));
    }

    @PostMapping("/tasks/{taskId}/assign")
    public ResponseEntity<ApiResponse<VolunteerTask>> assignVolunteer(
            @PathVariable String taskId,
            @RequestBody Map<String, String> body) {
        String volunteerId = body.get("volunteerId");
        String dispatcherName = body.getOrDefault("dispatcherName", "Dispatcher");

        VolunteerTask task = assignmentService.assignVolunteerToTask(taskId, volunteerId, dispatcherName);
        return ResponseEntity.ok(ApiResponse.ok("Volunteer assigned to task", task));
    }

    @PostMapping("/tasks/{taskId}/accept")
    public ResponseEntity<ApiResponse<VolunteerTask>> acceptTask(
            @PathVariable String taskId,
            @RequestBody Map<String, String> body) {
        String volunteerId = body.get("volunteerId");
        VolunteerTask task = assignmentService.acceptTask(taskId, volunteerId);
        return ResponseEntity.ok(ApiResponse.ok("Task accepted", task));
    }

    @PostMapping("/tasks/{taskId}/reject")
    public ResponseEntity<ApiResponse<VolunteerTask>> rejectTask(
            @PathVariable String taskId,
            @RequestBody Map<String, String> body) {
        String volunteerId = body.get("volunteerId");
        String reason = body.getOrDefault("reason", "Unavailable");
        VolunteerTask task = assignmentService.rejectTask(taskId, volunteerId, reason);
        return ResponseEntity.ok(ApiResponse.ok("Task rejected", task));
    }

    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<ApiResponse<VolunteerTask>> completeTask(
            @PathVariable String taskId,
            @RequestBody Map<String, String> body) {
        String volunteerId = body.get("volunteerId");
        String notes = body.getOrDefault("notes", "Task finished successfully");
        VolunteerTask task = assignmentService.completeTask(taskId, volunteerId, notes);
        return ResponseEntity.ok(ApiResponse.ok("Task marked as completed", task));
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<ApiResponse<List<VolunteerTask>>> getVolunteerTasks(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(assignmentService.getTasksForVolunteer(id)));
    }

    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<VolunteerTask>>> getAllTasks() {
        return ResponseEntity.ok(ApiResponse.ok(assignmentService.getAllTasks()));
    }
}
