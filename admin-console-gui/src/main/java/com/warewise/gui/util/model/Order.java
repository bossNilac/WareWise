package com.warewise.gui.util.model;


import com.warewise.gui.util.enums.OrderStatus;
import com.google.gson.annotations.SerializedName;

public class Order {
    private int ID;
    @SerializedName("general_item_id")
    private int generalItemId;
    private int quantity;
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

    public Order() {
    }

    public int getGeneralItemId() {
        return generalItemId;
    }

    public void setGeneralItemId(int generalItemId) {
        this.generalItemId = generalItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public Order(int ID, int generalItemId, int quantity, OrderStatus status,
                 String createdAt, String updatedAt,int  userId) {
        this.ID = ID;
        this.generalItemId = generalItemId;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    public Order(int generalItemId, int quantity, OrderStatus status,
                 String createdAt, String updatedAt,int userId) {
        this.generalItemId = generalItemId;
        this.quantity = quantity;
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
