package com.warewise.client.controller;

import com.warewise.client.networking.ApiHandler;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.networking.ParamBuilder;
import com.warewise.client.util.AdminUtil;
import com.warewise.client.util.model.Inventory;
import com.warewise.client.util.model.Warehouse;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AdjustDamageController implements Initializable {
    @FXML private ComboBox<InventoryChoice> inventoryCombo;
    @FXML private ComboBox<String> adjustmentTypeCombo;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private TextArea reasonArea;

    private Map<Integer, Warehouse> warehouseById = Map.of();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adjustmentTypeCombo.setItems(FXCollections.observableArrayList("Damage", "Shrinkage", "Correction"));
        adjustmentTypeCombo.getSelectionModel().selectFirst();
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1_000_000, 1));
        loadData();
    }

    @FXML
    private void applyAdjustmentAction(ActionEvent event) {
        InventoryChoice choice = inventoryCombo.getValue();
        if (choice == null) {
            showError("Select inventory first.");
            return;
        }

        Inventory inventory = choice.inventory();
        int quantity = quantitySpinner.getValue();
        int newQuantity = Math.max(0, inventory.getQuantity() - quantity);
        String reason = reasonArea.getText() == null || reasonArea.getText().isBlank()
                ? adjustmentTypeCombo.getValue()
                : adjustmentTypeCombo.getValue() + ": " + reasonArea.getText().trim();

        inventory.setQuantity(newQuantity);
        inventory.setLastUpdated(LocalDateTime.now().toString());
        inventory.setDescription(inventory.getDescription() + " | Adjusted: " + reason);

        String response = ApiHandler.sendApiCall(ApiHandler.PATCH, ApiHandler.INVENTORIES,
                "update_inventory", ParamBuilder.buildParamsInventory(false, inventory));
        if (AdminUtil.parseResponse(response)) {
            loadData();
            reasonArea.clear();
        }
    }

    @FXML
    private void refreshAction(ActionEvent event) {
        loadData();
    }

    private void loadData() {
        DataHandler.initTables("Inventory");
        DataHandler.initTables("Warehouse");
        List<Inventory> inventories = DataHandler.parsedInventoryList == null ? List.of() : DataHandler.parsedInventoryList;
        List<Warehouse> warehouses = DataHandler.parsedWarehousesList == null ? List.of() : DataHandler.parsedWarehousesList;
        warehouseById = warehouses.stream()
                .collect(Collectors.toMap(Warehouse::getID, Function.identity(), (first, second) -> first));

        inventoryCombo.setItems(FXCollections.observableArrayList(inventories.stream()
                .map(inventory -> new InventoryChoice(inventory, warehouseName(inventory.getWarehouseId())))
                .toList()));
        if (!inventoryCombo.getItems().isEmpty()) {
            inventoryCombo.getSelectionModel().selectFirst();
        }
    }

    private String warehouseName(int warehouseId) {
        Warehouse warehouse = warehouseById.get(warehouseId);
        return warehouse == null ? "Warehouse " + warehouseId : warehouse.getName();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    private record InventoryChoice(Inventory inventory, String warehouseName) {
        @Override
        public String toString() {
            return inventory.getName() + " | " + warehouseName + " | Qty " + inventory.getQuantity();
        }
    }
}
