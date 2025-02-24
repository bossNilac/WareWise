package com.warewise.common.model;

import com.warewise.common.util.database.SequenceManager;
import com.warewise.common.util.enums.TableName;

public class Logs {
    private int ID;
    private int userID;
    private String action;
    private String description;
    private String createdAt;

    public Logs(int ID, int userID, String action, String description, String createdAt) {
        this.ID = ID;
        this.userID = userID;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
    }
    public Logs(int userID, String action, String description, String createdAt) {
        this.setID(SequenceManager.getInstance().getNextId(TableName.LOGS.getTableName()));
        this.userID = userID;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String toString(){
        String output = "";
        output = output + ID +",";
        output = output + userID+",";
        output = output + description+",";
        output = output + createdAt+",";
        return output;
    }
}
