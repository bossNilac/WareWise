package warewise.server.common.model;

import warewise.server.common.util.enums.UserRole;

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

    public User(String createdAt, String email, UserRole role, String passwordHash, String username, int warehouseId) {
        this(createdAt, email, role, passwordHash, username, List.of(warehouseId));
    }

    public User(String createdAt, String email, UserRole role, String passwordHash, String username, List<Integer> warehouseIds) {
        this.createdAt = createdAt;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.username = username;
        setWarehouseIds(warehouseIds);
    }

    public User(int ID, String createdAt, String email, UserRole role, String passwordHash, String username, int warehouseId) {
        this(ID, createdAt, email, role, passwordHash, username, List.of(warehouseId));
    }

    public User(int ID, String createdAt, String email, UserRole role, String passwordHash, String username, List<Integer> warehouseIds) {
        this.ID = ID;
        this.createdAt = createdAt;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.username = username;
        setWarehouseIds(warehouseIds);
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
}
