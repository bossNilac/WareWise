package com.warewise.server.server.util;

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
        server.setInventoryData(inventoryHandler.loadInventory());
        server.setItemData(itemHandler.loadItems(null));
//        server.setLogsData(logsHandler.loadLogs());
        server.setOrderData(orderHandler.loadOrders());
        server.setStockData(stockAlertHandler.loadStockAlerts());
        server.setSupplierData(supplierHandler.loadSuppliers());
    }

    public void addUser(User user){
        userHandler.addUser(user);
    }

    public void deleteUser(User user){
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

}
