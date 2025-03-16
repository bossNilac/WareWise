package com.warewise.server.server;

import com.warewise.common.model.*;
import com.warewise.common.util.SequenceManager;
import com.warewise.server.server.networking.SocketServer;
import com.warewise.server.server.util.DataBaseLoader;
import com.warewise.server.service.ServiceHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Server class accepts client connections and stores them.
 * Now it tracks ServerConnection objects (each of which internally manages its own set of ServiceHandlers).
 */
public class Server extends SocketServer {
    private static DataBaseLoader dbLoader;
    private static SystemUsageMonitor systemMonitor;
    // Store active connections.
    private final Set<ServerConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());
    // A simple set of logged-in usernames.
    private final Set<String> userList = Collections.synchronizedSet(new java.util.HashSet<>());

    // Fields for storing data from the database
    private final List<User> users = new CopyOnWriteArrayList<>( new ArrayList<>());
    private final List<Category> categories = new CopyOnWriteArrayList<>( new ArrayList<>());
    private final List<Inventory> inventories = new CopyOnWriteArrayList<>( new ArrayList<>());
    private final List<Item> items = new CopyOnWriteArrayList<>( new ArrayList<>());
    private final List<Logs> logs = new CopyOnWriteArrayList<>( new ArrayList<>());
    private final List<Order> orders = new CopyOnWriteArrayList<>( new ArrayList<>());
    private final List<StockAlert> stockAlertList = new CopyOnWriteArrayList<>( new ArrayList<>());
    private final List<Supplier> suppliers = new CopyOnWriteArrayList<>( new ArrayList<>());


    protected Server(int port) throws IOException {
        super(port);
    }

    private static String uiHeader =
    """
            ██╗    ██╗ █████╗ ██████╗ ███████╗██╗    ██╗██╗███████╗███████╗
            ██║    ██║██╔══██╗██╔══██╗██╔════╝██║    ██║██║██╔════╝██╔════╝
            ██║ █╗ ██║███████║██████╔╝█████╗  ██║ █╗ ██║██║███████╗█████╗ \s
            ██║███╗██║██╔══██║██╔══██╗██╔══╝  ██║███╗██║██║╚════██║██╔══╝ \s
            ╚███╔███╔╝██║  ██║██║  ██║███████╗╚███╔███╔╝██║███████║███████╗
             ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚══╝╚══╝ ╚═╝╚══════╝╚══════╝
    """;




    public static void main(String[] args) {
        if (args.length == 1) {
            int port = Integer.parseInt(args[0]);
            try {
                Server server = new Server(port);
                System.out.println("Server started on port " + server.getPort() + ".");
                dbLoader = new DataBaseLoader(server);
                systemMonitor = new SystemUsageMonitor(server);
                SequenceManager.getInstance();
                dbLoader.loadDataFromDB();
                systemMonitor.start();
                System.out.println("Loaded data.");
                server.acceptConnections();
            } catch (IOException e) {
                System.out.println("Port " + port + " is already in use.");
            }
        } else {
            System.out.println(uiHeader);
            Scanner sc = new Scanner(System.in);
            int port = 0;
            boolean validPort = false;

            while (!validPort) {
                System.out.println("Enter port number: ");
                if (sc.hasNextInt()) {
                    port = sc.nextInt();

                    if (port >= 0 && port <= 65536) {
                        validPort = true;
                    } else {
                        System.out.println("Enter a valid port number: ");
                    }
                } else {
                    System.out.println("That's not a number. Please enter a number.");
                    sc.next();
                }
            }
            try {
                Server server = new Server(port);
                System.out.println("Server started on port " + server.getPort() + ".");
                dbLoader = new DataBaseLoader(server);
                systemMonitor = new SystemUsageMonitor(server);
                SequenceManager.getInstance();
                dbLoader.loadDataFromDB();
                systemMonitor.start();
                System.out.println("Loaded data.");
                server.acceptConnections();
            } catch (IOException e) {
                System.out.println("Port " + port + " is already in use.");
            }
        }
    }

    public  DataBaseLoader getDbLoader() {
        return dbLoader;
    }


    @Override
    public int getPort() {
        return super.getPort();
    }

    @Override
    protected void handleConnection(Socket socket) {
        try {
            ServerConnection connection = new ServerConnection(socket, this);
            addClient(connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void acceptConnections() throws IOException {
        super.acceptConnections();
    }

    @Override
    public synchronized void close() {
        systemMonitor.stopMonitoring();
        systemMonitor.interrupt();
        super.close();
    }

    /**
     * Adds a new connection to the set of active connections.
     */
    public void addClient(ServerConnection connection) {
        connections.add(connection);
        System.out.println("Added client connection.");
    }

    /**
     * Removes a connection based on a service handler's associated connection.
     * Each ServiceHandler can call this method when it detects a disconnect.
     */
    public void removeClient(ServiceHandler handler) {
        connections.remove(handler.getConnection());
//        System.out.println("Removed client connection.");
    }

    // Getters and utility methods.

    public Set<String> getUserList() {
        System.out.println("Sending list of logged in users.");
        return userList;
    }

    public String getHello() {
        return "Welcome to WARE WISE server";
    }

    public void addToList(String username) {
        userList.add(username);
    }

    public void removeFromList(String username) {
        userList.remove(username);
    }

    public boolean isLoggedIn(String username) {
        return userList.contains(username);
    }

    public void setUserData(List<User> userData) {
        this.users.clear();
        this.users.addAll(userData);
    }

    public void setCategoryData(List<Category> categoryData) {
        this.categories.clear();
        this.categories.addAll(categoryData);
    }


    public void setInventoryData(List<Inventory> inventoryData) {
        this.inventories.clear();
        this.inventories.addAll(inventoryData);
    }

    public void setItemData(List<Item> itemData) {
        this.items.clear();
        this.items.addAll(itemData);
    }

    public void setLogsData(List<Logs> logData) {
        this.logs.clear();
        this.logs.addAll(logData);
    }

    public void setOrderData(List<Order> orderData) {
        this.orders.clear();
        this.orders.addAll(orderData);
    }

    public void setStockData(List<StockAlert> stockData) {
        this.stockAlertList.clear();
        this.stockAlertList.addAll(stockData);
    }

    public void setSupplierData(List<Supplier> supplierData) {
        this.suppliers.clear();
        this.suppliers.addAll(supplierData);
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Inventory> getInventories() {
        return inventories;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Logs> getLogs() {
        return logs;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<StockAlert> getStockAlertList() {
        return stockAlertList;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public List<User> getUsers() {
        return users;
    }

    public Set<ServerConnection> getConnections() {
        return connections;
    }
}



