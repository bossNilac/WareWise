package com.warewise.gui.util.model;


import com.warewise.gui.util.enums.UserRole;

public class User {
    private int ID;
    private String username;
    private String passwordHash;
    private UserRole role;
    private String email;
    private String createdAt;
    private int warehouseId;

    public User(String createdAt, String email, UserRole role, String passwordHash, String username,int warehouseId) {
        this.createdAt = createdAt;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.username = username;
        this.warehouseId = warehouseId;
    }

    public User(int ID,String createdAt, String email, UserRole role, String passwordHash, String username,int warehouseId) {
        this.ID = ID;
        this.createdAt = createdAt;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.username = username;
        this.warehouseId = warehouseId;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }
}
