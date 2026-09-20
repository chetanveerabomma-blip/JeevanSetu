package com.jeevansetu.model;

public enum EmergencyPriority {
    CRITICAL(4),
    HIGH(3),
    MEDIUM(2),
    LOW(1);

    private final int level;

    EmergencyPriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
