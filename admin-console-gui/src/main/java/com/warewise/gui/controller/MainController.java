package com.warewise.gui.controller;

import com.warewise.gui.networking.Protocol;
import com.warewise.gui.networking.WareHouseDataHandler;
import com.warewise.gui.util.AdminUtil;
import com.warewise.gui.util.EnhancedTableView;
import com.warewise.gui.util.UtilityCommands;
import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.warewise.gui.networking.WareHouseDataHandler.*;

public class MainController {

    @FXML
    private Label cpuUsageLabel;
    @FXML
    private Label conncectionsUsageLabel;
    @FXML
    private Button refreshButton;
    @FXML
    private Button endConnectionButton;
    @FXML
    private Button connectionsLabel;
    @FXML
    private Label serverActionsLabel;
    @FXML
    private Label memoryUsageLabel;
    @FXML
    private Button dashBoardTitleLabel;
    @FXML
    private Label connectedAsLabel;
    @FXML
    private Button recoverMenuButton;
    @FXML
    private VBox dashboard;
    @FXML
    private BorderPane root;
    @FXML
    private  Label usernameLabel;
    @FXML
    private  Label connNumberLabel;
    @FXML
    private  Label cpuNumberLabel;
    @FXML
    private  Label memNumberLabel;
    @FXML
    private Button startServerButton;
    @FXML
    private Button closeServerButton;
    @FXML
    private  AnchorPane dashboardPane;
    @FXML
    private TableView<Pair<String,String>> connectionTableView;
    @FXML
    private TabPane dbTablePane;
    @FXML
    private Label dbMenuLabel;
    @FXML
    private Button deleteTableButton;
    @FXML
    private Button updateTableButton;
    @FXML
    private Button addTableButton;
    @FXML
    private TableView<String[]> alertsTableView;
    @FXML
    private TableView<String[]> ordersTableView;
    @FXML
    private TableView<String[]> suppliersTableView;
    @FXML
    private TableView<String[]> itemTableView;
    @FXML
    private TableView<String[]> inventoryTableView;
    @FXML
    private TableView<String[]> categoryTableView;
    @FXML
    private TableView<String[]> usersTableView;
    @FXML
    private Button refreshTableAction;

    TableColumn<Pair<String, String>, String> nameColumn = new TableColumn<>("Username");
    TableColumn<Pair<String, String>, String> ageColumn = new TableColumn<>("IP");
    private List<Pair<String, String>> tableData ;

    private List<Node> dashboardUiElements = new ArrayList<>();
    private List<Node> connectionsUiElements = new ArrayList<>();
    private List<Node> dbUiElements = new ArrayList<>();

    private boolean isDashboardVisible = true; // Track dashboard state
    private static boolean addPressed;

    private static List<String[]> parsedUsersList;
    private static List<String[]> parsedCategoriesList;
    private static List<String[]> parsedInventoryList;
    private static List<String[]> parsedItemsList;
    private static List<String[]> parsedOrdersList;
    private static List<String[]> parsedAlertsList;
    private static List<String[]> parsedSuppliersList;

    private static EnhancedTableView usersTable;
    private static EnhancedTableView categoriesTable;
    private static EnhancedTableView inventoryTable;
    private static EnhancedTableView itemsTable;
    private static EnhancedTableView ordersTable;
    private static EnhancedTableView alertsTable;
    private static EnhancedTableView suppliersTable;


    @FXML
    public void initialize() {
        dashboardUiElements.addAll(List.of(
                serverActionsLabel, memoryUsageLabel, connectedAsLabel, usernameLabel,
                connNumberLabel, cpuNumberLabel, memNumberLabel,
                  startServerButton, closeServerButton , dashBoardTitleLabel,cpuUsageLabel,memoryUsageLabel
                ,conncectionsUsageLabel
        ));
        connectionsUiElements.addAll(List.of(refreshTableAction,
                connectionTableView,connectionsLabel,refreshButton,endConnectionButton
        ));


        dbUiElements.addAll(List.of(dbTablePane, dbMenuLabel, deleteTableButton,
                updateTableButton, addTableButton, alertsTableView,
              ordersTableView, suppliersTableView, itemTableView,
              inventoryTableView, categoryTableView, usersTableView));
        resetUi();
        toggleDashboardUI(null );

    }


    public void stopServerAction(ActionEvent actionEvent) {
        AdminUtil.closeServer(ServerApplication.getNetworkingObject());
        try {
            ServerApplication.getNetworkingObject().close();
            usernameLabel.setText("N/A");
            cpuNumberLabel.setText("N/A");
            memNumberLabel.setText("N/A");
            connNumberLabel.setText("N/A");
            UtilityCommands.displayNotificationPanel(1,"Closed server!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startServerAction(ActionEvent actionEvent) {
        File file = new File(AdminUtil.CREDENTIALS_FILE);
        AdminUtil.startServer();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ServerApplication.initNetworkingObject();
        if(UtilityCommands.isFileEmpty(file) || !file.exists()){
            String[] loginPrompt = LoginPrompt.promptLogin();
            AdminUtil.saveLoginCred(loginPrompt[0],loginPrompt[1]);
            AdminUtil.logIn(ServerApplication.getNetworkingObject(),loginPrompt[0],loginPrompt[1]);
        }else {
            String[] params = AdminUtil.getLoginCred();
            AdminUtil.logIn(ServerApplication.getNetworkingObject(),params[0],params[1]);
            setUsernameLabel(params[0]);
        }
        refreshTableAction(null);
        WareHouseDataHandler.initTables();
        usersTable= new EnhancedTableView(usersTableView, USER_COL_NO, USERS_COLUMNS, parsedUsersList);
        categoriesTable= new EnhancedTableView(categoryTableView, CATEGORY_COL_NO, CATEGORIES_COLUMNS, parsedCategoriesList);
        inventoryTable= new EnhancedTableView(inventoryTableView, INVENTORY_COL_NO, INVENTORY_COLUMNS, parsedInventoryList);
        itemsTable= new EnhancedTableView(itemTableView, ITEMS_COL_NO, ORDER_ITEMS_COLUMNS, parsedItemsList);
        ordersTable= new EnhancedTableView(ordersTableView, ORDERS_COL_NO, ORDERS_COLUMNS, parsedOrdersList);
        alertsTable= new EnhancedTableView(alertsTableView, STOCK_ALERT_COL_NO, STOCK_ALERTS_COLUMNS, parsedAlertsList);
        suppliersTable= new EnhancedTableView(suppliersTableView, SUPPLIERS_COL_NO, SUPPLIERS_COLUMNS, parsedSuppliersList);
    }

    public void menuButtonAction(ActionEvent actionEvent){
        toggleDashboard();
    }

    private void toggleDashboard() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(350), dashboard);
        FadeTransition fade = new FadeTransition(Duration.millis(250), dashboard);
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), dashboard);

        if (isDashboardVisible) {
            fade.setFromValue(1.0);
            fade.setToValue(0.0); // Smooth fading out

            slide.setToX(250); // Move to the right
            slide.setInterpolator(Interpolator.EASE_IN); // Fast at start, slow at end

            scale.setToX(0.9);
            scale.setToY(0.9); // Slight shrink effect for pop-out

            slide.setOnFinished(event -> root.setLeft(null));

            // Play animations together
            new ParallelTransition(slide, fade, scale).play();


        } else {
            root.setLeft(dashboard); // Add before animation starts

            fade.setFromValue(0.0);
            fade.setToValue(1.0); // Smooth fading in

            slide.setFromX(250);
            slide.setToX(0); // Slide back in
            slide.setInterpolator(Interpolator.EASE_OUT); // Slow at start, fast at end

            scale.setFromX(0.9);
            scale.setFromY(0.9);
            scale.setToX(1);
            scale.setToY(1); // Pop-in effect

            new ParallelTransition(slide, fade, scale).play();
        }
        recoverMenuButton.setVisible(isDashboardVisible);
        isDashboardVisible = !isDashboardVisible;
    }

    public  void setCpuNumberLabel(String text) {
         cpuNumberLabel.setText(text);
    }

    public  void setConnNumberLabel(String text) {
         connNumberLabel.setText(text);
    }

    public  void setUsernameLabel(String text) {
         usernameLabel.setText(text);
    }

    public  void setMemNumberLabel(String text) {
        memNumberLabel.setText(text);
    }

    public  boolean isDashBoardVisible(){
        return dashboardPane.isVisible();
    }

    public void exitAction(){
        System.exit(0);
    }

    public void logOutAction(){
        AdminUtil.logOut(ServerApplication.getNetworkingObject());
        usernameLabel.setText("N/A");
        cpuNumberLabel.setText("N/A");
        memNumberLabel.setText("N/A");
        connNumberLabel.setText("N/A");
        try {
            ServerApplication.getNetworkingObject().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UtilityCommands.displayNotificationPanel(1,"Logged out!");
    }

    public void refreshTableAction(ActionEvent actionEvent){
        ServerApplication.getNetworkingObject().sendMessage(Protocol.HEARTBEAT);
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(tableData);

        connectionTableView.getColumns().remove(nameColumn);
        connectionTableView.getColumns().remove(ageColumn);
        connectionTableView.getColumns().addAll(nameColumn, ageColumn);

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        ageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue()));

        ObservableList<Pair<String, String>> data = FXCollections.observableArrayList(tableData);
        connectionTableView.getItems().setAll(data);
        connectionTableView.refresh();
    }

    public void endConnectionAction(ActionEvent actionEvent){
        Pair<String, String> selectedRow = connectionTableView.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            AdminUtil.kickUser(ServerApplication.getNetworkingObject(),selectedRow.getKey());
            refreshTableAction(null);
        }
    }

    public void toggleDashboardUI(ActionEvent actionEvent){
        resetUi();
        dashboardUiElements.forEach(node -> node.setVisible(true));
    }

    public void toggleConnectionsDashboardUI(ActionEvent event){
        resetUi();
        connectionsUiElements.forEach(node -> node.setVisible(true));
    }

    public void toggleDbUI(ActionEvent event){
        resetUi();
        dbUiElements.forEach(node -> node.setVisible(true));
    }

    private void resetUi(){
        dashboardUiElements.forEach(node -> node.setVisible(false));
        connectionsUiElements.forEach(node -> node.setVisible(false));
        dbUiElements.forEach(node -> node.setVisible(false));
    }

    public void loadConnectionsTableData(String output){
        List<Pair<String, String>> parsedConnections = new ArrayList<>();
        String[] parts = output.replaceAll("/","").split(Protocol.SEPARATOR);
        for (int i = 1; i < parts.length; i += 2) {
            if (i + 1 < parts.length) {
                parsedConnections.add(new Pair<>(parts[i+1], parts[i])); // IP -> Username
            }
        }
        setConnectionTableData(parsedConnections);
    }

    public void setConnectionTableData(List<Pair<String, String>> tableData) {
        this.tableData = tableData;
    }

    public void addToTableAction(ActionEvent actionEvent){
        addPressed = true;
        switch (getCurrentTabName()){
            case"Users":
                usersTable.addEmptyRowForEditing();
                break;
            case"Category":
                categoriesTable.addEmptyRowForEditing();
                break;
            case"Inventory":
                inventoryTable.addEmptyRowForEditing();
                break;
            case"Item":
                itemsTable.addEmptyRowForEditing();
                break;
            case"Suppliers":
                suppliersTable.addEmptyRowForEditing();
                break;
            case"Orders":
                ordersTable.addEmptyRowForEditing();
                break;
            case"Alerts":
                alertsTable.addEmptyRowForEditing();
                break;
            default:break;
        }
    }
    public void updateToTableAction(ActionEvent actionEvent) {
        String[] data = null;
        String header = null;
        if(addPressed){
            switch (getCurrentTabName()) {
                case "Users":
                    header = Protocol.ADD_USER;
                    data = usersTable.commitEditingRow();
                    usersTable.updateSelectedRow(data);
                    break;
                case "Category":
                    header = Protocol.ADD_CATEGORY;
                    data = categoriesTable.commitEditingRow();
                    categoriesTable.updateSelectedRow(data);
                    break;
                case "Inventory":
                    header = Protocol.ADD_INVENTORY;
                    data = inventoryTable.commitEditingRow();
                    inventoryTable.updateSelectedRow(data);
                    break;
                case "Item":
                    header = Protocol.ADD_ITEM;
                    data= itemsTable.commitEditingRow();
                    itemsTable.updateSelectedRow(data);
                    break;
                case "Suppliers":
                    header = Protocol.ADD_SUPPLIER;
                    data = suppliersTable.commitEditingRow();
                    suppliersTable.updateSelectedRow(data);
                    break;
                case "Orders":
                    header = Protocol.CREATE_ORDER;
                    data = ordersTable.commitEditingRow();
                    ordersTable.updateSelectedRow(data);
                    break;
                case "Alerts":
                    header = Protocol.STOCK_ALERT;
                    data = alertsTable.commitEditingRow();
                    alertsTable.updateSelectedRow(data);
                    break;
                default:
                    break;
            }
        }else {
            switch (getCurrentTabName()) {
                case "Users":
                    header = Protocol.UPDATE_USER;
                    data = usersTable.commitEditingRow();
                    usersTable.updateSelectedRow(data);
                    break;
                case "Category":
                    header = Protocol.UPDATE_CATEGORY;
                    data = categoriesTable.commitEditingRow();
                    categoriesTable.updateSelectedRow(data);
                    break;
                case "Inventory":
                    header = Protocol.UPDATE_INVENTORY;
                    data = inventoryTable.commitEditingRow();
                    inventoryTable.updateSelectedRow(data);
                    break;
                case "Item":
                    header = Protocol.UPDATE_ITEM;
                    data = itemsTable.commitEditingRow();
                    itemsTable.updateSelectedRow(data);
                    break;
                case "Suppliers":
                    header = Protocol.UPDATE_SUPPLIER;
                    data = suppliersTable.commitEditingRow();
                    suppliersTable.updateSelectedRow(data);
                    break;
                case "Orders":
                    header = Protocol.UPDATE_ORDER;
                    data = ordersTable.commitEditingRow();
                    ordersTable.updateSelectedRow(data);
                    break;
                case "Alerts":
                    header = Protocol.UPDATE_STOCK_ALERT;
                    data = alertsTable.commitEditingRow();
                    alertsTable.updateSelectedRow(data);
                    break;
                default:
                    break;
            }
        }
        if(data != null && header != null){
            WareHouseDataHandler.parseAndSendToServer(header,data);
        }
    }
    public void deleteToTableAction(ActionEvent actionEvent){
        String[] data = null;
        String header = null;
        switch (getCurrentTabName()) {
            case "Users":
                header = Protocol.DELETE_USER;
                data = usersTable.deleteSelectedRow();
                break;
            case "Category":
                header = Protocol.DELETE_CATEGORY;
                data = categoriesTable.deleteSelectedRow();
                break;
            case "Inventory":
                header = Protocol.DELETE_INVENTORY;
                data = inventoryTable.deleteSelectedRow();
                break;
            case "Item":
                header = Protocol.DELETE_ITEM;
                data= itemsTable.deleteSelectedRow();
                break;
            case "Suppliers":
                header = Protocol.DELETE_SUPPLIER;
                data = suppliersTable.deleteSelectedRow();
                break;
            case "Orders":
                header = Protocol.DELETE_ORDER;
                data = ordersTable.deleteSelectedRow();
                break;
            case "Alerts":
                header = Protocol.DELETE_STOCK_ALERT;
                data = alertsTable.deleteSelectedRow();
                break;
            default:
                break;
        }
        if(data != null && header != null){
            WareHouseDataHandler.parseAndSendToServer(header,data);
        }
        addPressed = false;
    }

    public void refreshToTableAction(ActionEvent actionEvent){
        WareHouseDataHandler.initTables();
        ordersTable.refresh();
        usersTable.refresh();
        categoriesTable.refresh();
        alertsTable.refresh();
        itemsTable.refresh();
        suppliersTable.refresh();
        inventoryTable.refresh();
    }


    public String getCurrentTabName() {
        Tab selectedTab = dbTablePane.getSelectionModel().getSelectedItem();

        if (selectedTab != null) {
            return selectedTab.getText();  // Return the name of the currently selected tab
        } else {
            return "No tab selected";  // In case no tab is selected
        }
    }

    public static void setParsedSuppliersList(List<String[]> data) {
        parsedSuppliersList = data;
    }
    public static void setParsedAlertsList(List<String[]> data) {
        parsedAlertsList = data;
    }
    public static void setParsedOrdersList(List<String[]> data) {
        parsedOrdersList = data;
    }
    public static void setParsedItemsList(List<String[]> data) {
        parsedItemsList = data;
    }
    public static void setParsedInventoryList(List<String[]> data) {
        parsedInventoryList = data;
    }
    public static void setParsedCategoriesList(List<String[]> data) {
        parsedCategoriesList = data;
    }
    public static void setParsedUsersList(List<String[]> data) {
        parsedUsersList = data;
    }


}