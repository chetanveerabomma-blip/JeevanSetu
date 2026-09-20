package com.jeevansetu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "analytics")
public class AnalyticsMetric {
    @Id
    private String id;

    @Indexed
    private String metricCategory; // "INCIDENT_VOLUME", "AVG_RESPONSE_TIME", "RESOURCE_UTILIZATION"

    private double value;
    private Map<String, Object> metadata;

    @Indexed
    private Instant recordedAt = Instant.now();

    public AnalyticsMetric() {}

    public AnalyticsMetric(String metricCategory, double value, Map<String, Object> metadata) {
        this.metricCategory = metricCategory;
        this.value = value;
        this.metadata = metadata;
        this.recordedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMetricCategory() { return metricCategory; }
    public void setMetricCategory(String metricCategory) { this.metricCategory = metricCategory; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public Instant getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Instant recordedAt) { this.recordedAt = recordedAt; }
}
