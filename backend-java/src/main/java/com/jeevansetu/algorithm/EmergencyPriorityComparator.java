package com.jeevansetu.algorithm;

import com.jeevansetu.model.Emergency;

import java.util.Comparator;

/**
 * Comparator for Emergency Priority Queue.
 * Orders emergencies by:
 * 1. Priority level (CRITICAL > HIGH > MEDIUM > LOW)
 * 2. Unconscious flag (true > false)
 * 3. Injured count (descending)
 * 4. Creation time (FIFO - earlier reported incidents first)
 */
public class EmergencyPriorityComparator implements Comparator<Emergency> {

    @Override
    public int compare(Emergency e1, Emergency e2) {
        if (e1 == null && e2 == null) return 0;
        if (e1 == null) return 1;
        if (e2 == null) return -1;

        // 1. Compare Priority level (higher level integer first -> descending)
        int p1 = e1.getPriority() != null ? e1.getPriority().getLevel() : 0;
        int p2 = e2.getPriority() != null ? e2.getPriority().getLevel() : 0;
        if (p1 != p2) {
            return Integer.compare(p2, p1);
        }

        // 2. Unconscious tie-breaker
        if (e1.isUnconscious() != e2.isUnconscious()) {
            return e1.isUnconscious() ? -1 : 1;
        }

        // 3. Injured count tie-breaker
        if (e1.getInjuredCount() != e2.getInjuredCount()) {
            return Integer.compare(e2.getInjuredCount(), e1.getInjuredCount());
        }

        // 4. FIFO by creation time
        if (e1.getCreatedAt() != null && e2.getCreatedAt() != null) {
            return e1.getCreatedAt().compareTo(e2.getCreatedAt());
        }

        return 0;
    }
}
