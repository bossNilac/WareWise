package com.warewise.gui.util.enums;

public enum OrderStatus {
    PENDING,
    FULFILLED,
    CANCELLED;

    public static OrderStatus fromLabel(String label) {
        if (label == null) {
            throw new IllegalArgumentException("OrderStatus label cannot be null");
        }
        switch (label.toUpperCase()) {
            case "PENDING":
                return PENDING;
            case "FULFILLED":
                return FULFILLED;
            case "CANCELLED":
                return CANCELLED;
            default:
                throw new IllegalArgumentException("Unknown OrderStatus: " + label);
        }
    }

    @Override
    public String toString() {
        return this.name();
    }

}
