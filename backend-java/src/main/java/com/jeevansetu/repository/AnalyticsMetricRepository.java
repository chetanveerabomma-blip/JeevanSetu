package com.jeevansetu.repository;

import com.jeevansetu.model.AnalyticsMetric;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsMetricRepository extends MongoRepository<AnalyticsMetric, String> {
    List<AnalyticsMetric> findByMetricCategory(String metricCategory);
}
