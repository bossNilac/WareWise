package com.warewise.gui.util.model;


public class Log {
    private int ID;
    private String username;
    private String action;
    private String description;
    private String createdAt;

    public Log() {
    }

    public Log(int ID, String username, String action, String description, String createdAt) {
        this.ID = ID;
        this.username = username;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
    }
    public Log(String username, String action, String description, String createdAt) {
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}
