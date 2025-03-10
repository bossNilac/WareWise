package com.warewise.common.util.protocol;

/**
 * WarehouseProtocol class with constants for creating protocol messages.
 * This protocol defines communication commands between the client and the server.
 */
public final class Protocol {

    // Common constants
    public static final String SEPARATOR = "~";  // Message separator
    public static final String ERRORTAG = "[ERROR]: ";  // Error tag for responses

    // ============================
    // ---- Authentication Commands ----
    // ============================
    public static final String LOGIN = "LOGIN";               // LOGIN~username~password
    public static final String HELLO = "HELLO";               // HELLO
    public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS"; // LOGIN_SUCCESS~role
    public static final String LOGIN_FAILURE = "LOGIN_FAILURE"; // LOGIN_FAILURE~reason
    public static final String LOGOUT = "LOGOUT";             // LOGOUT~userID

    // ============================
    // ---- User Management Commands ----
    // ============================
    public static final String ADD_USER = "ADD_USER";         // ADD_USER~username~role~email~password
    public static final String UPDATE_USER = "UPDATE_USER";   // UPDATE_USER~userID~field~newValue
    public static final String KICK_USER = "KICK_USER";   // KICK_USER~userID~value
    public static final String DELETE_USER = "DELETE_USER";   // DELETE_USER~userID
    public static final String LIST_USERS = "LIST_USERS";     // LIST_USERS
    public static final String LIST_ONLINE_USERS = "LIST_ONLINE_USERS";     // LIST_ONLINE_USERS

    // ============================
    // ---- Inventory Management ----
    // ============================
    public static final String ADD_ITEM = "ADD_ITEM";         // ADD_ITEM~productName~description~quantity~price~supplierID
    public static final String UPDATE_ITEM = "UPDATE_ITEM";   // UPDATE_ITEM~productID~field~newValue
    public static final String DELETE_ITEM = "DELETE_ITEM";   // DELETE_ITEM~productID
    public static final String LIST_ITEMS = "LIST_ITEMS";     // LIST_ITEMS

    // ============================
// ---- Category Management ----
// ============================
    public static final String ADD_CATEGORY = "ADD_CATEGORY";         // ADD_CATEGORY~categoryName~description
    public static final String UPDATE_CATEGORY = "UPDATE_CATEGORY";   // UPDATE_CATEGORY~categoryID~field~newValue
    public static final String DELETE_CATEGORY = "DELETE_CATEGORY";   // DELETE_CATEGORY~categoryID
    public static final String LIST_CATEGORIES = "LIST_CATEGORIES";   // LIST_CATEGORIES

    // ============================
// ---- Inventory Management ----
// ============================
    public static final String ADD_INVENTORY = "ADD_INVENTORY";         // ADD_INVENTORY~name~description~quantity~lastUpdated
    public static final String UPDATE_INVENTORY = "UPDATE_INVENTORY";   // UPDATE_INVENTORY~inventoryID~name~description~quantity~lastUpdated
    public static final String DELETE_INVENTORY = "DELETE_INVENTORY";   // DELETE_INVENTORY~inventoryID
    public static final String LIST_INVENTORY = "LIST_INVENTORY";       // LIST_INVENTORY

    // ============================
    // ---- Order Management ----
    // ============================
    public static final String CREATE_ORDER = "CREATE_ORDER"; // CREATE_ORDER~customerName~itemID~quantity
    public static final String UPDATE_ORDER = "UPDATE_ORDER"; // UPDATE_ORDER~orderID~status
    public static final String DELETE_ORDER = "DELETE_ORDER"; // DELETE_ORDER~orderID
    public static final String LIST_ORDERS = "LIST_ORDERS";   // LIST_ORDERS

    // ============================
    // ---- Supplier Management ----
    // ============================
    public static final String ADD_SUPPLIER = "ADD_SUPPLIER";     // ADD_SUPPLIER~name~email~phone~address
    public static final String UPDATE_SUPPLIER = "UPDATE_SUPPLIER"; // UPDATE_SUPPLIER~supplierID~field~newValue
    public static final String DELETE_SUPPLIER = "DELETE_SUPPLIER"; // DELETE_SUPPLIER~supplierID
    public static final String LIST_SUPPLIERS = "LIST_SUPPLIERS";   // LIST_SUPPLIERS

    // ============================
    // ---- Stock Alerts ----
    // ============================
    public static final String LIST_STOCK_ALERTS = "LIST_STOCK_ALERTS";   // LIST_SUPPLIERS
    public static final String DELETE_STOCK_ALERT = "DELETE_STOCK_ALERT";   // DELETE_STOCK_ALERT~stockAlertID
    public static final String UPDATE_STOCK_ALERT = "UPDATE_STOCK_ALERT";   // UPDATE_STOCK_ALERT~stockAlertID~productID~threshold~createdAt~resolved
    public static final String STOCK_ALERT = "STOCK_ALERT";   // STOCK_ALERT~productID~threshold
    public static final String RESOLVE_ALERT = "RESOLVE_ALERT"; // RESOLVE_ALERT~alertID

    // ============================
    // ---- Reporting ----
    // ============================
    public static final String GENERATE_REPORT = "GENERATE_REPORT"; // GENERATE_REPORT~reportType~startDate~endDate
    public static final String REPORT_RESULT = "REPORT_RESULT";     // REPORT_RESULT~reportData

    // ============================
    // ---- Error & System Commands ----
    // ============================
    public static final String SERVER_ERROR = "SERVER_ERROR";   // SERVER_ERROR~message
    public static final String INVALID_COMMAND = "INVALID_COMMAND"; // INVALID_COMMAND~reason
    public static final String HEARTBEAT = "HEARTBEAT";         // HEARTBEAT~timestamp
    public static final String SEND_BENCHMARK = "SEND_BENCHMARK";         // SEND_BENCHMARK~DATA~DATA

    // Private constructor to prevent instantiation
    private Protocol() {
        // No instances allowed
    }
}
