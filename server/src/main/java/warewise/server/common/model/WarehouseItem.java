package warewise.server.common.model;

public class WarehouseItem {
    private int ID;
    private int orderID;
    private int inventoryID;
    private int quantity;
    private int generalItemId;
    private String expireDate;
    private boolean sold;



    public WarehouseItem(int ID, int orderID, int inventoryID, int quantity,int generalItemId,String expireDate,boolean sold) {
        this.ID = ID;
        this.orderID = orderID;
        this.inventoryID = inventoryID;
        this.quantity = quantity;
        this.generalItemId = generalItemId;
        this.expireDate = expireDate;
        this.sold = sold;
    }


    public WarehouseItem(int orderID, int inventoryID, int quantity,int generalItemId,String expireDate,boolean sold) {
        this.orderID = orderID;
        this.inventoryID = inventoryID;
        this.quantity = quantity;
        this.generalItemId = generalItemId;
        this.expireDate = expireDate;
        this.sold = sold;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getGeneralItemId() {
        return generalItemId;
    }

    public void setGeneralItemId(int generalItemId) {
        this.generalItemId = generalItemId;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
