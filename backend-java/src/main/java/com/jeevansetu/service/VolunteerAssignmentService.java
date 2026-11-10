package com.jeevansetu.service;

import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.Volunteer;
import com.jeevansetu.model.VolunteerTask;
import com.jeevansetu.repository.VolunteerRepository;
import com.jeevansetu.repository.VolunteerTaskRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class VolunteerAssignmentService {

    private final VolunteerTaskRepository taskRepository;
    private final VolunteerRepository volunteerRepository;
    private final AuditLogService auditLogService;
    private final WebSocketEventPublisher eventPublisher;

    public VolunteerAssignmentService(VolunteerTaskRepository taskRepository,
                                      VolunteerRepository volunteerRepository,
                                      AuditLogService auditLogService,
                                      WebSocketEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.volunteerRepository = volunteerRepository;
        this.auditLogService = auditLogService;
        this.eventPublisher = eventPublisher;
    }

    public VolunteerTask createVolunteerRequest(String incidentId, String incidentCode, String title, String description, String requiredSkill, String location, double lat, double lng) {
        VolunteerTask task = new VolunteerTask();
        task.setTaskCode("TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        task.setIncidentId(incidentId);
        task.setIncidentCode(incidentCode);
        task.setTitle(title);
        task.setDescription(description);
        task.setRequiredSkill(requiredSkill);
        task.setLocationAddress(location);
        task.setLatitude(lat);
        task.setLongitude(lng);
        task.setStatus("PROPOSED");

        VolunteerTask saved = taskRepository.save(task);

        eventPublisher.publish("VOLUNTEER_TASK_CREATED", Map.of(
                "taskId", saved.getId(),
                "taskCode", saved.getTaskCode(),
                "title", saved.getTitle(),
                "requiredSkill", saved.getRequiredSkill()
        ));

        return saved;
    }

    public VolunteerTask assignVolunteerToTask(String taskId, String volunteerId, String dispatcherName) {
        VolunteerTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found: " + volunteerId));

        task.setAssignedVolunteerId(volunteer.getId());
        task.setAssignedVolunteerName(volunteer.getName());
        task.setDispatcherApproved(true);
        task.setApprovedByDispatcher(dispatcherName != null ? dispatcherName : "Dispatcher");
        task.setStatus("APPROVED");

        VolunteerTask saved = taskRepository.save(task);

        // Mark volunteer as engaged
        volunteer.setAvailabilityStatus("ENGAGED");
        volunteer.setActiveIncidentId(task.getIncidentId());
        volunteerRepository.save(volunteer);

        auditLogService.log("VOLUNTEER_ASSIGNED", task.getId(), "VOLUNTEER_TASK",
                "Assigned to volunteer: " + volunteer.getName() + " by " + dispatcherName);

        eventPublisher.publish("VOLUNTEER_ASSIGNED", Map.of(
                "taskId", saved.getId(),
                "volunteerId", volunteer.getId(),
                "volunteerName", volunteer.getName(),
                "taskTitle", saved.getTitle()
        ));

        return saved;
    }

    public VolunteerTask acceptTask(String taskId, String volunteerId) {
        VolunteerTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        if (!task.getAssignedVolunteerId().equals(volunteerId)) {
            throw new IllegalArgumentException("Task not assigned to this volunteer");
        }

        task.setStatus("ACCEPTED");
        task.setAcceptedAt(Instant.now());
        VolunteerTask saved = taskRepository.save(task);

        eventPublisher.publish("VOLUNTEER_TASK_ACCEPTED", Map.of(
                "taskId", saved.getId(),
                "volunteerId", volunteerId
        ));

        return saved;
    }

    public VolunteerTask rejectTask(String taskId, String volunteerId, String reason) {
        VolunteerTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        task.setStatus("REJECTED");
        task.setCompletionNotes("Rejected by volunteer: " + reason);
        VolunteerTask saved = taskRepository.save(task);

        // Release volunteer
        Volunteer volunteer = volunteerRepository.findById(volunteerId).orElse(null);
        if (volunteer != null) {
            volunteer.setAvailabilityStatus("AVAILABLE");
            volunteer.setActiveIncidentId(null);
            volunteerRepository.save(volunteer);
        }

        eventPublisher.publish("VOLUNTEER_TASK_REJECTED", Map.of(
                "taskId", saved.getId(),
                "volunteerId", volunteerId,
                "reason", reason
        ));

        return saved;
    }

    public VolunteerTask completeTask(String taskId, String volunteerId, String notes) {
        VolunteerTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        task.setStatus("COMPLETED");
        task.setCompletedAt(Instant.now());
        task.setCompletionNotes(notes);
        VolunteerTask saved = taskRepository.save(task);

        // Update volunteer stats & release status
        Volunteer volunteer = volunteerRepository.findById(volunteerId).orElse(null);
        if (volunteer != null) {
            volunteer.setAvailabilityStatus("AVAILABLE");
            volunteer.setActiveIncidentId(null);
            volunteer.setCompletedTasksCount(volunteer.getCompletedTasksCount() + 1);
            volunteerRepository.save(volunteer);
        }

        auditLogService.log("VOLUNTEER_TASK_COMPLETED", task.getId(), "VOLUNTEER_TASK", "Completed with notes: " + notes);

        eventPublisher.publish("VOLUNTEER_TASK_COMPLETED", Map.of(
                "taskId", saved.getId(),
                "volunteerId", volunteerId
        ));

        return saved;
    }

    public List<VolunteerTask> getTasksForVolunteer(String volunteerId) {
        return taskRepository.findByAssignedVolunteerId(volunteerId, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<VolunteerTask> getAllTasks() {
        return taskRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
