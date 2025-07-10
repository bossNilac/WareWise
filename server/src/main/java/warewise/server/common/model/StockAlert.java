package warewise.server.common.model;


import warewise.server.common.util.enums.StockAlertStatus;

public class StockAlert {
    private int ID;
    private int productID;
    private StockAlertStatus threshold;
    private String createdAt;
    private boolean resolved;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public StockAlertStatus getThreshold() {
        return threshold;
    }

    public void setThreshold(StockAlertStatus threshold) {
        this.threshold = threshold;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean getResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public StockAlert(int ID, int productID, StockAlertStatus threshold, String createdAt,
                      boolean resolved) {
        this.ID = ID;
        this.productID = productID;
        this.threshold = threshold;
        this.createdAt = createdAt;
        this.resolved = resolved;
    }

    public StockAlert(int productID, StockAlertStatus threshold, String createdAt,
                      boolean resolved) {
        this.productID = productID;
        this.threshold = threshold;
        this.createdAt = createdAt;
        this.resolved = resolved;
    }
}
