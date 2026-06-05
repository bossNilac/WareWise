package com.warewise.client.controller;

import com.warewise.client.networking.DataHandler;
import com.warewise.client.util.SimplePdfReportWriter;
import com.warewise.client.util.enums.OrderStatus;
import com.warewise.client.util.model.Category;
import com.warewise.client.util.model.GeneralItem;
import com.warewise.client.util.model.Inventory;
import com.warewise.client.util.model.Order;
import com.warewise.client.util.model.User;
import com.warewise.client.util.model.Warehouse;
import com.warewise.client.util.model.WarehouseItem;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReportsController implements Initializable {
    @FXML private Label totalOrdersLabel;
    @FXML private Label fulfilledOrdersLabel;
    @FXML private Label stockUnitsLabel;
    @FXML private Label inventoryValueLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label revenueLabel;
    @FXML private ComboBox<String> reportSelector;
    @FXML private StackPane chartPane;
    @FXML private TableView<ReportRow> reportTable;
    @FXML private TableColumn<ReportRow, String> metricColumn;
    @FXML private TableColumn<ReportRow, String> valueColumn;

    private List<User> users = List.of();
    private List<Order> orders = List.of();
    private List<Inventory> inventory = List.of();
    private List<Warehouse> warehouses = List.of();
    private List<WarehouseItem> warehouseItems = List.of();
    private List<GeneralItem> generalItems = List.of();
    private List<Category> categories = List.of();
    private final ObservableList<ReportRow> rows = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        metricColumn.setCellValueFactory(data -> data.getValue().metricProperty());
        valueColumn.setCellValueFactory(data -> data.getValue().valueProperty());
        reportTable.setItems(rows);
        reportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadData();
        populateKpis();

        reportSelector.setItems(FXCollections.observableArrayList(
                "Orders by User",
                "Inventory by Warehouse",
                "Order Status",
                "Inventory by Category",
                "Revenue by Item"
        ));
        reportSelector.setValue("Orders by User");
        reportSelector.setOnAction(event -> refreshReport());
        refreshReport();
    }

    @FXML
    private void exportPdf(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export WareWise Report");
        chooser.setInitialFileName("warewise-report.pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));

        Window owner = chartPane.getScene() == null ? null : chartPane.getScene().getWindow();
        File target = chooser.showSaveDialog(owner);
        if (target == null) {
            return;
        }

        try {
            SimplePdfReportWriter.writeReport(target.toPath(), "WareWise Reports and Analytics", buildPdfLines());
            showAlert(Alert.AlertType.INFORMATION, "Report exported", "PDF saved to " + target.getAbsolutePath());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Export failed", e.getMessage());
        }
    }

    private void loadData() {
        DataHandler.initTables("Users");
        DataHandler.initTables("Orders");
        DataHandler.initTables("Inventory");
        DataHandler.initTables("Warehouse");
        DataHandler.initTables("WarehouseItem");
        DataHandler.initTables("GeneralItem");
        DataHandler.initTables("Category");

        users = safeList(DataHandler.parsedUsersList);
        orders = safeList(DataHandler.parsedOrdersList);
        inventory = safeList(DataHandler.parsedInventoryList);
        warehouses = safeList(DataHandler.parsedWarehousesList);
        warehouseItems = safeList(DataHandler.parsedWarehouseItemsList);
        generalItems = safeList(DataHandler.parsedItemsList);
        categories = safeList(DataHandler.parsedCategoriesList);
    }

    private void populateKpis() {
        int totalOrders = orders.size();
        long fulfilledOrders = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.FULFILLED)
                .count();
        int stockUnits = inventory.stream().mapToInt(Inventory::getQuantity).sum();
        long lowStockRecords = inventory.stream()
                .filter(record -> record.getQuantity() <= DataHandler.LOW_STOCK_THRESHOLD)
                .count();
        double revenue = warehouseItems.stream()
                .filter(WarehouseItem::getSold)
                .mapToDouble(WarehouseItem::getTotal)
                .sum();
        double inventoryValue = estimateInventoryValue();

        totalOrdersLabel.setText(String.valueOf(totalOrders));
        fulfilledOrdersLabel.setText(String.valueOf(fulfilledOrders));
        stockUnitsLabel.setText(String.valueOf(stockUnits));
        lowStockLabel.setText(String.valueOf(lowStockRecords));
        revenueLabel.setText(String.format("$%.2f", revenue));
        inventoryValueLabel.setText(String.format("$%.2f", inventoryValue));
    }

    private void refreshReport() {
        String selected = reportSelector.getValue();
        if ("Inventory by Warehouse".equals(selected)) {
            showMapReport(selected, inventoryByWarehouse(), false);
        } else if ("Order Status".equals(selected)) {
            showMapReport(selected, orderStatusCounts(), true);
        } else if ("Inventory by Category".equals(selected)) {
            showMapReport(selected, inventoryByCategory(), false);
        } else if ("Revenue by Item".equals(selected)) {
            showMapReport(selected, revenueByItem(), false);
        } else {
            showMapReport("Orders by User", ordersByUser(), false);
        }
    }

    private void showMapReport(String title, Map<String, ? extends Number> data, boolean pieChart) {
        rows.setAll(data.entrySet().stream()
                .map(entry -> new ReportRow(entry.getKey(), formatNumber(entry.getValue())))
                .collect(Collectors.toList()));

        if (pieChart) {
            PieChart chart = new PieChart();
            chart.setTitle(title);
            data.forEach((name, value) -> chart.getData().add(new PieChart.Data(name, value.doubleValue())));
            chartPane.getChildren().setAll(chart);
            return;
        }

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setTitle(title);
        xAxis.setLabel("Metric");
        yAxis.setLabel("Value");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        data.forEach((name, value) -> series.getData().add(new XYChart.Data<>(name, value)));
        chart.getData().setAll(series);
        chartPane.getChildren().setAll(chart);
    }

    private Map<String, Integer> ordersByUser() {
        Map<Integer, User> userById = users.stream()
                .collect(Collectors.toMap(User::getID, Function.identity(), (first, second) -> first));

        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> usernameFor(userById.get(order.getUserId())),
                        LinkedHashMap::new,
                        Collectors.summingInt(order -> 1)
                ));
    }

    private Map<String, Integer> inventoryByWarehouse() {
        Map<Integer, Warehouse> warehouseById = warehouses.stream()
                .collect(Collectors.toMap(Warehouse::getID, Function.identity(), (first, second) -> first));

        return inventory.stream()
                .collect(Collectors.groupingBy(
                        record -> warehouseNameFor(warehouseById.get(record.getWarehouseId()), record.getWarehouseId()),
                        LinkedHashMap::new,
                        Collectors.summingInt(Inventory::getQuantity)
                ));
    }

    private Map<String, Integer> orderStatusCounts() {
        Map<OrderStatus, Integer> counts = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, 0);
        }
        orders.forEach(order -> counts.computeIfPresent(order.getStatus(), (status, count) -> count + 1));

        Map<String, Integer> result = new LinkedHashMap<>();
        counts.forEach((status, count) -> result.put(status.toString(), count));
        return result;
    }

    private Map<String, Integer> inventoryByCategory() {
        Map<Integer, Category> categoryById = categories.stream()
                .collect(Collectors.toMap(Category::getID, Function.identity(), (first, second) -> first));

        return generalItems.stream()
                .collect(Collectors.groupingBy(
                        item -> categoryNameFor(categoryById.get(item.getCategoryId()), item.getCategoryId()),
                        LinkedHashMap::new,
                        Collectors.summingInt(GeneralItem::getSetQuantity)
                ));
    }

    private Map<String, Double> revenueByItem() {
        Map<Integer, GeneralItem> itemById = generalItems.stream()
                .collect(Collectors.toMap(GeneralItem::getId, Function.identity(), (first, second) -> first));

        return warehouseItems.stream()
                .filter(WarehouseItem::getSold)
                .collect(Collectors.groupingBy(
                        item -> itemNameFor(itemById.get(item.getGeneralItemId()), item.getGeneralItemId()),
                        Collectors.summingDouble(WarehouseItem::getTotal)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (first, second) -> first,
                        LinkedHashMap::new
                ));
    }

    private double estimateInventoryValue() {
        Map<String, GeneralItem> itemByName = generalItems.stream()
                .collect(Collectors.toMap(
                        item -> item.getName().toLowerCase(),
                        Function.identity(),
                        (first, second) -> first
                ));

        return inventory.stream()
                .mapToDouble(record -> {
                    GeneralItem item = itemByName.get(record.getName().toLowerCase());
                    return item == null ? 0 : record.getQuantity() * item.getPrice();
                })
                .sum();
    }

    private List<String> buildPdfLines() {
        List<String> lines = new ArrayList<>();
        lines.add("KPIs");
        lines.add("Total orders: " + totalOrdersLabel.getText());
        lines.add("Fulfilled orders: " + fulfilledOrdersLabel.getText());
        lines.add("Stock units: " + stockUnitsLabel.getText());
        lines.add("Estimated inventory value: " + inventoryValueLabel.getText());
        lines.add("Low stock records: " + lowStockLabel.getText());
        lines.add("Sold revenue: " + revenueLabel.getText());
        lines.add("");
        lines.add(reportSelector.getValue());
        for (ReportRow row : rows) {
            lines.add(row.getMetric() + ": " + row.getValue());
        }
        return lines;
    }

    private static <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private static String usernameFor(User user) {
        return user == null || user.getUsername() == null ? "Unknown user" : user.getUsername();
    }

    private static String warehouseNameFor(Warehouse warehouse, int id) {
        return warehouse == null || warehouse.getName() == null ? "Warehouse " + id : warehouse.getName();
    }

    private static String categoryNameFor(Category category, int id) {
        return category == null || category.getName() == null ? "Category " + id : category.getName();
    }

    private static String itemNameFor(GeneralItem item, int id) {
        return item == null || item.getName() == null ? "Item " + id : item.getName();
    }

    private static String formatNumber(Number number) {
        if (number instanceof Double || number instanceof Float) {
            return String.format("%.2f", number.doubleValue());
        }
        return String.valueOf(number.intValue());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ReportRow {
        private final SimpleStringProperty metric;
        private final SimpleStringProperty value;

        public ReportRow(String metric, String value) {
            this.metric = new SimpleStringProperty(metric);
            this.value = new SimpleStringProperty(value);
        }

        public String getMetric() {
            return metric.get();
        }

        public String getValue() {
            return value.get();
        }

        public SimpleStringProperty metricProperty() {
            return metric;
        }

        public SimpleStringProperty valueProperty() {
            return value;
        }
    }
}
