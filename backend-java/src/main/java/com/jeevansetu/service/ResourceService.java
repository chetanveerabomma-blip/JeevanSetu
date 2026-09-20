package com.jeevansetu.service;

import com.jeevansetu.algorithm.DijkstraRouter;
import com.jeevansetu.algorithm.ResourceSuitabilityScorer;
import com.jeevansetu.algorithm.RouteResult;
import com.jeevansetu.algorithm.ScoredResource;
import com.jeevansetu.exception.BadRequestException;
import com.jeevansetu.exception.ResourceNotFoundException;
import com.jeevansetu.model.*;
import com.jeevansetu.repository.AssignmentRepository;
import com.jeevansetu.repository.HospitalRepository;
import com.jeevansetu.repository.NotificationRepository;
import com.jeevansetu.repository.ResponderUnitRepository;
import com.jeevansetu.websocket.WebSocketEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    private final ResponderUnitRepository responderUnitRepository;
    private final AssignmentRepository assignmentRepository;
    private final HospitalRepository hospitalRepository;
    private final NotificationRepository notificationRepository;
    private final EmergencyService emergencyService;
    private final ResourceSuitabilityScorer suitabilityScorer;
    private final DijkstraRouter dijkstraRouter;
    private final WebSocketEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    public ResourceService(
            ResponderUnitRepository responderUnitRepository,
            AssignmentRepository assignmentRepository,
            HospitalRepository hospitalRepository,
            NotificationRepository notificationRepository,
            EmergencyService emergencyService,
            ResourceSuitabilityScorer suitabilityScorer,
            DijkstraRouter dijkstraRouter,
            WebSocketEventPublisher eventPublisher,
            AuditLogService auditLogService) {
        this.responderUnitRepository = responderUnitRepository;
        this.assignmentRepository = assignmentRepository;
        this.hospitalRepository = hospitalRepository;
        this.notificationRepository = notificationRepository;
        this.emergencyService = emergencyService;
        this.suitabilityScorer = suitabilityScorer;
        this.dijkstraRouter = dijkstraRouter;
        this.eventPublisher = eventPublisher;
        this.auditLogService = auditLogService;
    }

    public List<ResponderUnit> getAllResponderUnits() {
        return responderUnitRepository.findAll();
    }

    public List<ResponderUnit> getUnitsByType(ResourceType type) {
        return responderUnitRepository.findByType(type);
    }

    public List<ResponderUnit> getUnitsByStatus(ResourceStatus status) {
        return responderUnitRepository.findByStatus(status);
    }

    public ResponderUnit getUnitById(String id) {
        return responderUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responder unit not found with id: " + id));
    }

    public ResponderUnit getUnitByCode(String code) {
        return responderUnitRepository.findByUnitCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Responder unit not found with code: " + code));
    }

    /**
     * Algorithmic recommendation: Scores candidate units for an emergency and returns ranked list.
     */
    public List<ScoredResource> getRecommendedUnits(String emergencyId, ResourceType type) {
        Emergency emergency = emergencyService.getEmergencyById(emergencyId);
        List<ResponderUnit> candidates = type != null
                ? responderUnitRepository.findByType(type)
                : responderUnitRepository.findAll();

        return suitabilityScorer.rankResources(emergency, candidates);
    }

    /**
     * Dispatches a specific responder unit to an emergency.
     * Computes route using Dijkstra's algorithm.
     */
    public Assignment assignUnitToEmergency(String emergencyId, String resourceId, String dispatcherEmail) {
        Emergency emergency = emergencyService.getEmergencyById(emergencyId);
        ResponderUnit unit = getUnitById(resourceId);

        if (unit.getStatus() != ResourceStatus.AVAILABLE && unit.getStatus() != ResourceStatus.EN_ROUTE) {
            throw new BadRequestException("Unit " + unit.getUnitCode() + " is currently " + unit.getStatus());
        }

        // 1. Calculate Dijkstra optimal route from unit to incident
        RouteResult route = dijkstraRouter.calculateRoute(
                unit.getLatitude(), unit.getLongitude(),
                emergency.getLatitude(), emergency.getLongitude()
        );

        // 2. Create Assignment record
        Assignment assignment = new Assignment();
        assignment.setIncidentId(emergency.getId());
        assignment.setIncidentCode(emergency.getIncidentCode());
        assignment.setResourceId(unit.getId());
        assignment.setResourceType(unit.getType());
        assignment.setUnitCode(unit.getUnitCode());
        assignment.setResponderName(unit.getName());
        assignment.setResponderContact(unit.getContactPhone());
        assignment.setStatus(ResourceStatus.ASSIGNED);
        assignment.setEstimatedDistanceKm(route.getTotalDistanceKm());
        assignment.setEstimatedMinutes(route.getEstimatedMinutes());
        assignment.setRouteCoordinates(route.getPathCoordinates());
        assignment.setRoutingSummary(route.getRouteSummary());
        assignment.setAssignedAt(Instant.now());

        Assignment savedAssignment = assignmentRepository.save(assignment);

        // 3. Update Unit status
        unit.setStatus(ResourceStatus.ASSIGNED);
        unit.setCurrentIncidentId(emergency.getId());
        unit.setLastUpdated(Instant.now());
        responderUnitRepository.save(unit);

        // 4. Update Emergency document
        if (!emergency.getAssignedResourceIds().contains(unit.getId())) {
            emergency.getAssignedResourceIds().add(unit.getId());
        }
        if (emergency.getStatus() == EmergencyStatus.REPORTED || emergency.getStatus() == EmergencyStatus.PROCESSING) {
            emergency.setStatus(EmergencyStatus.ASSIGNED);
        }
        emergencyService.updateStatus(
                emergency.getId(),
                EmergencyStatus.ASSIGNED,
                dispatcherEmail,
                "CONTROL_CENTER",
                String.format("Assigned %s [%s] (ETA: %d mins, %.1f km)",
                        unit.getType(), unit.getUnitCode(), route.getEstimatedMinutes(), route.getTotalDistanceKm())
        );

        // 5. Send notification to Responder
        Notification notif = new Notification(
                null,
                mapRoleFromResourceType(unit.getType()),
                "🚨 New Assignment: " + emergency.getIncidentCode(),
                String.format("You are dispatched to %s. Priority: %s", emergency.getAddress(), emergency.getPriority()),
                emergency.getId(),
                emergency.getIncidentCode(),
                "ASSIGNMENT"
        );
        notificationRepository.save(notif);

        // 6. Broadcast via WebSocket
        eventPublisher.publishDispatchAssigned(savedAssignment);
        eventPublisher.publishNotification(notif);

        // 7. Audit log
        auditLogService.logAction(
                dispatcherEmail,
                "CONTROL_CENTER",
                "RESOURCE_ASSIGNED",
                emergency.getId(),
                emergency.getIncidentCode(),
                String.format("Assigned unit %s (%s)", unit.getUnitCode(), unit.getType()),
                ResourceStatus.AVAILABLE.name(),
                ResourceStatus.ASSIGNED.name()
        );

        return savedAssignment;
    }

    /**
     * Responder transitions status through the emergency workflow.
     */
    public Assignment updateAssignmentStatus(String assignmentId, ResourceStatus newStatus, String actorEmail, String actorRole) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));

        ResourceStatus oldStatus = assignment.getStatus();
        assignment.setStatus(newStatus);
        Instant now = Instant.now();

        ResponderUnit unit = responderUnitRepository.findById(assignment.getResourceId()).orElse(null);

        switch (newStatus) {
            case ASSIGNED -> assignment.setAcceptedAt(now);
            case EN_ROUTE -> {
                assignment.setEnRouteAt(now);
                if (unit != null) unit.setStatus(ResourceStatus.EN_ROUTE);
                emergencyService.updateStatus(
                        assignment.getIncidentId(), EmergencyStatus.EN_ROUTE, actorEmail, actorRole,
                        String.format("Unit %s is EN ROUTE to incident location.", assignment.getUnitCode())
                );
            }
            case ARRIVED -> {
                assignment.setArrivedAt(now);
                if (unit != null) unit.setStatus(ResourceStatus.BUSY);
                emergencyService.updateStatus(
                        assignment.getIncidentId(), EmergencyStatus.ARRIVED, actorEmail, actorRole,
                        String.format("Unit %s has ARRIVED at emergency scene.", assignment.getUnitCode())
                );
            }
            case BUSY -> {
                assignment.setPatientHandledAt(now);
                emergencyService.updateStatus(
                        assignment.getIncidentId(), EmergencyStatus.PATIENT_HANDLED, actorEmail, actorRole,
                        String.format("Unit %s has secured and treated patient.", assignment.getUnitCode())
                );
            }
            case AVAILABLE -> { // COMPLETED
                assignment.setCompletedAt(now);
                if (unit != null) {
                    unit.setStatus(ResourceStatus.AVAILABLE);
                    unit.setCurrentIncidentId(null);
                    unit.setCompletedTrips(unit.getCompletedTrips() + 1);
                    responderUnitRepository.save(unit);
                }
                emergencyService.updateStatus(
                        assignment.getIncidentId(), EmergencyStatus.RESOLVED, actorEmail, actorRole,
                        String.format("Incident successfully resolved by unit %s.", assignment.getUnitCode())
                );
            }
            default -> {}
        }

        if (unit != null) {
            unit.setLastUpdated(now);
            responderUnitRepository.save(unit);
        }

        Assignment saved = assignmentRepository.save(assignment);
        eventPublisher.publishDispatchStatusChanged(saved);

        auditLogService.logAction(
                actorEmail, actorRole, "RESPONDER_STATUS_CHANGED",
                assignment.getIncidentId(), assignment.getIncidentCode(),
                "Unit " + assignment.getUnitCode() + " status: " + newStatus,
                oldStatus.name(), newStatus.name()
        );

        return saved;
    }

    /**
     * Hospital transfer workflow: notifies receiving hospital and recalculates route to hospital.
     */
    public RouteResult routeToHospital(String assignmentId, String hospitalId, String actorEmail) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found: " + hospitalId));

        Emergency emergency = emergencyService.getEmergencyById(assignment.getIncidentId());

        RouteResult hospitalRoute = dijkstraRouter.calculateRoute(
                emergency.getLatitude(), emergency.getLongitude(),
                hospital.getLatitude(), hospital.getLongitude()
        );

        assignment.setRouteCoordinates(hospitalRoute.getPathCoordinates());
        assignment.setEstimatedDistanceKm(hospitalRoute.getTotalDistanceKm());
        assignment.setEstimatedMinutes(hospitalRoute.getEstimatedMinutes());
        assignment.setRoutingSummary("Transit to " + hospital.getName() + " (" + hospitalRoute.getEstimatedMinutes() + " mins)");
        assignment.setHospitalReachedAt(Instant.now());
        assignmentRepository.save(assignment);

        emergencyService.updateStatus(
                emergency.getId(), EmergencyStatus.REACHED_HOSPITAL, actorEmail, "AMBULANCE",
                String.format("En route / reached %s for admission.", hospital.getName())
        );

        // Notify Hospital
        Notification hospitalNotif = new Notification(
                null,
                Role.HOSPITAL,
                "🚨 Inbound Emergency Trauma Admission: " + emergency.getIncidentCode(),
                String.format("Ambulance %s arriving at %s. Casualties: %d. Priority: %s",
                        assignment.getUnitCode(), hospital.getName(), emergency.getInjuredCount(), emergency.getPriority()),
                emergency.getId(),
                emergency.getIncidentCode(),
                "HOSPITAL_ALERT"
        );
        notificationRepository.save(hospitalNotif);
        eventPublisher.publishNotification(hospitalNotif);

        return hospitalRoute;
    }

    public List<Assignment> getAssignmentsForIncident(String incidentId) {
        return assignmentRepository.findByIncidentId(incidentId);
    }

    public Optional<Assignment> getActiveAssignmentForUnit(String unitId) {
        List<Assignment> active = assignmentRepository.findByResourceIdAndStatusIn(
                unitId, List.of(ResourceStatus.ASSIGNED, ResourceStatus.EN_ROUTE, ResourceStatus.ARRIVED, ResourceStatus.BUSY)
        );
        return active.isEmpty() ? Optional.empty() : Optional.of(active.get(0));
    }

    private Role mapRoleFromResourceType(ResourceType type) {
        if (type == null) return Role.CONTROL_CENTER;
        return switch (type) {
            case AMBULANCE -> Role.AMBULANCE;
            case POLICE -> Role.POLICE;
            case FIRE_UNIT -> Role.FIRE;
            case HOSPITAL -> Role.HOSPITAL;
            case BLOOD_BANK -> Role.BLOOD_BANK;
            case VOLUNTEER -> Role.VOLUNTEER;
            default -> Role.CONTROL_CENTER;
        };
    }
}
