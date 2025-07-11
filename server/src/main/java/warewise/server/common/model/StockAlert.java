package warewise.server.common.model;


public class StockAlert {
    private int ID;
    private int productID;
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

    public StockAlert(int ID, int productID, String createdAt,
                      boolean resolved) {
        this.ID = ID;
        this.productID = productID;
        this.createdAt = createdAt;
        this.resolved = resolved;
    }

    public StockAlert(int productID, String createdAt,
                      boolean resolved) {
        this.productID = productID;
        this.createdAt = createdAt;
        this.resolved = resolved;
    }
}
