package com.warewise.gui.controller;

import com.warewise.gui.networking.ApiHandler;
import com.warewise.gui.networking.ApiResponse;
import com.warewise.gui.networking.ParamBuilder;
import com.warewise.gui.networking.WareHouseDataHandler;
import com.warewise.gui.util.*;
import com.warewise.gui.util.model.*;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.warewise.gui.util.AdminUtil.*;

public class MainController {
    @FXML
    private Label usersTextLabel;
    @FXML
    private Label numberOfUsers;
    @FXML
    private Label warehousesTextLabel;
    @FXML
    private Label numberOfWarehouses;
    @FXML
    private Label usernameLabel;
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
    private  AnchorPane dashboardPane;
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
    private TableView<StockAlert> alertsTableView;
    @FXML
    private TableView<Order> ordersTableView;
    @FXML
    private TableView<Supplier> suppliersTableView;
    @FXML
    private TableView<GeneralItem> itemTableView;
    @FXML
    private TableView<Inventory> inventoryTableView;
    @FXML
    private TableView<Category> categoryTableView;
    @FXML
    private TableView<User> usersTableView;
    @FXML
    private TableView<Warehouse> warehouseTableView;
    @FXML
    private TableView<Log> logsTableView;
    @FXML
    private Button refreshDbTableButton;
    @FXML
    private Label settingsLabel;
    @FXML
    private CheckBox mode1CheckBox;
    @FXML
    private CheckBox mode2CheckBox;
    @FXML
    private CheckBox mode3CheckBox;
    @FXML
    private Label settingsLabel1;
    @FXML
    private Button linkdinButton;
    @FXML
    private Button gitHubButton;
    @FXML
    private Button saveSettingsButton;


    private final List<Node> dashboardUiElements = new ArrayList<>();
    private final List<Node> dbUiElements = new ArrayList<>();
    private final List<Node> settingsUiElements = new ArrayList<>();

    private boolean isDashboardVisible = true; // Track dashboard state
    private boolean areAllTableInit = false; // Track init of db table state
    private static boolean addPressed=false;
    private boolean isServerOn = false;
    private boolean isLogin = false;
    private int  checkedCount = 0;

    private static EnhancedTableView usersTable;
    private static EnhancedTableView categoriesTable;
    private static EnhancedTableView inventoryTable;
    private static EnhancedTableView itemsTable;
    private static EnhancedTableView ordersTable;
    private static EnhancedTableView alertsTable;
    private static EnhancedTableView suppliersTable;
    private static EnhancedTableView warehouseTable;
    private static EnhancedTableView logsTable;
    private CheckBox[] checkBoxes = new CheckBox[3];

    @FXML
    public void initialize() {
        dashboardPane.setMaxWidth(Double.MAX_VALUE);
        dashboardPane.setMaxHeight(Double.MAX_VALUE);
        recoverMenuButton.toFront();

        dashboardUiElements.addAll(List.of(
               connectedAsLabel, usernameLabel
                 , dashBoardTitleLabel,numberOfUsers,warehousesTextLabel,numberOfWarehouses,usersTextLabel
        ));

        dbUiElements.addAll(List.of(dbTablePane, dbMenuLabel, deleteTableButton,
                updateTableButton, addTableButton, alertsTableView,
              ordersTableView, suppliersTableView, itemTableView,
              inventoryTableView, categoryTableView, usersTableView,refreshDbTableButton,warehouseTableView,logsTableView));
        settingsUiElements.addAll(List.of(settingsLabel,
                mode1CheckBox,mode2CheckBox,mode3CheckBox,
                settingsLabel1,linkdinButton,gitHubButton,saveSettingsButton));
        resetUi();
        toggleDashboardUI(null );
        isServerOn = UtilityCommands.pingServer();
        switch (PropertiesReader.getActiveIndex(ServerApplication.settings)){
            case 1:break;
            case 0:
                promptLogin();
                break;
            case 2:
                doLogin();
                usernameLabel.setText(sessionUsername);
                break;
            default:UtilityCommands.displayNotificationPanel(3,"Cannot run app;");System.exit(0);
        }
        checkBoxes[0]=mode1CheckBox;
        checkBoxes[1]=mode2CheckBox;
        checkBoxes[2]=mode3CheckBox;
        for(int i = 0 ; i< checkBoxes.length;i++){
            checkBoxes[i].setSelected(ServerApplication.settings[i]);
        }

        if(loggedIn){
            boolean flag = checkIfAdmin();
            while(!flag){
                promptLogin();
                flag = checkIfAdmin();
            }
            refreshToTableAction(null);
        }

    }

    public void menuButtonAction(ActionEvent actionEvent){
        toggleDashboard();
    }

    private void toggleDashboard() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(350), dashboard);
        FadeTransition fade = new FadeTransition(Duration.millis(250), dashboard);

        if (isDashboardVisible) {
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            slide.setFromX(0);
            slide.setToX(-dashboard.getWidth());
            slide.setInterpolator(Interpolator.EASE_IN);

            slide.setOnFinished(event -> {
                root.setLeft(null);
                dashboard.setTranslateX(0);
                dashboard.setOpacity(1.0);
                recoverMenuButton.setVisible(true);
                recoverMenuButton.toFront();
                isDashboardVisible = false;
                root.requestLayout();
            });

            new ParallelTransition(slide, fade).play();

        } else {
            recoverMenuButton.setVisible(false);
            dashboard.setTranslateX(-dashboard.getWidth());
            dashboard.setOpacity(0.0);
            root.setLeft(dashboard);
            root.requestLayout();

            fade.setFromValue(0.0);
            fade.setToValue(1.0);

            slide.setFromX(-dashboard.getWidth());
            slide.setToX(0);
            slide.setInterpolator(Interpolator.EASE_OUT);

            slide.setOnFinished(event -> {
                dashboard.setTranslateX(0);
                dashboard.setOpacity(1.0);
                isDashboardVisible = true;
                root.requestLayout();
            });

            new ParallelTransition(slide, fade).play();
        }
    }


    public  void setUsernameLabel(String text) {
         usernameLabel.setText(text);
    }


    public  boolean isDashBoardVisible(){
        return dashboardPane.isVisible();
    }

    public void exitAction(){
        if(UtilityCommands.displayWarning("Are you sure you want to exit?",true)) System.exit(0);
    }

    public void logOutAction(){
        if(isServerOn){
            if(UtilityCommands.displayWarning("Are you sure you want to log out?",true)) {
                AdminUtil.logOut();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                usernameLabel.setText("N/A");
                areAllTableInit = false;
                UtilityCommands.displayNotificationPanel(1, "Logged out!");
                isLogin = false;
            }
        }else{
            UtilityCommands.displayWarning("Server is not running!",false);
        }
    }

    public void toggleDashboardUI(ActionEvent actionEvent){
        resetUi();
        dashboardUiElements.forEach(node -> node.setVisible(true));
    }

    public void toggleDbUI(ActionEvent event){
        resetUi();
        dbUiElements.forEach(node -> node.setVisible(true));
    }

    private void resetUi(){
        dashboardUiElements.forEach(node -> node.setVisible(false));
        dbUiElements.forEach(node -> node.setVisible(false));
        settingsUiElements.forEach(node -> node.setVisible(false));
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
            case"General Items":
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
            case"Warehouse":
                warehouseTable.addEmptyRowForEditing();
                break;
            default:break;
        }
    }

    public void updateToTableAction(ActionEvent actionEvent) {
        String body;
        String method   = addPressed ? "POST" : "PATCH";
        String endpoint;
        String action;
        Object model;

        // 1) Determine model, endpoint, action & request body
        switch (getCurrentTabName()) {
            case "Users": {
                User user = (User) usersTable.commitEditingRow();
                model    = user;
                endpoint = ApiHandler.USERS;
                action   = addPressed ? "add_user" : "update_user";
                body     = ParamBuilder.buildParamsUser(addPressed, user);
                break;
            }
            case "Category": {
                Category category = (Category) categoriesTable.commitEditingRow();
                model      = category;
                endpoint   = ApiHandler.CATEGORIES;
                action     = addPressed ? "add_category" : "update_category";
                body       = ParamBuilder.buildParamsCategory(addPressed, category);
                break;
            }
            case "Inventory": {
                Inventory inventory = (Inventory) inventoryTable.commitEditingRow();
                model     = inventory;
                endpoint  = ApiHandler.INVENTORIES;
                action    = addPressed ? "add_inventory" : "update_inventory";
                body      = ParamBuilder.buildParamsInventory(addPressed, inventory);
                break;
            }
            case "General Items": {
                GeneralItem generalItem = (GeneralItem) itemsTable.commitEditingRow();
                model     = generalItem;
                endpoint  = ApiHandler.ITEMS;
                action    = addPressed ? "add_item" : "update_item";
                body      = ParamBuilder.buildParamsItem(addPressed, generalItem);
                break;
            }
            case "Suppliers": {
                Supplier supplier = (Supplier) suppliersTable.commitEditingRow();
                model     = supplier;
                endpoint  = ApiHandler.SUPPLIERS;
                action    = addPressed ? "add_supplier" : "update_supplier";
                body      = ParamBuilder.buildParamsSupplier(addPressed, supplier);
                break;
            }
            case "Orders": {
                Order order = (Order) ordersTable.commitEditingRow();
                model     = order;
                endpoint  = ApiHandler.ORDERS;
                action    = addPressed ? "add_order" : "update_order";
                body      = ParamBuilder.buildParamsOrder(addPressed, order);
                break;
            }
            case "Alerts": {
                StockAlert alert = (StockAlert) alertsTable.commitEditingRow();
                model     = alert;
                endpoint  = ApiHandler.STOCK_ALERTS;
                action    = addPressed ? "add_stock_alert" : "update_stock_alert";
                body      = ParamBuilder.buildParamsStockAlert(addPressed, alert);
                break;
            }
            case "Warehouse": {
                Warehouse warehouse = (Warehouse) warehouseTable.commitEditingRow();
                model     = warehouse;
                endpoint  = ApiHandler.WAREHOUSES;
                action    = addPressed ? "add_warehouse" : "update_warehouse";
                body      = ParamBuilder.buildParamsWarehouse(addPressed, warehouse);
                break;
            }
            default:
                UtilityCommands.displayNotificationPanel(3, "Unknown tab: " + getCurrentTabName());
                addPressed = false;
                return;
        }

        // 2) Send the API call and wrap the response
        String jsonResponse = ApiHandler.sendApiCall(method, endpoint, action, body);
        ApiResponse apiResponse = new ApiResponse(jsonResponse);

        // 3) Notify user and update the table if successful
        if (!apiResponse.getSuccess()) {
            UtilityCommands.displayNotificationPanel(3, "Unsuccessful operation");
        } else {
            UtilityCommands.displayNotificationPanel(1, apiResponse.getMessage());
            switch (getCurrentTabName()) {
                case "Users":
                    usersTable.updateSelectedRow((User) model);
                    break;
                case "Category":
                    categoriesTable.updateSelectedRow((Category) model);
                    break;
                case "Inventory":
                    inventoryTable.updateSelectedRow((Inventory) model);
                    break;
                case "General Items":
                    itemsTable.updateSelectedRow((GeneralItem) model);
                    break;
                case "Suppliers":
                    suppliersTable.updateSelectedRow((Supplier) model);
                    break;
                case "Orders":
                    ordersTable.updateSelectedRow((Order) model);
                    break;
                case "Alerts":
                    alertsTable.updateSelectedRow((StockAlert) model);
                    break;
                case "Warehouse":
                    warehouseTable.updateSelectedRow((Warehouse) model);
                    break;
            }
        }
        refreshToTableAction(null);
        addPressed = false;
    }

    public void deleteToTableAction(ActionEvent actionEvent){
        switch (getCurrentTabName()) {
            case "Users":
                User user = (User) usersTable.deleteSelectedRow();
                if(user != null){
                    ApiHandler.sendDeleteCall("DELETE_USER",user.getID());
                }
                break;
            case "Category":
                Category category = (Category) categoriesTable.deleteSelectedRow();
                if(category != null){
                    ApiHandler.sendDeleteCall("DELETE_CATEGORY",category.getID());
                }
                break;
            case "Inventory":
                Inventory inventory = (Inventory) inventoryTable.deleteSelectedRow();
                if(inventory != null){
                    ApiHandler.sendDeleteCall("DELETE_INVENTORY",inventory.getID());
                }
                break;
            case "General Items":
                GeneralItem generalItem = (GeneralItem) itemsTable.deleteSelectedRow();
                if(generalItem != null){
                    ApiHandler.sendDeleteCall("DELETE_ITEM", generalItem.getId());
                }
                break;
            case "Suppliers":
                Supplier supplier = (Supplier) suppliersTable.deleteSelectedRow();
                if(supplier != null){
                    ApiHandler.sendDeleteCall("DELETE_SUPPLIER",supplier.getID());
                }
                break;
            case "Orders":
                Order order = (Order) ordersTable.deleteSelectedRow();
                if(order != null){
                    ApiHandler.sendDeleteCall("DELETE_ORDER",order.getID());
                }
                break;
            case "Alerts":
                StockAlert alert = (StockAlert) alertsTable.deleteSelectedRow();
                if(alert != null){
                    ApiHandler.sendDeleteCall("DELETE_STOCK_ALERT",alert.getID());
                }
                break;
            case "Warehouse":
                Warehouse warehouse = (Warehouse) warehouseTable.deleteSelectedRow();
                if(warehouse != null){
                    ApiHandler.sendDeleteCall("DELETE_WAREHOUSE",warehouse.getID());
                }
                break;
            default:
                break;
        }

        addPressed = false;
    }

    public void refreshToTableAction(ActionEvent actionEvent){
            if (!areAllTableInit) {
                WareHouseDataHandler.initTables();
                areAllTableInit = true;

                // initialize all EnhancedTableView instances
                usersTable = new EnhancedTableView<>(usersTableView, WareHouseDataHandler.parsedUsersList);
                itemsTable = new EnhancedTableView<>(itemTableView, WareHouseDataHandler.parsedItemsList);
                categoriesTable = new EnhancedTableView<>(categoryTableView, WareHouseDataHandler.parsedCategoriesList);
                inventoryTable = new EnhancedTableView<>(inventoryTableView, WareHouseDataHandler.parsedInventoryList);
                ordersTable = new EnhancedTableView<>(ordersTableView, WareHouseDataHandler.parsedOrdersList);
                suppliersTable = new EnhancedTableView<>(suppliersTableView, WareHouseDataHandler.parsedSuppliersList);
                alertsTable = new EnhancedTableView<>(alertsTableView, WareHouseDataHandler.parsedAlertsList);
                warehouseTable = new EnhancedTableView<>(warehouseTableView, WareHouseDataHandler.parsedWarehousesList);
                logsTable = new EnhancedTableView<>(logsTableView, WareHouseDataHandler.parsedLogsList);

            } else {
                // refresh only the current tab's data
                WareHouseDataHandler.initTables(getCurrentTabName());
            }

            // update the visible table
            switch (getCurrentTabName()) {
                case "Users":
                    usersTable = new EnhancedTableView<>(usersTableView, WareHouseDataHandler.parsedUsersList);
                    usersTable.refresh();
                    break;
                case "General Items":
                    itemsTable = new EnhancedTableView<>(itemTableView, WareHouseDataHandler.parsedItemsList);
                    itemsTable.refresh();
                    break;
                case "Category":
                    categoriesTable = new EnhancedTableView<>(categoryTableView, WareHouseDataHandler.parsedCategoriesList);
                    categoriesTable.refresh();
                    break;
                case "Inventory":
                    inventoryTable = new EnhancedTableView<>(inventoryTableView, WareHouseDataHandler.parsedInventoryList);
                    inventoryTable.refresh();
                    break;
                case "Orders":
                    ordersTable = new EnhancedTableView<>(ordersTableView, WareHouseDataHandler.parsedOrdersList);
                    ordersTable.refresh();
                    break;
                case "Suppliers":
                    suppliersTable = new EnhancedTableView<>(suppliersTableView, WareHouseDataHandler.parsedSuppliersList);
                    suppliersTable.refresh();
                    break;
                case "Alerts":
                    alertsTable = new EnhancedTableView<>(alertsTableView, WareHouseDataHandler.parsedAlertsList);
                    alertsTable.refresh();
                    break;
                case "Warehouse":
                    warehouseTable = new EnhancedTableView<>(warehouseTableView, WareHouseDataHandler.parsedWarehousesList);
                    warehouseTable.refresh();
                    break;
                case "Logs":
                    logsTable = new EnhancedTableView<>(logsTableView, WareHouseDataHandler.parsedLogsList);
                    logsTable.refresh();
                    break;
                default:
                    break;
            }

            numberOfUsers.setText(String.valueOf(WareHouseDataHandler.parsedUsersList.size()));
            numberOfWarehouses.setText(String.valueOf(WareHouseDataHandler.parsedWarehousesList.size()));

    }

    public String getCurrentTabName() {
        Tab selectedTab = dbTablePane.getSelectionModel().getSelectedItem();

        if (selectedTab != null) {
            return selectedTab.getText();  // Return the name of the currently selected tab
        } else {
            return "No tab selected";  // In case no tab is selected
        }
    }
    

    public void helpAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("WareWise Help");
        alert.setHeaderText("WareWise Admin Console");
        alert.setContentText("""
                Use the left menu to switch between Dashboard, Database, and Settings.
                Open Database to view, add, update, delete, and refresh server data.
                Use Menu > Login before editing data if automatic login is disabled.
                """);
        alert.showAndWait();
    }

    public void logInAction(ActionEvent event){
        if(isServerOn){
            logIn();
        }else{
            UtilityCommands.displayWarning("Server is not running!",false);
        }
    }

    private void logIn() {
        File file = new File(AdminUtil.CREDENTIALS_FILE);
        if(UtilityCommands.isFileEmpty(file) || !file.exists()){
            promptLogin();
        }else {
            AdminUtil.doLogin();
            setUsernameLabel(sessionUsername);
        }
        refreshToTableAction(null);
        isLogin = true;
    }

    private void promptLogin(){
        String[] loginPrompt = LoginPrompt.promptLogin();
        if (loginPrompt == null) {
            return;
        }
        AdminUtil.saveLoginCred(loginPrompt[0],loginPrompt[1]);
        AdminUtil.doLogin();
        setUsernameLabel(loginPrompt[0]);
        setUsernameLabel(sessionUsername);

    }

    public void aboutAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About WareWise");
        alert.setHeaderText("WareWise App");
        alert.setContentText("""
                Portfolio warehouse management project.
                Author: Calin Baculescu
                GitHub: https://github.com/bossNilac/WareWise
                """);
        alert.showAndWait();
    }

    public void toggleSettingsButton(ActionEvent actionEvent){
        resetUi();
        settingsUiElements.forEach(node -> node.setVisible(true));
    }

    public void toGitHubAction(ActionEvent actionEvent) {
        UtilityCommands.openLink("https://github.com/bossNilac");
    }

    public void toLinkdinAction(ActionEvent actionEvent) {
        UtilityCommands.openLink("https://www.linkedin.com/in/calin-baculescu-a47297206/");
    }

    public void updateCheckBox1(ActionEvent actionEvent) {
        handleSelection(checkBoxes,0);
    }

    public void updateCheckBox2(ActionEvent actionEvent) {
        handleSelection(checkBoxes,1);
    }

    public void updateCheckBox3(ActionEvent actionEvent) {
        handleSelection(checkBoxes,2);
    }

    private void handleSelection(CheckBox[] checkBoxes, int selectedIndex) {
        checkedCount = 0 ;
        boolean[] tempSettings = new boolean[3];
        for ( int i = 0 ;i < 3; i++) {
            tempSettings[i] = checkBoxes[i].isSelected();
            if (checkBoxes[i].isSelected()) {
                checkedCount++;
            }
        }
        if (checkedCount > 1) {
            checkBoxes[selectedIndex].setSelected(false); // Deselect the last one checked
            UtilityCommands.displayWarning("Cannot have two modes selected at the same time",false); // Call method when invalid selection occurs
        }else {
            System.arraycopy(tempSettings, 0,
                    ServerApplication.settings, 0, 3);
        }
    }

    public void saveSettingsAction(ActionEvent actionEvent){
        UtilityCommands.displayNotificationPanel(1,"Saved settings");
        PropertiesReader.setSettings(ServerApplication.settings);
    }


}
