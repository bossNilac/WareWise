package com.warewise.client.controller;

import com.warewise.client.networking.DataHandler;
import com.warewise.client.util.model.Category;
import com.warewise.client.util.model.GeneralItem;   // maps items_general_data
import com.warewise.client.util.model.Inventory;     // maps inventory
import com.warewise.client.util.model.Order;         // maps orders
import com.warewise.client.util.model.WarehouseItem;     // maps items (the order‐line table)
import com.warewise.client.util.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    private static final int LOW_STOCK_THRESHOLD = 10;

    // KPI Labels
    @FXML private Label totalOrdersLabel;
    @FXML private Label inventoryLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label salesLabel;

    // Chart Section
    @FXML private ComboBox<String> chartTypeComboBox;
    @FXML private StackPane       chartContainer;

    // Recent Activity & Actions
    @FXML private ListView<String> activityFeedList;
    @FXML private Button           createOrderBtn;
    @FXML private Button           updateInventoryBtn;
    @FXML private ListView<String> notificationsList;

    // these back the chart
    private String[] nameLabels;
    private Number[] numberValues;

    private MainController mainController;

    public void setMainController(MainController mc) {
        this.mainController = mc;
    }


    @Override
    public void initialize(URL loc, ResourceBundle res) {
        // 1) Load all relevant tables
        DataHandler.initTables("Category");
        DataHandler.initTables("GeneralItem");   // your items_general_data
        DataHandler.initTables("Inventory");
        DataHandler.initTables("WarehouseItem");         // your order‐lines table
        DataHandler.initTables("Orders");
        DataHandler.initTables("Logs");
        DataHandler.initTables("Users");

        List<Category>    categories    = DataHandler.parsedCategoriesList;
        List<GeneralItem> generalItems  = DataHandler.parsedItemsList;
        List<Inventory>   stockRecords  = DataHandler.parsedInventoryList;
        List<WarehouseItem>   orderItems    = DataHandler.parsedWarehouseItemsList;
        List<Order>       orders        = DataHandler.parsedOrdersList;
        List<User> users = DataHandler.parsedUsersList;

        Map<Integer,User> idToUser = users.stream()
                .collect(Collectors.toMap(User::getID, u->u));
        Map<Integer,GeneralItem> giMap = generalItems.stream()
                .collect(Collectors.toMap(GeneralItem::getId, gi->gi));

        // 2) Compute KPIs
        int totalOrders    = orders.size();
        double totalSales  = orderItems.stream()
                .filter(WarehouseItem::getSold)      // sold == true
                .mapToDouble(WarehouseItem::getTotal)
                .sum();
        int totalInventory = stockRecords.stream()
                .mapToInt(Inventory::getQuantity)
                .sum();
        long lowStockCount = orderItems.stream()
                .filter(oi -> oi.getQuantity() <= LOW_STOCK_THRESHOLD)
                .count();


        totalOrdersLabel.setText(String.valueOf(totalOrders));
        salesLabel      .setText(String.format("$%.2f", totalSales));
        inventoryLabel  .setText(String.valueOf(totalInventory));
        lowStockLabel   .setText(String.valueOf(lowStockCount));

        // 3) Build chart data: sum up set_quantity from generalItems by category
        int nCats = categories.size();
        nameLabels   = new String[nCats];
        numberValues = new Number[nCats];
        for (int i = 0; i < nCats; i++) {
            Category cat = categories.get(i);
            nameLabels[i]   = cat.getName();
            numberValues[i] = generalItems.stream()
                    .filter(it -> it.getCategoryId() == cat.getID())
                    .mapToInt(GeneralItem::getSetQuantity)
                    .sum();
        }

        // 4) Chart toggle
        chartTypeComboBox.setItems(
                FXCollections.observableArrayList("Bar Chart","Pie Chart")
        );
        chartTypeComboBox.setValue("Bar Chart");
        chartTypeComboBox.setOnAction(e -> updateChart());
        updateChart();

        // 5) Recent activity & quick actions
        User me = DataHandler.getCurrentUser();
        activityFeedList.setItems(
                FXCollections.observableArrayList(
                        DataHandler.getRecentActionsForOtherUsers(me.getUsername(), 6)
                )
        );
        createOrderBtn   .setOnAction(e -> handleCreateOrder());
        updateInventoryBtn.setOnAction(e -> handleUpdateInventory());



        ObservableList<String> notifications = FXCollections.observableArrayList();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

// — New orders and updates in the last 24h —
        for(Order o : orders) {
            // parse using whatever format you store createdAt/updatedAt in
            LocalDateTime created = LocalDateTime.parse(o.getCreatedAt(), dtFmt);
            if (created.isAfter(now.minusHours(24))) {
                User u = idToUser.get(o.getUserId());
                String name = u != null ? u.getUsername() : "Unknown";
                notifications.add(
                        "New order received by " + name +
                                " at " + created.format(DateTimeFormatter.ofPattern("HH:mm"))
                );
            }

            LocalDateTime updated = LocalDateTime.parse(o.getUpdatedAt(), dtFmt);
            // only count an “update” if it’s different from the creation time
            if (updated.isAfter(now.minusHours(24)) && !updated.equals(created)) {
                User u = idToUser.get(o.getUserId());
                String name = u != null ? u.getUsername() : "Unknown";
                notifications.add(
                        "Order updated by " + name +
                                " at " + updated.format(DateTimeFormatter.ofPattern("HH:mm"))
                );
            }
        }

        int LOW_STOCK_THRESHOLD = 10;
        for (WarehouseItem oi : orderItems) {
            if (oi.getQuantity() <= LOW_STOCK_THRESHOLD) {
                GeneralItem gi = giMap.get(oi.getGeneralItemId());
                String itemName = gi != null ? gi.getName() : "Unknown item";
                notifications.add(
                        "Low stock alert: “" + itemName +
                                "” has only " + oi.getQuantity() + " left"
                );
            }
        }
// 3) Finally wire it up
        notificationsList.setItems(notifications);

    }

    private void updateChart() {
        if ("Pie Chart".equals(chartTypeComboBox.getValue())) {
            initPieChart(nameLabels, numberValues);
        } else {
            initBarChart(nameLabels, numberValues, "Items per Category");
        }
    }

    private void initBarChart(String[] labels, Number[] vals, String seriesName) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis   y = new NumberAxis();
        x.setLabel("Category");  y.setLabel("Count");
        BarChart<String,Number> bc = new BarChart<>(x,y);
        bc.setTitle("Inventory by Category");
        XYChart.Series<String,Number> s = new XYChart.Series<>();
        s.setName(seriesName);
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] != null && vals[i] != null) {
                s.getData().add(new XYChart.Data<>(labels[i], vals[i]));
            }
        }
        bc.getData().setAll(s);
        chartContainer.getChildren().setAll(bc);
    }

    private void initPieChart(String[] labels, Number[] vals) {
        PieChart pc = new PieChart();
        pc.setTitle("Inventory Distribution");
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] != null && vals[i] != null) {
                pc.getData().add(new PieChart.Data(labels[i], vals[i].doubleValue()));
            }
        }
        chartContainer.getChildren().setAll(pc);
    }

    private void handleCreateOrder() {
        if (mainController != null) {
            mainController.loadContentPane("OrdersView.fxml");
        }
    }


    private void handleUpdateInventory() {
        System.out.println("Update Inventory clicked");
    }
}
