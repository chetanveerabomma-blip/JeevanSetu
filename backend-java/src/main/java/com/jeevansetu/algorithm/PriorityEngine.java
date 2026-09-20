package com.jeevansetu.algorithm;

import com.jeevansetu.model.Emergency;
import com.jeevansetu.model.EmergencyPriority;
import com.jeevansetu.model.EmergencyType;
import org.springframework.stereotype.Component;

/**
 * Authoritative Java Rule Engine for Emergency Priority Determination.
 * Evaluates clinical trauma indicators, environmental hazard factors, and casualty volume.
 */
@Component
public class PriorityEngine {

    public EmergencyPriority determinePriority(Emergency emergency) {
        if (emergency == null) {
            return EmergencyPriority.MEDIUM;
        }

        // 1. Life-Threatening Clinical Conditions -> CRITICAL
        if (emergency.isUnconscious()) {
            return EmergencyPriority.CRITICAL;
        }
        if (emergency.isSevereBleeding()) {
            return EmergencyPriority.CRITICAL;
        }

        // 2. High Hazard Incidents & Structural Trapping -> CRITICAL
        if (emergency.isTrapped()) {
            return EmergencyPriority.CRITICAL;
        }
        if (emergency.getType() == EmergencyType.BUILDING_COLLAPSE) {
            return EmergencyPriority.CRITICAL;
        }
        if (emergency.getType() == EmergencyType.FIRE && (emergency.getInjuredCount() > 0 || emergency.isFireInvolved())) {
            return EmergencyPriority.CRITICAL;
        }

        // 3. Mass Casualty Thresholds
        if (emergency.getPeopleAffected() >= 5 || emergency.getInjuredCount() >= 3) {
            return EmergencyPriority.CRITICAL;
        }
        if (emergency.getPeopleAffected() >= 2 || emergency.getInjuredCount() >= 1) {
            return EmergencyPriority.HIGH;
        }

        // 4. Category-Specific Baseline Priorities
        if (emergency.getType() == EmergencyType.NATURAL_DISASTER || emergency.getType() == EmergencyType.FLOOD) {
            return EmergencyPriority.HIGH;
        }
        if (emergency.getType() == EmergencyType.ACCIDENT || emergency.getType() == EmergencyType.FIRE) {
            return EmergencyPriority.HIGH;
        }
        if (emergency.getType() == EmergencyType.BLOOD_EMERGENCY) {
            return emergency.getInjuredCount() > 0 ? EmergencyPriority.HIGH : EmergencyPriority.MEDIUM;
        }
        if (emergency.getType() == EmergencyType.MEDICAL) {
            return EmergencyPriority.HIGH;
        }
        if (emergency.getType() == EmergencyType.POLICE || emergency.getType() == EmergencyType.RESCUE) {
            return EmergencyPriority.MEDIUM;
        }

        // 5. Default Non-Urgent
        return EmergencyPriority.LOW;
    }
}
