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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReplenishmentController implements Initializable {
    @FXML private TableView<ReplenishmentRow> replenishmentTable;
    @FXML private TableColumn<ReplenishmentRow, String> itemColumn;
    @FXML private TableColumn<ReplenishmentRow, String> warehouseColumn;
    @FXML private TableColumn<ReplenishmentRow, Integer> quantityColumn;
    @FXML private TableColumn<ReplenishmentRow, Integer> reorderColumn;
    @FXML private TableColumn<ReplenishmentRow, String> priorityColumn;

    private final ObservableList<ReplenishmentRow> rows = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataHandler.initTables("Inventory");
        DataHandler.initTables("Warehouse");
        DataHandler.initTables("Users");

        List<Inventory> inventory = DataHandler.parsedInventoryList == null ? List.of() : DataHandler.parsedInventoryList;
        List<Warehouse> warehouses = DataHandler.parsedWarehousesList == null ? List.of() : DataHandler.parsedWarehousesList;
        Map<Integer, Warehouse> warehouseById = warehouses.stream()
                .collect(Collectors.toMap(Warehouse::getID, Function.identity(), (first, second) -> first));

        User currentUser = DataHandler.getCurrentUser();
        List<Integer> visibleWarehouseIds = currentUser == null || currentUser.getWarehouseIds().isEmpty()
                ? warehouses.stream().map(Warehouse::getID).toList()
                : currentUser.getWarehouseIds();

        itemColumn.setCellValueFactory(data -> data.getValue().itemProperty());
        warehouseColumn.setCellValueFactory(data -> data.getValue().warehouseProperty());
        quantityColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        reorderColumn.setCellValueFactory(data -> data.getValue().suggestedReorderProperty().asObject());
        priorityColumn.setCellValueFactory(data -> data.getValue().priorityProperty());

        rows.setAll(inventory.stream()
                .filter(record -> visibleWarehouseIds.isEmpty() || visibleWarehouseIds.contains(record.getWarehouseId()))
                .filter(record -> record.getQuantity() <= DataHandler.LOW_STOCK_THRESHOLD)
                .map(record -> new ReplenishmentRow(
                        record.getName(),
                        warehouseName(warehouseById, record.getWarehouseId()),
                        record.getQuantity(),
                        Math.max(0, DataHandler.LOW_STOCK_THRESHOLD * 2 - record.getQuantity()),
                        record.getQuantity() <= 3 ? "High" : "Normal"
                ))
                .toList());

        replenishmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        replenishmentTable.setItems(rows);
    }

    private String warehouseName(Map<Integer, Warehouse> warehouseById, int warehouseId) {
        Warehouse warehouse = warehouseById.get(warehouseId);
        return warehouse == null ? "Warehouse " + warehouseId : warehouse.getName();
    }

    public static class ReplenishmentRow {
        private final SimpleStringProperty item;
        private final SimpleStringProperty warehouse;
        private final SimpleIntegerProperty quantity;
        private final SimpleIntegerProperty suggestedReorder;
        private final SimpleStringProperty priority;

        public ReplenishmentRow(String item, String warehouse, int quantity, int suggestedReorder, String priority) {
            this.item = new SimpleStringProperty(item);
            this.warehouse = new SimpleStringProperty(warehouse);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.suggestedReorder = new SimpleIntegerProperty(suggestedReorder);
            this.priority = new SimpleStringProperty(priority);
        }

        public SimpleStringProperty itemProperty() {
            return item;
        }

        public SimpleStringProperty warehouseProperty() {
            return warehouse;
        }

        public SimpleIntegerProperty quantityProperty() {
            return quantity;
        }

        public SimpleIntegerProperty suggestedReorderProperty() {
            return suggestedReorder;
        }

        public SimpleStringProperty priorityProperty() {
            return priority;
        }
    }
}
