package com.warewise.gui.util.model;


import com.warewise.gui.util.enums.OrderStatus;

public class Order {
    private int ID;
    private String customerName;
    private String customerEmail;
    private OrderStatus status;
    private String createdAt;
    private String updatedAt;
    private int userId;



    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Order(int ID, String customerName, String customerEmail, OrderStatus status,
                 String createdAt, String updatedAt,int  userId) {
        this.ID = ID;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    public Order(String customerName, String customerEmail, OrderStatus status,
                 String createdAt, String updatedAt,int userId) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.status = status;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
