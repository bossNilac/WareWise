package com.warewise.common.model;

import com.warewise.common.util.SequenceManager;
import com.warewise.common.util.enums.OrderStatus;
import com.warewise.common.util.enums.TableName;

import java.util.List;

public class Order {
    private int ID;
    private String customerName;
    private String customerEmail;
    private OrderStatus status;
    private String createdAt;
    private String updatedAt;

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
                 String createdAt, String updatedAt) {
        this.ID = ID;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Order(String customerName, String customerEmail, OrderStatus status,
                 String createdAt, String updatedAt) {
        this.setID(SequenceManager.getInstance().getNextId(TableName.ORDERS.getTableName()));
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.status = status;
        this.updatedAt = updatedAt;
    }



    public String toString(){
        String output = "";
        output = output + ID +",";
        output = output + customerName+",";
        output = output + customerEmail+",";
        output = output + status+",";
        output = output + createdAt+",";
        output = output + updatedAt+",";
        return output;
    }

}
