package com.warewise.client.controller;

import com.warewise.client.networking.ApiHandler;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.networking.ParamBuilder;
import com.warewise.client.util.AlertUtil;
import com.warewise.client.util.model.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.DefaultStringConverter;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.warewise.client.networking.ApiHandler.*;
import static com.warewise.client.networking.ApiHandler.GENERAL_ITEMS;
import static com.warewise.client.util.AdminUtil.parseResponse;

public class AlertsController implements Initializable {
    @FXML
    private ListView<String> notificationsList;
    @FXML
    private TableView<StockAlert> alertsTableView;

    @FXML
    private TableColumn<StockAlert, String> productColumn;
    @FXML
    private TableColumn<StockAlert, String> createdAtColumn;
    @FXML
    private TableColumn<StockAlert, Boolean> resolvedColumn;

    @FXML
    private TextField      productFilterField;
    @FXML
    private TextField      dateFilterField;
    @FXML
    private ComboBox<String> resolvedFilterCombo;


    ObservableList<String> productNameList;

    private int LOW_STOCK_THRESHOLD = 10;
    Map<String, GeneralItem> giNameMap;

    private final ObservableList<StockAlert> alertsData = FXCollections.observableArrayList();


    @Override
    public void initialize(URL loc, ResourceBundle res) {

        alertsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 1) Load all relevant tables
        DataHandler.initTables("Category");
        DataHandler.initTables("GeneralItem");
        DataHandler.initTables("Inventory");
        DataHandler.initTables("WarehouseItem");
        DataHandler.initTables("Warehouse");
        DataHandler.initTables("Orders");
        DataHandler.initTables("Users");
        DataHandler.initTables("Alerts");


        List<Category> categories = DataHandler.parsedCategoriesList;
        List<GeneralItem> generalItems = DataHandler.parsedItemsList;
        List<Inventory> stockRecords = DataHandler.parsedInventoryList;
        List<WarehouseItem> orderItems = DataHandler.parsedWarehouseItemsList;
        List<Order> orders = DataHandler.parsedOrdersList;
        List<User> users = DataHandler.parsedUsersList;
        List<Warehouse> warehouses = DataHandler.parsedWarehousesList;

        ObservableList<String> notifications = FXCollections.observableArrayList();


        Map<Integer, User> idToUser = users.stream()
                .collect(Collectors.toMap(User::getID, u -> u));
        Map<Integer, GeneralItem> giMap = generalItems.stream()
                .collect(Collectors.toMap(GeneralItem::getId, gi -> gi));

        giNameMap = generalItems.stream()
                .collect(Collectors.toMap(GeneralItem::getName, gi -> gi));

        productNameList = FXCollections.observableArrayList(giNameMap.keySet());

        Map<Integer, Order> orderMap = orders.stream()
                .collect(Collectors.toMap(Order::getID, gi -> gi));

        Map<Integer, Category> categoriesMap = categories.stream()
                .collect(Collectors.toMap(Category::getID, gi -> gi));

        Map<Integer, Inventory> inventoriesMap = stockRecords.stream()
                .collect(Collectors.toMap(Inventory::getID, gi -> gi));

        Map<Integer, Warehouse> warehousesMap = warehouses.stream()
                .collect(Collectors.toMap(Warehouse::getID, gi -> gi));

        List<StockAlert> masterAlerts = DataHandler.parsedAlertsList;

        resolvedFilterCombo.setItems(FXCollections.observableArrayList("All", "Yes", "No"));
        resolvedFilterCombo.setValue("All");

        Runnable applyFilters = () -> {
            String prodText = productFilterField.getText().trim().toLowerCase();
            String dateText = dateFilterField.getText().trim();
            String resText  = resolvedFilterCombo.getValue();

            List<StockAlert> filtered = masterAlerts.stream()
                    .filter(a -> {
                        // 1) product name filter (lookup via your productColumn logic):
                        GeneralItem gi = giNameMap.get(productColumn.getCellData(a));
                        String name = gi != null ? gi.getName().toLowerCase() : "";
                        boolean okProd = prodText.isEmpty() || name.contains(prodText);

                        // 2) date filter
                        boolean okDate = dateText.isEmpty()
                                || a.getCreatedAt().startsWith(dateText);

                        // 3) resolved filter
                        boolean okRes = resText.equals("All")
                                || (resText.equals("Yes") && a.getResolved())
                                || (resText.equals("No")  && !a.getResolved());

                        return okProd && okDate && okRes;
                    })
                    .collect(Collectors.toList());

            alertsData.setAll(filtered);
            alertsTableView.setItems(alertsData);
        };

        productFilterField.textProperty()
                .addListener((obs,old,nw) -> applyFilters.run());
        dateFilterField.textProperty()
                .addListener((obs,old,nw) -> applyFilters.run());
        resolvedFilterCombo.valueProperty()
                .addListener((obs,old,nw) -> applyFilters.run());

// initial load with no filters
        applyFilters.run();




        // Bind table columns to Category properties
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        resolvedColumn.setCellValueFactory(new PropertyValueFactory<>("resolved"));

        resolvedColumn.setCellValueFactory(cellData -> {
            StockAlert item = cellData.getValue();
            SimpleBooleanProperty prop = new SimpleBooleanProperty(item.getResolved());
            prop.addListener((obs, oldVal, newVal) -> {
                item.setResolved(newVal);
                // you can call your boolean update helper directly here
                updateAlertBoolean(new TableColumn.CellEditEvent<>(
                        alertsTableView,
                        new TablePosition<>(alertsTableView,
                                alertsTableView.getItems().indexOf(item),
                                resolvedColumn),
                        TableColumn.editCommitEvent(),
                        newVal
                ));
            });
            return prop;
        });
        resolvedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(resolvedColumn));

        productColumn.setCellValueFactory(cellData -> {
            int id = cellData.getValue().getProductID();
            String name = giMap.containsKey(id)
                    ? giMap.get(id).getName()
                    : "";
            return new SimpleStringProperty(name);
        });
        productColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                new DefaultStringConverter(), productNameList
        ));

        alertsData.setAll(DataHandler.parsedAlertsList);
        alertsTableView.setItems(alertsData);


        for (WarehouseItem oi : orderItems) {
            if (oi.getQuantity() <= LOW_STOCK_THRESHOLD) {

                GeneralItem gi = giMap.get(oi.getGeneralItemId());
                Order order = orderMap.get(oi.getOrderID());
                User user = idToUser.get(order.getUserId());
                Category category = categoriesMap.get(gi.getCategoryId());
                Inventory inventory = inventoriesMap.get(oi.getInventoryID());
                Warehouse warehouse = warehousesMap.get(inventory.getWarehouseId());

                String itemName = gi != null ? gi.getName() : "Unknown item";
                String orderDate = order != null ? order.getUpdatedAt() : "No order date";
                String userName = user != null ? user.getUsername() : "No user on order";
                String categoryName = category != null ? category.getName() : "No category on Item";
                String inventoryName = inventory != null ? inventory.getName() : "No inventory for Item";
                String warehousesName = warehouse != null ? warehouse.getName() : "No warehouse for Item";

                String text = "Low stock alert: “" + itemName +
                        "“ has only " + oi.getQuantity() + " left"
                        + " , received on " + orderDate
                        + " , done by " + userName
                        + " , of category " + categoryName
                        + " , of inventory " + inventoryName
                        + " , of warehouse " + warehousesName;
                notifications.add(
                        text
                );
            }
        }
// 3) Finally wire it up
        notificationsList.setItems(notifications);

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
        if(keyEvent.getCode().equals(KeyCode.DELETE) && selectedItem !=null){
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Alert")) {
                ApiHandler.sendDeleteCall("DELETE_STOCK_ALERT",selectedItem.getID());
                refreshTable();
            }
        }
    }


    public void deleteAlertActionEvent(ActionEvent actionEvent){
        final StockAlert selectedItem = alertsTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Alert")) {
                ApiHandler.sendDeleteCall("DELETE_STOCK_ALERT",selectedItem.getID());
                refreshTable();
            }
        }
    }

    public void refreshTable() {
        DataHandler.initTables("Alerts");
        alertsData.setAll(DataHandler.parsedAlertsList);
    }


    public void createAlert(ActionEvent actionEvent) {
        String selectedString = notificationsList.getSelectionModel().getSelectedItem();

        Pattern p = Pattern.compile("Low stock alert: “(.+?)“ has only");
        Matcher m = p.matcher(selectedString);

        if (m.find()) {
            String parsedItemName = m.group(1);
            System.out.println("Extracted itemName = " + parsedItemName);

            GeneralItem item = giNameMap.get(parsedItemName);

            StockAlert stockAlert = new StockAlert(item.getId(), LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), false);

            String param = ParamBuilder.buildParamsStockAlert(true, stockAlert);
            ApiHandler.sendApiCall(POST, STOCK_ALERTS, "add_stock_alert", param);
        }
    }
}
