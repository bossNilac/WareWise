package com.warewise.client.controller;

import com.warewise.client.networking.DataHandler;
import com.warewise.client.util.model.Inventory;
import com.warewise.client.util.model.User;
import com.warewise.client.util.model.Warehouse;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InventoryController implements Initializable {
    @FXML private ComboBox<String> warehouseFilter;
    @FXML private TableView<InventoryRow> inventoryTable;
    @FXML private TableColumn<InventoryRow, String> itemColumn;
    @FXML private TableColumn<InventoryRow, String> warehouseColumn;
    @FXML private TableColumn<InventoryRow, Integer> quantityColumn;
    @FXML private TableColumn<InventoryRow, String> descriptionColumn;
    @FXML private TableColumn<InventoryRow, String> lastUpdatedColumn;

    private final ObservableList<InventoryRow> rows = FXCollections.observableArrayList();
    private List<Inventory> inventory = List.of();
    private Map<Integer, Warehouse> warehouseById = Map.of();
    private List<Integer> visibleWarehouseIds = List.of();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataHandler.initTables("Inventory");
        DataHandler.initTables("Warehouse");
        DataHandler.initTables("Users");

        inventory = DataHandler.parsedInventoryList == null ? List.of() : DataHandler.parsedInventoryList;
        List<Warehouse> warehouses = DataHandler.parsedWarehousesList == null ? List.of() : DataHandler.parsedWarehousesList;
        warehouseById = warehouses.stream().collect(Collectors.toMap(Warehouse::getID, Function.identity(), (first, second) -> first));

        User currentUser = DataHandler.getCurrentUser();
        visibleWarehouseIds = currentUser == null || currentUser.getWarehouseIds().isEmpty()
                ? warehouses.stream().map(Warehouse::getID).toList()
                : currentUser.getWarehouseIds();

        itemColumn.setCellValueFactory(data -> data.getValue().itemNameProperty());
        warehouseColumn.setCellValueFactory(data -> data.getValue().warehouseNameProperty());
        quantityColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        lastUpdatedColumn.setCellValueFactory(data -> data.getValue().lastUpdatedProperty());
        inventoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        inventoryTable.setItems(rows);

        warehouseFilter.setItems(FXCollections.observableArrayList(buildFilterNames()));
        warehouseFilter.setValue("All assigned warehouses");
        warehouseFilter.setOnAction(event -> refreshRows());
        refreshRows();
    }

    private List<String> buildFilterNames() {
        List<String> names = visibleWarehouseIds.stream()
                .map(this::warehouseName)
                .toList();
        ObservableList<String> result = FXCollections.observableArrayList("All assigned warehouses");
        result.addAll(names);
        return result;
    }

    private void refreshRows() {
        String selected = warehouseFilter.getValue();
        rows.setAll(inventory.stream()
                .filter(record -> visibleWarehouseIds.isEmpty() || visibleWarehouseIds.contains(record.getWarehouseId()))
                .filter(record -> selected == null
                        || "All assigned warehouses".equals(selected)
                        || warehouseName(record.getWarehouseId()).equals(selected))
                .map(record -> new InventoryRow(
                        record.getName(),
                        warehouseName(record.getWarehouseId()),
                        record.getQuantity(),
                        record.getDescription(),
                        record.getLastUpdated()
                ))
                .toList());
    }

    private String warehouseName(int warehouseId) {
        Warehouse warehouse = warehouseById.get(warehouseId);
        return warehouse == null ? "Warehouse " + warehouseId : warehouse.getName();
    }

    public static class InventoryRow {
        private final SimpleStringProperty itemName;
        private final SimpleStringProperty warehouseName;
        private final SimpleIntegerProperty quantity;
        private final SimpleStringProperty description;
        private final SimpleStringProperty lastUpdated;

        public InventoryRow(String itemName, String warehouseName, int quantity, String description, String lastUpdated) {
            this.itemName = new SimpleStringProperty(itemName);
            this.warehouseName = new SimpleStringProperty(warehouseName);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.description = new SimpleStringProperty(description);
            this.lastUpdated = new SimpleStringProperty(lastUpdated);
        }

        public SimpleStringProperty itemNameProperty() {
            return itemName;
        }

        public SimpleStringProperty warehouseNameProperty() {
            return warehouseName;
        }

        public SimpleIntegerProperty quantityProperty() {
            return quantity;
        }

        public SimpleStringProperty descriptionProperty() {
            return description;
        }

        public SimpleStringProperty lastUpdatedProperty() {
            return lastUpdated;
        }
    }
}
