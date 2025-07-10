package warewise.server.common.util.enums;

public enum StockAlertStatus {
    ACTIVE,
    RESOLVED;

    public static StockAlertStatus fromLabel(String label) {
        if (label == null) {
            throw new IllegalArgumentException("StockAlertStatus label cannot be null");
        }
        switch (label.toUpperCase()) {
            case "ACTIVE":
                return ACTIVE;
            case "RESOLVED":
                return RESOLVED;
            default:
                throw new IllegalArgumentException("Unknown StockAlertStatus: " + label);
        }
    }

    @Override
    public String toString() {
        return this.name();
    }
}
