package com.warewise.gui.util.enums;

public enum UserRole {
    ADMIN,
    MANAGER,
    WORKER;

    public static UserRole fromLabel(String label) {
        if (label == null) {
            throw new IllegalArgumentException("Label must not be null");
        }
        switch (label.trim().toUpperCase()) {
            case "ADMIN":
                return ADMIN;
            case "MANAGER":
                return MANAGER;
            case "WORKER":
                return WORKER;
            default:
                throw new IllegalArgumentException("Unknown user role label: " + label);
        }
    }

    @Override
    public String toString() {
        return this.name();
    }
}
