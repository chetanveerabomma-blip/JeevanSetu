package com.jeevansetu.controller;

import com.jeevansetu.dto.ApiResponse;
import com.jeevansetu.model.AuditLog;
import com.jeevansetu.service.AnalyticsService;
import com.jeevansetu.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics & Audit", description = "Endpoints for operational dashboards, statistical charts, and audit trails")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AuditLogService auditLogService;

    public AnalyticsController(AnalyticsService analyticsService, AuditLogService auditLogService) {
        this.analyticsService = analyticsService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/overview")
    @Operation(summary = "Get high-level command center KPIs (active, critical, available resources)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getCommandCenterOverview()));
    }

    @GetMapping("/charts")
    @Operation(summary = "Get aggregated chart datasets and heatmap coordinates")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getChartAnalytics() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getChartAnalytics()));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get recent system audit logs for incident governance")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(auditLogService.getRecentLogs(limit)));
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    @Operation(summary = "Export incidents to CSV for audits and external reporting")
    public ResponseEntity<String> exportCsv(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.Instant startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.Instant endDate) {
        String csv = analyticsService.exportIncidentsCsv(startDate, endDate);
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"jeevansetu_incidents.csv\"")
                .body(csv);
    }

    @GetMapping("/reports/summary")
    @Operation(summary = "Generate structured executive emergency management briefing report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getExecutiveReport(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.Instant startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.Instant endDate) {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.generateExecutiveReport(startDate, endDate)));
    }
}
