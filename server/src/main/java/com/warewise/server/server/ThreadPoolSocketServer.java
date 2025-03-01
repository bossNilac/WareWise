package com.warewise.server.server;

import com.warewise.common.model.*;
import com.warewise.server.database.handler.*;
import com.warewise.server.server.networking.SocketConnection;
import com.warewise.server.server.networking.SocketServer;
import com.warewise.server.service.ServiceHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolSocketServer extends SocketServer {

    private final ExecutorService executor;

    private List<User> userList;
    private List<Category> categoryList;
    private List<Inventory> inventoryList;
    private List<Item> itemList;
    private List<Logs> logsList;
    private List<Order> orderList;
    private List<StockAlert> stockAlertList;
    private List<Supplier> supplierList;



    // Fields for all handlers and utilities
    private final CategoryHandler categoryHandler = new CategoryHandler();
    private final InventoryHandler inventoryHandler = new InventoryHandler();
    private final ItemHandler itemHandler = new ItemHandler();
    private final LogsHandler logsHandler = new LogsHandler();
    private final OrderHandler orderHandler = new OrderHandler();
    private final StockAlertHandler stockAlertHandler = new StockAlertHandler();
    private final SupplierHandler supplierHandler = new SupplierHandler();
    private final UserHandler userHandler = new UserHandler();


    /**
     * Constructs a new ThreadPoolSocketServer that listens on the given port using a fixed thread pool.
     * @param port the port on which to listen for connections.
     * @param poolSize the number of threads in the pool.
     * @throws IOException if an I/O error occurs when opening the socket.
     */
    public ThreadPoolSocketServer(int port, int poolSize) throws IOException {
        super(port);
        this.loadData();
        this.executor = Executors.newFixedThreadPool(poolSize);
    }


    public static void main(String[] args) {
        try {
            ThreadPoolSocketServer server = new ThreadPoolSocketServer(12345,10);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * For each accepted connection, create a new ClientHandler and ServerConnection,
     * and submit the connection to the thread pool.
     */
    @Override
    protected void handleConnection(Socket socket) {
        try {
            ServerConnection connection = new ServerConnection(socket,this);
            executor.submit(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, SocketConnection connection){
        connection.sendMessage(message);
    }


    private void loadData(){
        userList = userHandler.loadUsers();
        categoryList= categoryHandler.loadCategories();
        inventoryList = inventoryHandler.loadInventory();
        itemList = itemHandler.loadItems(null);
        logsList = logsHandler.loadLogs();
        orderList = orderHandler.loadOrders();
        stockAlertList = stockAlertHandler.loadStockAlerts();
        supplierList = supplierHandler.loadSuppliers();
    }

    /**
     * Shut down the server socket and the thread pool.
     */
    public void shutdown() {
        close();
        executor.shutdown();
    }

    public List<User> getUserList() {
        return userList;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public List<Inventory> getInventoryList() {
        return inventoryList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public List<Logs> getLogsList() {
        return logsList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public List<StockAlert> getStockAlertList() {
        return stockAlertList;
    }

    public List<Supplier> getSupplierList() {
        return supplierList;
    }

    public CategoryHandler getCategoryHandler() {
        return categoryHandler;
    }

    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    public LogsHandler getLogsHandler() {
        return logsHandler;
    }

    public OrderHandler getOrderHandler() {
        return orderHandler;
    }

    public StockAlertHandler getStockAlertHandler() {
        return stockAlertHandler;
    }

    public SupplierHandler getSupplierHandler() {
        return supplierHandler;
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

}
