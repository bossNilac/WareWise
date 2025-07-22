package com.warewise.client.util.model;


import com.warewise.client.util.enums.OrderStatus;

public class Order {
    private int ID;
    private Integer general_item_id;
    private OrderStatus status;
    private String createdAt;
    private String updatedAt;
    private int userId;
    private int quantity;



    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Integer getGeneralItemId() {
        return general_item_id;
    }

    public void setGeneralItemId(Integer customerName) {
        this.general_item_id = customerName;
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

    public Order(int ID, int general_item_id,int quantity, OrderStatus status,
                 String createdAt, String updatedAt,int  userId) {
        this.ID = ID;
        this.general_item_id = general_item_id;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.quantity = quantity;
    }

    public Order(int general_item_id,int quantity, OrderStatus status,
                 String createdAt, String updatedAt,int userId) {
        this.general_item_id = general_item_id;
        this.status = status;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.userId = userId;
        this.quantity = quantity;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
