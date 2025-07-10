package warewise.server.common.model;

public class Item {
    private int ID;
    private int orderID;
    private int inventoryID;
    private int quantity;
    private double price;
    private double total;
    private int categoryID;
    private int supplierId;




    public Item(int ID, int orderID, int inventoryID,double price, int quantity, double total,  int categoryID,int supplierId) {
        this.ID = ID;
        this.orderID = orderID;
        this.inventoryID = inventoryID;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.categoryID = categoryID;
        this.supplierId = supplierId;
    }


    public Item(int orderID, int inventoryID, int quantity, double price, int category,int supplierId) {
        this.orderID = orderID;
        this.inventoryID = inventoryID;
        this.quantity = quantity;
        this.price = price;
        this.categoryID = category;
        this.total = price * (double)quantity;
        this.supplierId = supplierId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
}
