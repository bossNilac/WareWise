package com.warewise.client.controller;

import com.warewise.client.networking.ApiHandler;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.networking.ParamBuilder;
import com.warewise.client.util.AlertUtil;
import com.warewise.client.util.model.Category;
import com.warewise.client.util.model.GeneralItem;
import com.warewise.client.util.model.Inventory;
import com.warewise.client.util.model.LowStockNotification;
import com.warewise.client.util.model.StockAlert;
import com.warewise.client.util.model.Warehouse;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.warewise.client.networking.ApiHandler.PATCH;
import static com.warewise.client.networking.ApiHandler.STOCK_ALERTS;
import static com.warewise.client.util.AdminUtil.parseResponse;

public class AlertsController implements Initializable {
    @FXML private TableView<StockAlert> alertsTableView;
    @FXML private TableColumn<StockAlert, String> productColumn;
    @FXML private TableColumn<StockAlert, String> createdAtColumn;
    @FXML private TableColumn<StockAlert, Boolean> resolvedColumn;

    @FXML private TextField productFilterField;
    @FXML private TextField dateFilterField;
    @FXML private ComboBox<String> resolvedFilterCombo;

    @FXML private TableView<LowStockNotification> notificationsTable;
    @FXML private TableColumn<LowStockNotification, String> colItemName;
    @FXML private TableColumn<LowStockNotification, String> colOrderDate;
    @FXML private TableColumn<LowStockNotification, String> colUserName;
    @FXML private TableColumn<LowStockNotification, String> colCategory;
    @FXML private TableColumn<LowStockNotification, String> colInventory;
    @FXML private TableColumn<LowStockNotification, String> colWarehouse;

    private final ObservableList<LowStockNotification> notificationsData = FXCollections.observableArrayList();
    private final ObservableList<StockAlert> alertsData = FXCollections.observableArrayList();

    private List<StockAlert> masterAlerts = List.of();
    private Map<Integer, Inventory> inventoryById = Map.of();
    private Map<Integer, Warehouse> warehouseById = Map.of();

    @Override
    public void initialize(URL loc, ResourceBundle res) {
        alertsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        notificationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        resolvedFilterCombo.setItems(FXCollections.observableArrayList("All", "Yes", "No"));
        resolvedFilterCombo.setValue("All");

        productFilterField.textProperty().addListener((obs, oldValue, newValue) -> applyAlertFilters());
        dateFilterField.textProperty().addListener((obs, oldValue, newValue) -> applyAlertFilters());
        resolvedFilterCombo.valueProperty().addListener((obs, oldValue, newValue) -> applyAlertFilters());

        configureAlertTable();
        configureNotificationTable();
        loadData();
    }

    private void configureAlertTable() {
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        productColumn.setCellValueFactory(cellData -> {
            Inventory inventory = inventoryById.get(cellData.getValue().getProductID());
            return new SimpleStringProperty(inventory == null ? "Inventory " + cellData.getValue().getProductID() : inventory.getName());
        });
        resolvedColumn.setCellValueFactory(cellData -> {
            StockAlert item = cellData.getValue();
            SimpleBooleanProperty prop = new SimpleBooleanProperty(item.getResolved());
            prop.addListener((obs, oldVal, newVal) -> {
                item.setResolved(newVal);
                updateAlertBoolean(new TableColumn.CellEditEvent<>(
                        alertsTableView,
                        new TablePosition<>(alertsTableView, alertsTableView.getItems().indexOf(item), resolvedColumn),
                        TableColumn.editCommitEvent(),
                        newVal
                ));
            });
            return prop;
        });
        resolvedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(resolvedColumn));
        alertsTableView.setItems(alertsData);
    }

    private void configureNotificationTable() {
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colInventory.setCellValueFactory(new PropertyValueFactory<>("inventoryName"));
        colWarehouse.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));
        notificationsTable.setItems(notificationsData);
    }

    private void loadData() {
        DataHandler.initTables("Category");
        DataHandler.initTables("GeneralItem");
        DataHandler.initTables("Inventory");
        DataHandler.initTables("Warehouse");
        DataHandler.initTables("Alerts");

        List<Category> categories = DataHandler.parsedCategoriesList == null ? List.of() : DataHandler.parsedCategoriesList;
        List<GeneralItem> generalItems = DataHandler.parsedItemsList == null ? List.of() : DataHandler.parsedItemsList;
        List<Inventory> inventories = DataHandler.parsedInventoryList == null ? List.of() : DataHandler.parsedInventoryList;
        List<Warehouse> warehouses = DataHandler.parsedWarehousesList == null ? List.of() : DataHandler.parsedWarehousesList;
        masterAlerts = DataHandler.parsedAlertsList == null ? List.of() : DataHandler.parsedAlertsList;

        Map<Integer, Category> categoryById = categories.stream()
                .collect(Collectors.toMap(Category::getID, Function.identity(), (first, second) -> first));
        Map<String, GeneralItem> itemByName = generalItems.stream()
                .collect(Collectors.toMap(item -> item.getName().toLowerCase(), Function.identity(), (first, second) -> first));
        inventoryById = inventories.stream()
                .collect(Collectors.toMap(Inventory::getID, Function.identity(), (first, second) -> first));
        warehouseById = warehouses.stream()
                .collect(Collectors.toMap(Warehouse::getID, Function.identity(), (first, second) -> first));

        notificationsData.setAll(inventories.stream()
                .filter(inventory -> inventory.getQuantity() <= DataHandler.LOW_STOCK_THRESHOLD)
                .map(inventory -> {
                    GeneralItem item = itemByName.get(inventory.getName().toLowerCase());
                    Category category = item == null ? null : categoryById.get(item.getCategoryId());
                    return new LowStockNotification(
                            inventory.getID(),
                            inventory.getName(),
                            String.valueOf(inventory.getQuantity()),
                            inventory.getQuantity() == 0 ? "Out of stock" : "Low stock",
                            category == null ? "Uncategorized" : category.getName(),
                            inventory.getName(),
                            warehouseName(inventory.getWarehouseId())
                    );
                })
                .toList());

        applyAlertFilters();
    }

    private void applyAlertFilters() {
        String productText = productFilterField.getText() == null ? "" : productFilterField.getText().trim().toLowerCase();
        String dateText = dateFilterField.getText() == null ? "" : dateFilterField.getText().trim();
        String resolvedText = resolvedFilterCombo.getValue() == null ? "All" : resolvedFilterCombo.getValue();

        alertsData.setAll(masterAlerts.stream()
                .filter(alert -> {
                    Inventory inventory = inventoryById.get(alert.getProductID());
                    String inventoryName = inventory == null ? "" : inventory.getName().toLowerCase();
                    boolean productMatches = productText.isEmpty() || inventoryName.contains(productText);
                    boolean dateMatches = dateText.isEmpty()
                            || (alert.getCreatedAt() != null && alert.getCreatedAt().startsWith(dateText));
                    boolean resolvedMatches = "All".equals(resolvedText)
                            || ("Yes".equals(resolvedText) && alert.getResolved())
                            || ("No".equals(resolvedText) && !alert.getResolved());
                    return productMatches && dateMatches && resolvedMatches;
                })
                .toList());
    }

    private String warehouseName(int warehouseId) {
        Warehouse warehouse = warehouseById.get(warehouseId);
        return warehouse == null ? "Warehouse " + warehouseId : warehouse.getName();
    }

    private void updateAlertBoolean(TableColumn.CellEditEvent<StockAlert, Boolean> t) {
        if (AlertUtil.showYesNoAlert(AlertUtil.AlertType.MODIFY, "Alert")) {
            String params = ParamBuilder.buildParamsStockAlert(false, t.getTableView().getItems().get(
                    t.getTablePosition().getRow()));
            String unparsedResponse = ApiHandler.sendApiCall(PATCH, STOCK_ALERTS, "update_stock_alert", params);
            if (parseResponse(unparsedResponse)) {
                refreshTable();
            }
        }
    }

    public void deleteAlertAction(KeyEvent keyEvent) {
        final StockAlert selectedItem = alertsTableView.getSelectionModel().getSelectedItem();
        if (keyEvent.getCode().equals(KeyCode.DELETE) && selectedItem != null) {
            if (AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE, "Alert")) {
                ApiHandler.sendDeleteCall("DELETE_STOCK_ALERT", selectedItem.getID());
                refreshTable();
            }
        }
    }

    public void deleteAlertActionEvent(ActionEvent actionEvent) {
        final StockAlert selectedItem = alertsTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE, "Alert")) {
                ApiHandler.sendDeleteCall("DELETE_STOCK_ALERT", selectedItem.getID());
                refreshTable();
            }
        }
    }

    public void refreshTable() {
        DataHandler.initTables("Alerts");
        masterAlerts = DataHandler.parsedAlertsList == null ? List.of() : DataHandler.parsedAlertsList;
        applyAlertFilters();
    }

    public void createAlert(ActionEvent actionEvent) {
        LowStockNotification selected = notificationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        boolean alreadyOpen = masterAlerts.stream()
                .anyMatch(alert -> alert.getProductID() == selected.getInventoryId() && !alert.getResolved());
        if (alreadyOpen) {
            return;
        }

        StockAlert stockAlert = new StockAlert(selected.getInventoryId(), LocalDateTime.now().toString(), false);
        String param = ParamBuilder.buildParamsStockAlert(true, stockAlert);
        String response = ApiHandler.sendApiCall(ApiHandler.POST, ApiHandler.STOCK_ALERTS, "add_stock_alert", param);
        if (parseResponse(response)) {
            loadData();
        }
    }
}
