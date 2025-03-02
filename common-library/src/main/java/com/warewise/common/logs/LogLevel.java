package com.warewise.common.logs;

public enum LogLevel {
    TRACE(1),
    INFO(2),
    WARN(3),
    ERROR(4),
    FATAL(5);

    private final int severity;

    LogLevel(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {
        return severity;
    }

    /**
     * Returns true if the current log level is higher or equal to the provided level.
     */
    public boolean isLoggable(LogLevel threshold) {
        return this.severity >= threshold.severity;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
