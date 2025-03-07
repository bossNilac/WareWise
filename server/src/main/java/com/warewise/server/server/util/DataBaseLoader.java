package com.warewise.server.server.util;

import com.warewise.common.model.Category;
import com.warewise.common.model.Inventory;
import com.warewise.common.model.Item;
import com.warewise.common.model.User;
import com.warewise.common.util.enums.UserRole;
import com.warewise.server.server.Server;
import com.warewise.common.handler.*;

public class DataBaseLoader {
    private Server server ;

    private final CategoryHandler categoryHandler = new CategoryHandler();
    private final InventoryHandler inventoryHandler = new InventoryHandler();
    private final ItemHandler itemHandler = new ItemHandler();
    private final LogsHandler logsHandler = new LogsHandler();
    private final OrderHandler orderHandler = new OrderHandler();
    private final StockAlertHandler stockAlertHandler = new StockAlertHandler();
    private final SupplierHandler supplierHandler = new SupplierHandler();
    private final UserHandler userHandler = new UserHandler();


    public DataBaseLoader(Server server){
        this.server = server;
    }


    public void loadDataFromDB(){
        server.setUserData(userHandler.loadUsers());
        server.setCategoryData(categoryHandler.loadCategories());
        server.setItemData(itemHandler.loadItems(null));
        server.setInventoryData(inventoryHandler.loadInventory());
//        server.setLogsData(logsHandler.loadLogs());
        server.setOrderData(orderHandler.loadOrders());
        server.setStockData(stockAlertHandler.loadStockAlerts());
        server.setSupplierData(supplierHandler.loadSuppliers());
    }

    public void addUser(User user){
        userHandler.addUser(user);
        server.getUsers().add(user);
    }

    public void deleteUser(User user){
        server.getUsers().remove(user);
        userHandler.deleteUser(user);
    }

    public boolean updateUser(User user,String role,String email,String password){
        if (user.getRole().equals(UserRole.valueOf(role))
                && user.getEmail().equals(email) && user.getPasswordHash().equals(password)){
            return false;
        }
        if (!role.isBlank() || !email.isBlank() || !password.isBlank()) {
            if (!role.isBlank()) {
                user.setRole(UserRole.valueOf(role));
            }
            if (!email.isBlank()) {
                user.setEmail(email);
            }
            if (!password.isBlank()) {
                user.setPasswordHash(password);
            }
            userHandler.updateUser(user);
            return true;
        }else {
            return false;
        }
    }

    public boolean updateCategory(Category category, String name, String description) {
        if (category.getName().equals(name) && category.getDescription().equals(description)) {
            return false; // No changes detected
        }

        if (!name.isBlank() || !description.isBlank()) {
            if (!name.isBlank()) {
                category.setName(name);
            }
            if (!description.isBlank()) {
                category.setDescription(description);
            }
            categoryHandler.updateCategory(category); // Save changes to DB
            return true;
        } else {
            return false;
        }
    }

    public void addCategory(Category category) {
        server.getCategories().add(category);
        categoryHandler.addCategory(category);
    }

    public void deleteCategory(Category category){
        server.getCategories().remove(category);
        categoryHandler.deleteCategory(category);
    }

    public boolean updateItem(Item item, int orderID, int inventoryID, int quantity, double price, Category category) {
        if (item.getOrderID() == orderID &&
                item.getInventoryID() == inventoryID &&
                item.getQuantity() == quantity &&
                item.getPrice() == price &&
                item.getCategory() == category) {
            return false; // No changes detected
        }

        if (orderID >= 0 || inventoryID >= 0 || quantity >= 0 || price >= 0 || category != item.getCategory()) {
            if (orderID >= 0) {
                item.setOrderID(orderID);
            }
            if (inventoryID >= 0) {
                item.setInventoryID(inventoryID);
            }
            if (quantity >= 0) {
                item.setQuantity(quantity);
            }
            if (price >= 0) {
                item.setPrice(price);
            }
            if (item.getCategory()!= category) {
                item.setCategory(category);
                item.setCategoryID(category.getID());
            }
            itemHandler.updateItem(item); // Save changes to DB
            return true;
        } else {
            return false;
        }
    }

    public void addItem(Item item) {
        server.getItems().add(item);
        itemHandler.addItem(item);
    }

    public void deleteItem(Item item) {
        server.getItems().remove(item);
        itemHandler.deleteItem(item);
    }

    public boolean updateInventory(Inventory inventory, String name, String description, int quantity, String lastUpdated) {
        if (inventory.getName().equals(name) &&
                inventory.getDescription().equals(description) &&
                inventory.getQuantity() == quantity &&
                inventory.getLastUpdated().equals(lastUpdated)) {
            return false; // No changes detected
        }

        if (!name.isBlank() || !description.isBlank() || quantity >= 0 || !lastUpdated.isBlank()) {
            if (!name.isBlank()) {
                inventory.setName(name);
            }
            if (!description.isBlank()) {
                inventory.setDescription(description);
            }
            if (quantity >= 0) {
                inventory.setQuantity(quantity);
            }
            if (!lastUpdated.isBlank()) {
                inventory.setLastUpdated(lastUpdated);
            }

            inventoryHandler.updateInventory(inventory); // Save changes to DB
            return true;
        } else {
            return false;
        }
    }

    public void addInventory(Inventory inventory) {
        server.getInventories().add(inventory);
        inventoryHandler.addInventory(inventory);
    }

    public void deleteInventory(Inventory inventory) {
        server.getInventories().remove(inventory);
        inventoryHandler.deleteInventory(inventory);
    }



}
