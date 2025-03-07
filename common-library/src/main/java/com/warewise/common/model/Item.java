package com.warewise.common.model;

import com.warewise.common.util.SequenceManager;
import com.warewise.common.util.enums.TableName;

public class Item {
    private int ID;
    private int orderID;
    private int inventoryID;
    private int quantity;
    private double price;
    private double total;
    private Category category;
    private int categoryID;


    public Item(int ID, int orderID, int inventoryID, int quantity, double price, Category category) {
        this.ID = ID;
        this.orderID = orderID;
        this.inventoryID = inventoryID;
        this.quantity = quantity;
        this.price = price;
        this.total = price*quantity;
        this.category = category;
    }

    public Item(int ID, int orderID, int inventoryID, int quantity, double price, int categoryID) {
        this.ID = ID;
        this.orderID = orderID;
        this.inventoryID = inventoryID;
        this.quantity = quantity;
        this.price = price;
        this.total = price*quantity;
        this.categoryID = categoryID;
    }


    public Item(int orderID, int inventoryID, int quantity, double price, Category category) {
        this.setID(SequenceManager.getInstance().getNextId(TableName.ORDER_ITEMS.getTableName()));
        this.orderID = orderID;
        this.inventoryID = inventoryID;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String toString(){
        String output = "";
        output = output + ID +",";
        output = output + orderID+",";
        output = output + inventoryID +",";
        output = output + quantity+",";
        output = output + price+",";
        output = output + total+",";
        output = output + category;
        return output;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
}
