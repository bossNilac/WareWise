package com.warewise.client.controller;

import com.warewise.client.networking.DataHandler;
import com.warewise.client.util.model.GeneralItem;
import com.warewise.client.util.model.Inventory;
import com.warewise.client.util.model.User;
import com.warewise.client.util.model.Warehouse;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WarehouseHealthController implements Initializable {
    @FXML private BarChart<String, Number> warehouseChart;
    @FXML private TableView<WarehouseHealthRow> healthTable;
    @FXML private TableColumn<WarehouseHealthRow, String> warehouseColumn;
    @FXML private TableColumn<WarehouseHealthRow, Integer> stockUnitsColumn;
    @FXML private TableColumn<WarehouseHealthRow, Integer> lowStockColumn;
    @FXML private TableColumn<WarehouseHealthRow, Double> valueColumn;
    @FXML private TableColumn<WarehouseHealthRow, String> managersColumn;

    private final ObservableList<WarehouseHealthRow> rows = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataHandler.initTables("Warehouse");
        DataHandler.initTables("Inventory");
        DataHandler.initTables("GeneralItem");
        DataHandler.initTables("Users");

        List<Warehouse> warehouses = DataHandler.parsedWarehousesList == null ? List.of() : DataHandler.parsedWarehousesList;
        List<Inventory> inventory = DataHandler.parsedInventoryList == null ? List.of() : DataHandler.parsedInventoryList;
        List<GeneralItem> generalItems = DataHandler.parsedItemsList == null ? List.of() : DataHandler.parsedItemsList;
        List<User> users = DataHandler.parsedUsersList == null ? List.of() : DataHandler.parsedUsersList;

        Map<String, GeneralItem> itemByName = generalItems.stream()
                .collect(Collectors.toMap(item -> item.getName().toLowerCase(), Function.identity(), (first, second) -> first));

        warehouseColumn.setCellValueFactory(data -> data.getValue().warehouseProperty());
        stockUnitsColumn.setCellValueFactory(data -> data.getValue().stockUnitsProperty().asObject());
        lowStockColumn.setCellValueFactory(data -> data.getValue().lowStockRecordsProperty().asObject());
        valueColumn.setCellValueFactory(data -> data.getValue().estimatedValueProperty().asObject());
        managersColumn.setCellValueFactory(data -> data.getValue().managersProperty());
        healthTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        healthTable.setItems(rows);

        rows.setAll(warehouses.stream()
                .map(warehouse -> buildRow(warehouse, inventory, itemByName, users))
                .toList());
        refreshChart();
    }

    private WarehouseHealthRow buildRow(Warehouse warehouse, List<Inventory> inventory, Map<String, GeneralItem> itemByName, List<User> users) {
        List<Inventory> warehouseInventory = inventory.stream()
                .filter(record -> record.getWarehouseId() == warehouse.getID())
                .toList();
        int stockUnits = warehouseInventory.stream().mapToInt(Inventory::getQuantity).sum();
        int lowStock = (int) warehouseInventory.stream()
                .filter(record -> record.getQuantity() <= DataHandler.LOW_STOCK_THRESHOLD)
                .count();
        double estimatedValue = warehouseInventory.stream()
                .mapToDouble(record -> {
                    GeneralItem item = itemByName.get(record.getName().toLowerCase());
                    return item == null ? 0 : record.getQuantity() * item.getPrice();
                })
                .sum();
        String managers = users.stream()
                .filter(user -> user.getRole() != null && "MANAGER".equals(user.getRole().name()))
                .filter(user -> user.getWarehouseIds().contains(warehouse.getID()))
                .map(User::getUsername)
                .collect(Collectors.joining(", "));

        return new WarehouseHealthRow(
                warehouse.getName(),
                stockUnits,
                lowStock,
                estimatedValue,
                managers.isBlank() ? "Unassigned" : managers
        );
    }

    private void refreshChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Stock Units");
        for (WarehouseHealthRow row : rows) {
            series.getData().add(new XYChart.Data<>(row.getWarehouse(), row.getStockUnits()));
        }
        warehouseChart.setLegendVisible(false);
        warehouseChart.getData().setAll(series);
    }

    public static class WarehouseHealthRow {
        private final SimpleStringProperty warehouse;
        private final SimpleIntegerProperty stockUnits;
        private final SimpleIntegerProperty lowStockRecords;
        private final SimpleDoubleProperty estimatedValue;
        private final SimpleStringProperty managers;

        public WarehouseHealthRow(String warehouse, int stockUnits, int lowStockRecords, double estimatedValue, String managers) {
            this.warehouse = new SimpleStringProperty(warehouse);
            this.stockUnits = new SimpleIntegerProperty(stockUnits);
            this.lowStockRecords = new SimpleIntegerProperty(lowStockRecords);
            this.estimatedValue = new SimpleDoubleProperty(estimatedValue);
            this.managers = new SimpleStringProperty(managers);
        }

        public String getWarehouse() {
            return warehouse.get();
        }

        public int getStockUnits() {
            return stockUnits.get();
        }

        public SimpleStringProperty warehouseProperty() {
            return warehouse;
        }

        public SimpleIntegerProperty stockUnitsProperty() {
            return stockUnits;
        }

        public SimpleIntegerProperty lowStockRecordsProperty() {
            return lowStockRecords;
        }

        public SimpleDoubleProperty estimatedValueProperty() {
            return estimatedValue;
        }

        public SimpleStringProperty managersProperty() {
            return managers;
        }
    }
}
