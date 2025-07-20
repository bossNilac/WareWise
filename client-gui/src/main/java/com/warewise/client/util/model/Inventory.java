package com.warewise.client.util.model;


public class Inventory {
    private int ID;
    private String name;
    private String description;
    private int quantity;
    private String lastUpdated;
    private int warehouseId;

    public Inventory(int ID, String name, String description , int quantity,
                     String lastUpdated,int warehouseId) {
        this.ID = ID;
        this.description = description;
        this.name = name;
        this.quantity = quantity;
        this.lastUpdated = lastUpdated;
        this.warehouseId = warehouseId;
    }

    public Inventory(String name, String description , int quantity,
                     String lastUpdated,int warehouseId) {
        this.description = description;
        this.name = name;
        this.quantity = quantity;
        this.lastUpdated = lastUpdated;
        this.warehouseId = warehouseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }
}
