package com.warewise.common.model;

public class Category {
    private int ID;
    private String name;
    private String description;
    private String createdAt;

    public Category(int ID, String name, String description, String  createdAt) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
