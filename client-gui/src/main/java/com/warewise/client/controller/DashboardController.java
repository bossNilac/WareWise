package com.warewise.client.controller;

import com.warewise.client.networking.DataHandler;
import com.warewise.client.util.model.Category;
import com.warewise.client.util.model.GeneralItem;
import com.warewise.client.util.model.Inventory;
import com.warewise.client.util.model.Order;
import com.warewise.client.util.model.User;
import com.warewise.client.util.model.WarehouseItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {
    private static final int LOW_STOCK_THRESHOLD = 10;

    @FXML private Button notificationButton;
    @FXML private Label totalOrdersLabel;
    @FXML private Label inventoryLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label salesLabel;
    @FXML private ComboBox<String> chartTypeComboBox;
    @FXML private StackPane chartContainer;
    @FXML private ListView<String> activityFeedList;
    @FXML private Button createOrderBtn;
    @FXML private Button updateInventoryBtn;
    @FXML private ListView<String> notificationsList;

    private final ObservableList<String> notifications = FXCollections.observableArrayList();
    private List<Category> categories = List.of();
    private List<GeneralItem> generalItems = List.of();
    private List<Inventory> stockRecords = List.of();
    private List<WarehouseItem> orderItems = List.of();
    private List<Order> orders = List.of();
    private List<User> users = List.of();
    private Map<Integer, User> idToUser = Map.of();
    private Map<Integer, GeneralItem> giMap = Map.of();
    private String[] nameLabels = new String[0];
    private Number[] numberValues = new Number[0];
    private MainController mainController;

    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    @Override
    public void initialize(URL loc, ResourceBundle res) {
        DataHandler.initTables("Category");
        DataHandler.initTables("GeneralItem");
        DataHandler.initTables("Inventory");
        DataHandler.initTables("WarehouseItem");
        DataHandler.initTables("Orders");
        DataHandler.initTables("Logs");
        DataHandler.initTables("Users");

        categories = safeList(DataHandler.parsedCategoriesList);
        generalItems = safeList(DataHandler.parsedItemsList);
        stockRecords = safeList(DataHandler.parsedInventoryList);
        orderItems = safeList(DataHandler.parsedWarehouseItemsList);
        orders = safeList(DataHandler.parsedOrdersList);
        users = safeList(DataHandler.parsedUsersList);

        idToUser = users.stream().collect(Collectors.toMap(User::getID, user -> user, (first, second) -> first));
        giMap = generalItems.stream().collect(Collectors.toMap(GeneralItem::getId, item -> item, (first, second) -> first));

        configureTextList(activityFeedList);
        configureTextList(notificationsList);
        populateKpis();
        buildCategoryChartData();

        chartTypeComboBox.setItems(FXCollections.observableArrayList("Bar Chart", "Pie Chart"));
        chartTypeComboBox.setValue("Bar Chart");
        chartTypeComboBox.setOnAction(e -> updateChart());
        updateChart();

        User me = DataHandler.getCurrentUser();
        String currentUsername = me == null ? "" : me.getUsername();
        activityFeedList.setItems(FXCollections.observableArrayList(
                DataHandler.getRecentActionsForOtherUsers(currentUsername, 6)
        ));

        createOrderBtn.setOnAction(e -> handleCreateOrder());
        updateInventoryBtn.setOnAction(e -> handleUpdateInventory());
        createNotificationList();
    }

    private void populateKpis() {
        int totalOrders = orders.size();
        double totalSales = orderItems.stream()
                .filter(WarehouseItem::getSold)
                .mapToDouble(WarehouseItem::getTotal)
                .sum();
        int totalInventory = stockRecords.stream()
                .mapToInt(Inventory::getQuantity)
                .sum();
        long lowStockCount = stockRecords.stream()
                .filter(record -> record.getQuantity() <= LOW_STOCK_THRESHOLD)
                .count();

        totalOrdersLabel.setText(String.valueOf(totalOrders));
        salesLabel.setText(String.format("$%.2f", totalSales));
        inventoryLabel.setText(String.valueOf(totalInventory));
        lowStockLabel.setText(String.valueOf(lowStockCount));
    }

    private void buildCategoryChartData() {
        int nCats = categories.size();
        nameLabels = new String[nCats];
        numberValues = new Number[nCats];
        for (int i = 0; i < nCats; i++) {
            Category cat = categories.get(i);
            nameLabels[i] = cat.getName();
            numberValues[i] = generalItems.stream()
                    .filter(item -> item.getCategoryId() == cat.getID())
                    .mapToInt(GeneralItem::getSetQuantity)
                    .sum();
        }
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
        NumberAxis y = new NumberAxis();
        x.setLabel("Category");
        y.setLabel("Count");
        BarChart<String, Number> bc = new BarChart<>(x, y);
        bc.setTitle("Inventory by Category");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(seriesName);
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] != null && vals[i] != null) {
                series.getData().add(new XYChart.Data<>(labels[i], vals[i]));
            }
        }
        bc.getData().setAll(series);
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
        if (mainController != null) {
            mainController.loadContentPane("UpdateInventoryView.fxml");
        }
    }

    private void createNotificationList() {
        LocalDateTime now = LocalDateTime.now();
        notifications.clear();

        for (Order order : orders) {
            LocalDateTime created = DataHandler.parseServerDateTime(order.getCreatedAt());
            if (created != null && created.isAfter(now.minusHours(24))) {
                User user = idToUser.get(order.getUserId());
                String name = user != null ? user.getUsername() : "Unknown";
                notifications.add("New order received by " + name + " at " + created.format(DateTimeFormatter.ofPattern("HH:mm")));
            }

            LocalDateTime updated = DataHandler.parseServerDateTime(order.getUpdatedAt());
            if (updated != null && updated.isAfter(now.minusHours(24)) && !updated.equals(created)) {
                User user = idToUser.get(order.getUserId());
                String name = user != null ? user.getUsername() : "Unknown";
                notifications.add("Order updated by " + name + " at " + updated.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }

        for (Inventory record : stockRecords) {
            if (record.getQuantity() <= LOW_STOCK_THRESHOLD) {
                notifications.add("Low stock alert: " + record.getName() + " has only " + record.getQuantity() + " left");
            }
        }

        if (notifications.isEmpty()) {
            notifications.add("No active notifications");
        }
        notificationsList.setItems(notifications);
        notificationsList.refresh();
    }

    @FXML
    public void openNotifications(ActionEvent actionEvent) {
        ObservableList<String> list = FXCollections.observableArrayList();
        ObservableList<String> source = notificationsList.getItems();

        if (source == null || source.isEmpty()) {
            list.add("No active notifications");
        } else {
            int start = Math.max(0, source.size() - 5);
            for (int index = start; index < source.size(); index++) {
                list.add(source.get(index));
            }
        }

        ListView<String> popoverNotifications = new ListView<>(list);
        configureTextList(popoverNotifications);
        popoverNotifications.setPrefSize(420, 180);

        PopOver pop = new PopOver(popoverNotifications);
        pop.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
        pop.setDetachable(false);
        pop.show(notificationButton);
    }

    private void configureTextList(ListView<String> listView) {
        listView.setMinHeight(150);
        listView.setPlaceholder(new Label("No active notifications"));
        listView.setCellFactory(view -> new ListCell<>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.maxWidthProperty().bind(view.widthProperty().subtract(32));
                label.getStyleClass().add("dashboard-list-text");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                label.setText(item);
                setText(null);
                setGraphic(label);
            }
        });
    }

    private static <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }
}
