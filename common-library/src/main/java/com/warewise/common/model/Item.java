package com.warewise.common.model;

public class Item {
    private int ID;
    private int orderID;
    private int productID;
    private int quantity;
    private double price;
    private double total;
    private Category category;


    public Item(int ID, int orderID, int productID, int quantity, double price, double total, Category category) {
        this.ID = ID;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
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

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
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

}
