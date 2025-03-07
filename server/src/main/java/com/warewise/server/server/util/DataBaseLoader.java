package com.warewise.server.server.util;

import com.warewise.common.model.*;
import com.warewise.common.util.enums.OrderStatus;
import com.warewise.common.util.enums.StockAlertStatus;
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

    public boolean updateOrder(Order order, String customerName, String customerEmail, OrderStatus status, String updatedAt) {
        if (order.getCustomerName().equals(customerName) &&
                order.getCustomerEmail().equals(customerEmail) &&
                order.getStatus() == status &&
                order.getUpdatedAt().equals(updatedAt)) {
            return false; // No changes detected
        }

        if (!customerName.isBlank() || !customerEmail.isBlank() || status != order.getStatus() || !updatedAt.isBlank()) {
            if (!customerName.isBlank()) {
                order.setCustomerName(customerName);
            }
            if (!customerEmail.isBlank()) {
                order.setCustomerEmail(customerEmail);
            }
            if (status != order.getStatus()) {
                order.setStatus(status);
            }
            if (!updatedAt.isBlank()) {
                order.setUpdatedAt(updatedAt);
            }

            orderHandler.updateOrder(order); // Save changes to DB
            return true;
        } else {
            return false;
        }
    }

    public void addOrder(Order order) {
        server.getOrders().add(order);
        orderHandler.addOrder(order);
    }

    public void deleteOrder(Order order) {
        server.getOrders().remove(order);
        orderHandler.deleteOrder(order);
    }

    public boolean updateStockAlert(StockAlert stockAlert, StockAlertStatus threshold, String resolved) {
        if (stockAlert.getThreshold() == threshold &&
                stockAlert.getResolved().equals(resolved)) {
            return false;
        }

        if (threshold != stockAlert.getThreshold() || !resolved.isBlank()) {
            if (threshold != stockAlert.getThreshold()) {
                stockAlert.setThreshold(threshold);
            }
            if (!resolved.isBlank()) {
                stockAlert.setResolved(resolved);
            }

            stockAlertHandler.updateStockAlert(stockAlert);
            return true;
        } else {
            return false;
        }
    }

    public void addStockAlert(StockAlert stockAlert) {
        server.getStockAlertList().add(stockAlert);
        stockAlertHandler.addStockAlert(stockAlert);
    }

    public void deleteStockAlert(StockAlert stockAlert) {
        server.getStockAlertList().remove(stockAlert);
        stockAlertHandler.deleteStockAlert(stockAlert);
    }

    public boolean updateSupplier(Supplier supplier, String name, String contactEmail, String contactPhoneNo, String address) {
        if (supplier.getName().equals(name) &&
                supplier.getContactEmail().equals(contactEmail) &&
                supplier.getContactPhoneNo().equals(contactPhoneNo) &&
                supplier.getAddress().equals(address)) {
            return false;
        }

        if (!name.isBlank() || !contactEmail.isBlank() || !contactPhoneNo.isBlank() || !address.isBlank()) {
            if (!name.isBlank()) {
                supplier.setName(name);
            }
            if (!contactEmail.isBlank()) {
                supplier.setContactEmail(contactEmail);
            }
            if (!contactPhoneNo.isBlank()) {
                supplier.setContactPhoneNo(contactPhoneNo);
            }
            if (!address.isBlank()) {
                supplier.setAddress(address);
            }

            supplierHandler.updateSupplier(supplier);
            return true;
        } else {
            return false;
        }
    }

    public boolean updateStockAlert(StockAlert stockAlert, int productID, StockAlertStatus threshold, String createdAt, String resolved) {
        if (stockAlert.getProductID() == productID &&
                stockAlert.getThreshold() == threshold &&
                stockAlert.getCreatedAt().equals(createdAt) &&
                stockAlert.getResolved().equals(resolved)) {
            return false;
        }

        if (productID >= 0 || threshold != stockAlert.getThreshold() || !createdAt.isBlank() || !resolved.isBlank()) {
            if (productID >= 0) {
                stockAlert.setProductID(productID);
            }
            if (threshold != stockAlert.getThreshold()) {
                stockAlert.setThreshold(threshold);
            }
            if (!createdAt.isBlank()) {
                stockAlert.setCreatedAt(createdAt);
            }
            if (!resolved.isBlank()) {
                stockAlert.setResolved(resolved);
            }

            stockAlertHandler.updateStockAlert(stockAlert);
            return true;
        } else {
            return false;
        }
    }


    public void addSupplier(Supplier supplier) {
        server.getSuppliers().add(supplier);
        supplierHandler.addSupplier(supplier);
    }

    public void deleteSupplier(Supplier supplier) {
        server.getSuppliers().remove(supplier);
        supplierHandler.deleteSupplier(supplier);
    }





}
