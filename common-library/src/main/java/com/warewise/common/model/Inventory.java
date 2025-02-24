package com.warewise.common.model;

import com.warewise.common.util.database.SequenceManager;
import com.warewise.common.util.enums.TableName;

import java.util.List;

public class Inventory {
    private int ID;
    private String name;
    private String description;
    private List<Item> items ;
    private int quantity;
    private double price;
    private int supplyID;
    private String lastUpdated;

    public Inventory(int ID, String name, String description ,List<Item> items, int quantity, double price, int supplyID,
                     String lastUpdated) {
        this.ID = ID;
        this.description = description;
        this.name = name;
        this.items = items;
        this.quantity = quantity;
        this.price = price;
        this.supplyID = supplyID;
        this.lastUpdated = lastUpdated;
    }

    public Inventory(String name, String description , List<Item> items, int quantity, double price, int supplyID,
                     String lastUpdated) {
        this.setID(SequenceManager.getInstance().getNextId(TableName.INVENTORY.getTableName()));
        this.description = description;
        this.name = name;
        this.items = items;
        this.quantity = quantity;
        this.price = price;
        this.supplyID = supplyID;
        this.lastUpdated = lastUpdated;
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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
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

    public int getSupplyID() {
        return supplyID;
    }

    public void setSupplyID(int supplyID) {
        this.supplyID = supplyID;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String toString(){
        String output = "";
        output = output + ID +",";
        output = output + name+",";
        output = output + description+",";
        output = output + items.toString()+",";
        output = output + quantity+",";
        output = output + price +",";
        output = output + supplyID+",";
        output = output + lastUpdated;
        return output;
    }

}
