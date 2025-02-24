import com.warewise.common.model.*;
import com.warewise.common.util.database.handler.*;
import com.warewise.common.util.enums.OrderStatus;
import com.warewise.common.util.enums.StockAlertStatus;
import com.warewise.common.util.enums.UserRole;

import java.util.ArrayList;

public class HandlerTest {

    public static void main(String[] args) {
        testUserHandler();
        testCategoryHandler();
        testSupplierHandler();
        testInventoryHandler();
        testOrderHandler();
        testStockAlertHandler();
        testLogsHandler();
        testItemHandler();

        System.out.println("Full Handler Test Completed!");
    }

    // ------------------------------
    // ---- UserHandler Test ----
    private static void testUserHandler() {
        UserHandler userHandler = new UserHandler();
        User user1 = new User(1, "20-12-2005", "email@gmail.com", UserRole.ADMIN, "123", "calin");
        User user2 = new User(2, "19-12-2005", "email2@gmail.com", UserRole.MANAGER, "123", "alex");

        // Add users
        userHandler.addUser(user1);
        userHandler.addUser(user2);
        System.out.println("Users added:");
        userHandler.loadUsers().forEach(System.out::println);
        pause(1000);

        // Update user1
        user1.setEmail("updated_email@gmail.com");
        userHandler.updateUser(user1);
        System.out.println("User updated:");
        userHandler.loadUsers().forEach(System.out::println);
        pause(1000);

        // Delete users
        userHandler.deleteUser(user1);
        userHandler.deleteUser(user2);
        System.out.println("Users after deletion:");
        userHandler.loadUsers().forEach(System.out::println);
        pause(3000);
    }

    // ------------------------------
    // ---- CategoryHandler Test ----
    private static void testCategoryHandler() {
        CategoryHandler categoryHandler = new CategoryHandler();
        Category cat1 = new Category(0, "Electronics", "Devices and gadgets");
        Category cat2 = new Category(1, "Furniture", "Home and Office furniture");

        // Add categories
        categoryHandler.addCategory(cat1);
        categoryHandler.addCategory(cat2);
        System.out.println("Categories added:");
        categoryHandler.loadCategories().forEach(System.out::println);
        pause(1000);

        // Update category
        cat1.setDescription("Updated description for Electronics");
        categoryHandler.updateCategory(cat1);
        System.out.println("Category updated:");
        categoryHandler.loadCategories().forEach(System.out::println);
        pause(1000);

        // Delete categories
        categoryHandler.deleteCategory(cat1);
        categoryHandler.deleteCategory(cat2);
        System.out.println("Categories after deletion:");
        categoryHandler.loadCategories().forEach(System.out::println);
        pause(3000);
    }

    // ------------------------------
    // ---- SupplierHandler Test ----
    private static void testSupplierHandler() {
        SupplierHandler supplierHandler = new SupplierHandler();
        Supplier supplier = new Supplier(0,"Supplier Inc.", "supplier@example.com", "123456789", "123 Supplier St");

        // Add supplier
        supplierHandler.addSupplier(supplier);
        System.out.println("Supplier added:");
        supplierHandler.loadSuppliers().forEach(System.out::println);
        pause(1000);

        // Update supplier
        supplier.setContactEmail("updated_supplier@example.com");
        supplierHandler.updateSupplier(supplier);
        System.out.println("Supplier updated:");
        supplierHandler.loadSuppliers().forEach(System.out::println);
        pause(1000);

        // Delete supplier
        supplierHandler.deleteSupplier(supplier);
        System.out.println("Suppliers after deletion:");
        supplierHandler.loadSuppliers().forEach(System.out::println);
        pause(3000);
    }

    // ------------------------------
    // ---- InventoryHandler Test ----
    private static void testInventoryHandler() {
        InventoryHandler inventoryHandler = new InventoryHandler();
        Inventory inventory = new Inventory(0,"Laptop", "Gaming Laptop", new ArrayList<>(), 5, 1500.0, 1, "2023-01-01");

        // Add inventory
        inventoryHandler.addInventory(inventory);
        System.out.println("Inventory added:");
        inventoryHandler.loadInventory().forEach(inv -> System.out.println(inv.getID() + " - " + inv.getName()));
        pause(1000);

        // Update inventory
        inventory.setDescription("High-performance gaming laptop");
        inventoryHandler.updateInventory(inventory);
        System.out.println("Inventory updated:");
        inventoryHandler.loadInventory().forEach(inv -> System.out.println(inv.getID() + " - " + inv.getDescription()));
        pause(1000);

        // Delete inventory
        inventoryHandler.deleteInventory(inventory);
        System.out.println("Inventory after deletion:");
        inventoryHandler.loadInventory().forEach(inv -> System.out.println(inv.getID() + " - " + inv.getName()));
        pause(3000);
    }

    // ------------------------------
    // ---- OrderHandler Test ----
    private static void testOrderHandler() {
        OrderHandler orderHandler = new OrderHandler();
        Order order = new Order(0, "John Doe", "john@example.com", OrderStatus.PENDING, "2023-01-01", "2023-01-01");

        // Add order
        orderHandler.addOrder(order);
        System.out.println("Order added:");
        orderHandler.loadOrders().forEach(o -> System.out.println(o.getID() + " - " + o.getCustomerName()));
        pause(1000);

        // Update order
        order.setStatus(OrderStatus.FULFILLED);
        orderHandler.updateOrder(order);
        System.out.println("Order updated:");
        orderHandler.loadOrders().forEach(o -> System.out.println(o.getID() + " - " + o.getStatus()));
        pause(1000);

        // Delete order
        orderHandler.deleteOrder(order);
        System.out.println("Orders after deletion:");
        orderHandler.loadOrders().forEach(o -> System.out.println(o.getID() + " - " + o.getCustomerName()));
        pause(3000);
    }

    // ------------------------------
    // ---- StockAlertHandler Test ----
    private static void testStockAlertHandler() {
        StockAlertHandler stockAlertHandler = new StockAlertHandler();
        StockAlert alert = new StockAlert(0, 1, StockAlertStatus.ACTIVE, "2023-01-01", "false");

        // Add stock alert
        stockAlertHandler.addStockAlert(alert);
        System.out.println("Stock alert added:");
        stockAlertHandler.loadStockAlerts().forEach(a -> System.out.println(a.getID() + " - " + a.getProductID()));
        pause(1000);

        // Update stock alert
        alert.setResolved("true");
        stockAlertHandler.updateStockAlert(alert);
        System.out.println("Stock alert updated:");
        stockAlertHandler.loadStockAlerts().forEach(a -> System.out.println(a.getID() + " - " + a.getResolved()));
        pause(1000);

        // Delete stock alert
        stockAlertHandler.deleteStockAlert(alert);
        System.out.println("Stock alerts after deletion:");
        stockAlertHandler.loadStockAlerts().forEach(a -> System.out.println(a.getID()));
        pause(3000);
    }

    // ------------------------------
    // ---- LogsHandler Test ----
    private static void testLogsHandler() {
        LogsHandler logsHandler = new LogsHandler();
        Logs log = new Logs(0, 1, "USER_LOGIN", "User logged in", "2023-01-01");

        // Add log
        logsHandler.addLog(log);
        System.out.println("Log added:");
        logsHandler.loadLogs().forEach(l -> System.out.println(l.getID() + " - " + l.getAction()));
        pause(1000);

        // Update log
        log.setAction("USER_LOGOUT");
        logsHandler.updateLog(log);
        System.out.println("Log updated:");
        logsHandler.loadLogs().forEach(l -> System.out.println(l.getID() + " - " + l.getAction()));
        pause(1000);

        // Delete log
        logsHandler.deleteLog(log);
        System.out.println("Logs after deletion:");
        logsHandler.loadLogs().forEach(l -> System.out.println(l.getID()));
        pause(3000);
    }

    // ------------------------------
// ---- ItemHandler Test ----
    private static void testItemHandler() {
        ItemHandler itemHandler = new ItemHandler();
        // Create a sample Item (assuming category is null or set later)
        Item item = new Item(0, 1, 1, 5, 19.99, 5 * 19.99, null);

        // Add item
        itemHandler.addItem(item);
        System.out.println("Item added:");
        itemHandler.loadItems(null).forEach(it -> System.out.println(it.getID() + " - Quantity: " + it.getQuantity() + ", Price: " + it.getPrice()));
        pause(1000);

        // Update item
        item.setQuantity(10);
        item.setPrice(24.99);
        itemHandler.updateItem(item);
        System.out.println("Item updated:");
        itemHandler.loadItems(null).forEach(it -> System.out.println(it.getID() + " - Quantity: " + it.getQuantity() + ", Price: " + it.getPrice()));
        pause(1000);

        // Delete item
        itemHandler.deleteItem(item);
        System.out.println("Items after deletion:");
        itemHandler.loadItems(null).forEach(it -> System.out.println(it.getID()));
        pause(3000);
    }


    // ------------------------------
    // Utility method for adding delays between operations
    private static void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
