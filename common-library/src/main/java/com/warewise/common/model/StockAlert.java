package com.warewise.common.model;

import com.warewise.common.util.SequenceManager;
import com.warewise.common.util.enums.StockAlertStatus;
import com.warewise.common.util.enums.TableName;
import com.warewise.common.util.protocol.Protocol;

public class StockAlert {
    private int ID;
    private int productID;
    private StockAlertStatus threshold;
    private String createdAt;
    private String resolved;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public StockAlertStatus getThreshold() {
        return threshold;
    }

    public void setThreshold(StockAlertStatus threshold) {
        this.threshold = threshold;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getResolved() {
        return resolved;
    }

    public void setResolved(String resolved) {
        this.resolved = resolved;
    }

    public StockAlert(int ID, int productID, StockAlertStatus threshold, String createdAt,
                      String resolved) {
        this.ID = ID;
        this.productID = productID;
        this.threshold = threshold;
        this.createdAt = createdAt;
        this.resolved = resolved;
    }

    public StockAlert(int productID, StockAlertStatus threshold, String createdAt,
                      String resolved) {
        this.setID(SequenceManager.getInstance().getNextId(TableName.STOCK_ALERTS.getTableName()));
        this.productID = productID;
        this.threshold = threshold;
        this.createdAt = createdAt;
        this.resolved = resolved;
    }

    public String toString(){
        String output = "";
        output = output + ID + Protocol.ARG_SEPARATOR;
        output = output + productID+Protocol.ARG_SEPARATOR;
        output = output + threshold+Protocol.ARG_SEPARATOR;
        output = output + createdAt+Protocol.ARG_SEPARATOR;
        output = output + resolved+Protocol.ARG_SEPARATOR;
        return output;
    }
}
