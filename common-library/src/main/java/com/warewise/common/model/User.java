package com.warewise.common.model;

import com.warewise.common.util.database.SequenceManager;
import com.warewise.common.util.enums.TableName;
import com.warewise.common.util.enums.UserRole;

public class User {
    private int ID;
    private String username;
    private String passwordHash;
    private UserRole role;
    private String email;
    private String createdAt;

    public User(String createdAt, String email, UserRole role, String passwordHash, String username) {
        this.createdAt = createdAt;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.username = username;
        this.setID(SequenceManager.getInstance().getNextId(TableName.USERS.getTableName()));
    }

    public User(int ID,String createdAt, String email, UserRole role, String passwordHash, String username) {
        this.ID = ID;
        this.createdAt = createdAt;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.username = username;
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

    public String toString(){
        String output = "";
        output = output + ID +",";
        output = output + username+",";
        output = output + passwordHash+",";
        output = output + role+",";
        output = output + email+",";
        output = output + createdAt;
        return output;
    }
}
