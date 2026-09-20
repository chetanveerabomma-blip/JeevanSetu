package com.jeevansetu.algorithm;

import com.jeevansetu.model.Emergency;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe Priority Queue for active emergencies.
 * Used by the Command Center to ensure CRITICAL incidents are always addressed first.
 */
@Component
public class EmergencyPriorityQueue {

    private final PriorityQueue<Emergency> queue;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public EmergencyPriorityQueue() {
        this.queue = new PriorityQueue<>(100, new EmergencyPriorityComparator());
    }

    public void add(Emergency emergency) {
        rwLock.writeLock().lock();
        try {
            // Remove existing version if any, then re-insert to preserve heap order
            queue.removeIf(e -> e.getId() != null && e.getId().equals(emergency.getId()));
            queue.offer(emergency);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void addAll(Collection<Emergency> emergencies) {
        rwLock.writeLock().lock();
        try {
            for (Emergency e : emergencies) {
                queue.removeIf(existing -> existing.getId() != null && existing.getId().equals(e.getId()));
                queue.offer(e);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public Emergency poll() {
        rwLock.writeLock().lock();
        try {
            return queue.poll();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public Emergency peek() {
        rwLock.readLock().lock();
        try {
            return queue.peek();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public boolean remove(String emergencyId) {
        rwLock.writeLock().lock();
        try {
            return queue.removeIf(e -> e.getId() != null && e.getId().equals(emergencyId));
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public List<Emergency> getSortedList() {
        rwLock.readLock().lock();
        try {
            // Copy elements and sort them according to comparator for viewing
            PriorityQueue<Emergency> copy = new PriorityQueue<>(queue);
            List<Emergency> sorted = new ArrayList<>();
            while (!copy.isEmpty()) {
                sorted.add(copy.poll());
            }
            return sorted;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public int size() {
        rwLock.readLock().lock();
        try {
            return queue.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void clear() {
        rwLock.writeLock().lock();
        try {
            queue.clear();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
