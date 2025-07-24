package com.warewise.client.controller;

import com.warewise.client.networking.ApiHandler;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.networking.ParamBuilder;
import com.warewise.client.util.AlertUtil;
import com.warewise.client.util.enums.OrderStatus;
import com.warewise.client.util.form.OrderForm;
import com.warewise.client.util.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.warewise.client.networking.ApiHandler.*;
import static com.warewise.client.util.AdminUtil.parseResponse;

public class OrdersController {
    @FXML
    private TableView<Order> ordersTableView;
    @FXML
    private TableColumn<Order, OrderStatus> statusColumn;
    @FXML
    private TableColumn<Order,String> createdAtColumn;
    @FXML
    private TableColumn<Order,String> updatedAtColumn;
    @FXML
    private TableColumn<Order,String> userColumn;
    @FXML
    private TableColumn<Order,String> itemColumn;
    @FXML
    private TableColumn<Order,Integer> quantityColumn;

    private final ObservableList<Order> generalOrderData = FXCollections.observableArrayList();

    private Map<Integer, GeneralItem> idToGeneralItem;
    private Map<String,  GeneralItem>  nameToGeneralItem;
    private ObservableList<String>  generalItemNames;

    @FXML
    private TextField dateFilterField;
    @FXML
    private Button dateFilterBtn;
    @FXML
    private TextField      userFilterField;
    @FXML
    private Button         userFilterBtn;
    @FXML
    private ComboBox<OrderStatus> statusFilterCombo;




    @FXML
    private void initialize(){

        DataHandler.initTables("Orders");
        DataHandler.initTables("Users");
        DataHandler.initTables("GeneralItem");

        List<User> users = DataHandler.parsedUsersList;
        Map<Integer,User> idToUser = users.stream()
                .collect(Collectors.toMap(User::getID, u->u));



        ObservableList<OrderStatus> statuses = FXCollections.observableArrayList(OrderStatus.values());
        statusFilterCombo.setItems(statuses);

        // a single method to reapply all filters
        Runnable applyFilters = () -> {
            String dateText = dateFilterField.getText().trim();
            String userText = userFilterField.getText().trim().toLowerCase();
            OrderStatus statusSel = statusFilterCombo.getValue();

            List<Order> filtered = DataHandler.parsedOrdersList.stream()
                    .filter(o -> {
                        // 1) date filter
                        boolean okDate = dateText.isEmpty()
                                || o.getCreatedAt().startsWith(dateText);

                        // 2) user filter
                        boolean okUser = userText.isEmpty() || Optional.ofNullable(idToUser.get(o.getUserId()))
                                .map(u -> u.getUsername().toLowerCase().contains(userText))
                                .orElse(false);

                        // 3) status filter
                        boolean okStatus = (statusSel == null)
                                || o.getStatus() == statusSel;

                        return okDate && okUser && okStatus;
                    })
                    .collect(Collectors.toList());

            ordersTableView.setItems(FXCollections.observableArrayList(filtered));
        };

        // wire all three controls to use the same applyFilters()
        dateFilterBtn .setOnAction(e -> applyFilters.run());
        userFilterBtn .setOnAction(e -> applyFilters.run());
        statusFilterCombo.valueProperty()
                .addListener((obs, old, nw) -> applyFilters.run());

        // initial load
        applyFilters.run();

        ordersTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        idToGeneralItem      = DataHandler.parsedItemsList.stream()
                .collect(Collectors.toMap(GeneralItem::getId, c -> c));
        nameToGeneralItem    = DataHandler.parsedItemsList.stream()
                .collect(Collectors.toMap(GeneralItem::getName, c -> c));
        generalItemNames     = FXCollections.observableArrayList(nameToGeneralItem.keySet());



        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                new StringConverter<>() {
                    @Override
                    public String toString(OrderStatus status) {
                        return status == null ? "" : status.name();
                    }
                    @Override
                    public OrderStatus fromString(String string) {
                        try {
                            return OrderStatus.valueOf(string);
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    }
                },
                statuses
        ));

        statusColumn.setOnEditCommit(event -> {
            Order order = event.getRowValue();
            OrderStatus newStatus = event.getNewValue();
            if (newStatus != null) {
                order.setStatus(newStatus);
                updateOrder(event);     // your DAO/persistence call
                refreshTable();
            }
        });

        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));



        // 1) Only set the cellValueFactory to show username:
        userColumn.setCellValueFactory(cellData -> {
            int uid    = cellData.getValue().getUserId();
            String name = idToUser.containsKey(uid)
                    ? idToUser.get(uid).getUsername()
                    : "";
            return new SimpleStringProperty(name);
        });

        itemColumn.setCellValueFactory(cellData -> {
            int suppId = cellData.getValue().getGeneralItemId();
            String name = idToGeneralItem.containsKey(suppId)
                    ? idToGeneralItem.get(suppId).getName()
                    : "";
            return new SimpleStringProperty(name);
        });
        itemColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                new DefaultStringConverter(), generalItemNames
        ));

        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new IntegerStringConverter())
        );

        statusColumn.setEditable(true);
        createdAtColumn.setEditable(false);
        updatedAtColumn.setEditable(false);
        userColumn.setEditable(false);
        itemColumn.setEditable(false);
        quantityColumn.setEditable(false);

        generalOrderData.addAll(DataHandler.parsedOrdersList);
        ordersTableView.setItems(generalOrderData);


    }

    public void deleteOrderAction(KeyEvent keyEvent) {
        final Order selectedItem = ordersTableView.getSelectionModel().getSelectedItem();
        if(keyEvent.getCode().equals(KeyCode.DELETE) && selectedItem !=null){
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Order")) {
                ApiHandler.sendDeleteCall("DELETE_ORDER",selectedItem.getID());
                refreshTable();
            }
        }
    }

    public void addNewOrderAction(ActionEvent actionEvent) {
        Order selectedItem = new OrderForm().showAndWait();
        if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.CREATE,"Order")) {
            String params = ParamBuilder.buildParamsOrder(true,selectedItem);
            String unparsedResponse = ApiHandler.sendApiCall(POST,ORDERS,"add_order",params);
            parseResponse(unparsedResponse);
            refreshTable();
        }
    }

    public void deleteOrderActionEvent(ActionEvent actionEvent) {
        final Order selectedItem = ordersTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Order")) {
                ApiHandler.sendDeleteCall("DELETE_ORDER",selectedItem.getID());
                refreshTable();
            }
        }
    }

    private void updateOrder(TableColumn.CellEditEvent<Order, OrderStatus> t) {
        if (AlertUtil.showYesNoAlert(AlertUtil.AlertType.MODIFY, "Order")) {
            Order order = t.getTableView().getItems().get(
                    t.getTablePosition().getRow());
            order.setUpdatedAt(LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            String params = ParamBuilder.buildParamsOrder(false, order);
            System.out.println(params);
            String unparsedResponse = ApiHandler.sendApiCall(PATCH, ORDERS, "update_order", params);
            if (parseResponse(unparsedResponse)) {
                refreshTable();
            }
        }
    }

    public void refreshTable() {
        DataHandler.initTables("Orders");
        generalOrderData.setAll(DataHandler.parsedOrdersList);
    }

    public void resetFilters(ActionEvent actionEvent) {
          dateFilterField.setText("");
          userFilterField.setText("");
          statusFilterCombo.setValue(null);
          refreshTable();
    }
}
