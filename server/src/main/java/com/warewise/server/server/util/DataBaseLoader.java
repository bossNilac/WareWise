package com.warewise.server.server.util;

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
}
