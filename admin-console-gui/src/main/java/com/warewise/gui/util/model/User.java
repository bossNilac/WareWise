package com.warewise.gui.util.model;


import com.warewise.gui.util.enums.UserRole;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int ID;
    private String username;
    private String passwordHash;
    private UserRole role;
    private String email;
    private String createdAt;
    private List<Integer> warehouseIds = new ArrayList<>();
    private String warehouseIdsText = "";

    public User() {
    }

    public User(String createdAt, String email, UserRole role, String passwordHash, String username,int warehouseId) {
        this.createdAt = createdAt;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.username = username;
        setWarehouseId(warehouseId);
    }

    public User(int ID,String createdAt, String email, UserRole role, String passwordHash, String username,int warehouseId) {
        this.ID = ID;
        this.createdAt = createdAt;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.username = username;
        setWarehouseId(warehouseId);
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
        return warehouseIds.isEmpty() ? 0 : warehouseIds.get(0);
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseIds = new ArrayList<>();
        if (warehouseId > 0) {
            this.warehouseIds.add(warehouseId);
        }
    }

    public List<Integer> getWarehouseIds() {
        return warehouseIds;
    }

    public void setWarehouseIds(List<Integer> warehouseIds) {
        this.warehouseIds = new ArrayList<>();
        if (warehouseIds == null) {
            return;
        }
        for (Integer warehouseId : warehouseIds) {
            if (warehouseId != null && warehouseId > 0 && !this.warehouseIds.contains(warehouseId)) {
                this.warehouseIds.add(warehouseId);
            }
        }
    }

    public String getWarehouseIdsText() {
        return warehouseIds.toString().replace("[", "").replace("]", "").replace(" ", "");
    }

    public void setWarehouseIdsText(String warehouseIdsText) {
        List<Integer> parsed = new ArrayList<>();
        if (warehouseIdsText != null && !warehouseIdsText.isBlank()) {
            for (String token : warehouseIdsText.split(",")) {
                String trimmed = token.trim();
                if (!trimmed.isEmpty()) {
                    parsed.add(Integer.parseInt(trimmed));
                }
            }
        }
        setWarehouseIds(parsed);
    }
}
