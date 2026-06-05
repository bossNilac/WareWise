package com.warewise.client.controller;

import com.warewise.client.networking.ApiHandler;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.networking.ParamBuilder;
import com.warewise.client.util.AdminUtil;
import com.warewise.client.util.model.Category;
import com.warewise.client.util.model.GeneralItem;
import com.warewise.client.util.model.Inventory;
import com.warewise.client.util.model.Supplier;
import com.warewise.client.util.model.Warehouse;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class AddItemController implements Initializable {
    @FXML private TextField nameField;
    @FXML private TextField barcodeField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private TextField newCategoryField;
    @FXML private TextField categoryDescriptionField;
    @FXML private ComboBox<Supplier> supplierCombo;
    @FXML private TextField priceField;
    @FXML private TextField setQuantityField;
    @FXML private CheckBox expiresCheckBox;
    @FXML private CheckBox addToInventoryCheckBox;
    @FXML private ComboBox<Warehouse> warehouseCombo;
    @FXML private TextField inventoryQuantityField;
    @FXML private TextArea inventoryDescriptionArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addToInventoryCheckBox.selectedProperty().addListener((obs, oldValue, selected) -> setInventoryFieldsDisabled(!selected));
        loadLookups();
        setInventoryFieldsDisabled(true);
    }

    @FXML
    private void saveItemAction(ActionEvent event) {
        try {
            String name = required(nameField, "Name");
            String barcode = required(barcodeField, "Barcode");
            int price = parseNonNegative(priceField, "Price");
            int setQuantity = parseNonNegative(setQuantityField, "Set quantity");
            Supplier supplier = supplierCombo.getValue();
            if (supplier == null) {
                throw new IllegalArgumentException("Select a supplier.");
            }

            Category category = resolveCategory();
            if (category == null) {
                throw new IllegalArgumentException("Select a category or enter a new category.");
            }

            GeneralItem item = new GeneralItem(name, setQuantity, barcode, category.getID(),
                    supplier.getID(), price, expiresCheckBox.isSelected());
            String itemResponse = ApiHandler.sendApiCall(ApiHandler.POST, ApiHandler.GENERAL_ITEMS,
                    "add_item", ParamBuilder.buildParamsItem(true, item));
            if (!AdminUtil.parseResponse(itemResponse)) {
                return;
            }

            if (addToInventoryCheckBox.isSelected()) {
                Warehouse warehouse = warehouseCombo.getValue();
                if (warehouse == null) {
                    throw new IllegalArgumentException("Select a warehouse for actual inventory.");
                }
                int quantity = parseNonNegative(inventoryQuantityField, "Inventory quantity");
                String description = inventoryDescriptionArea.getText() == null || inventoryDescriptionArea.getText().isBlank()
                        ? "Created from Add Item"
                        : inventoryDescriptionArea.getText().trim();
                Inventory inventory = new Inventory(name, description, quantity,
                        LocalDateTime.now().toString(), warehouse.getID());
                String inventoryResponse = ApiHandler.sendApiCall(ApiHandler.POST, ApiHandler.INVENTORIES,
                        "add_inventory", ParamBuilder.buildParamsInventory(true, inventory));
                AdminUtil.parseResponse(inventoryResponse);
            }

            clearForm();
            loadLookups();
        } catch (IllegalArgumentException ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void clearAction(ActionEvent event) {
        clearForm();
    }

    private Category resolveCategory() {
        String newCategoryName = newCategoryField.getText();
        if (newCategoryName != null && !newCategoryName.isBlank()) {
            Category newCategory = new Category(newCategoryName.trim(), categoryDescriptionField.getText());
            String response = ApiHandler.sendApiCall(ApiHandler.POST, ApiHandler.CATEGORIES,
                    "add_category", ParamBuilder.buildParamsCategory(true, newCategory));
            if (!AdminUtil.parseResponse(response)) {
                return null;
            }
            DataHandler.initTables("Category");
            return DataHandler.parsedCategoriesList.stream()
                    .filter(category -> category.getName().equalsIgnoreCase(newCategoryName.trim()))
                    .max(Comparator.comparingInt(Category::getID))
                    .orElse(null);
        }
        return categoryCombo.getValue();
    }

    private void loadLookups() {
        DataHandler.initTables("Category");
        DataHandler.initTables("Suppliers");
        DataHandler.initTables("Warehouse");

        categoryCombo.setItems(FXCollections.observableArrayList(
                DataHandler.parsedCategoriesList == null ? List.of() : DataHandler.parsedCategoriesList));
        supplierCombo.setItems(FXCollections.observableArrayList(
                DataHandler.parsedSuppliersList == null ? List.of() : DataHandler.parsedSuppliersList));
        warehouseCombo.setItems(FXCollections.observableArrayList(
                DataHandler.parsedWarehousesList == null ? List.of() : DataHandler.parsedWarehousesList));

        categoryCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Category category) {
                return category == null ? "" : category.getName();
            }
            @Override public Category fromString(String string) {
                return categoryCombo.getItems().stream()
                        .filter(category -> category.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
        supplierCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Supplier supplier) {
                return supplier == null ? "" : supplier.getName();
            }
            @Override public Supplier fromString(String string) {
                return supplierCombo.getItems().stream()
                        .filter(supplier -> supplier.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
        warehouseCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Warehouse warehouse) {
                return warehouse == null ? "" : warehouse.getName();
            }
            @Override public Warehouse fromString(String string) {
                return warehouseCombo.getItems().stream()
                        .filter(warehouse -> warehouse.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void setInventoryFieldsDisabled(boolean disabled) {
        warehouseCombo.setDisable(disabled);
        inventoryQuantityField.setDisable(disabled);
        inventoryDescriptionArea.setDisable(disabled);
    }

    private String required(TextField field, String label) {
        String value = field.getText();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.trim();
    }

    private int parseNonNegative(TextField field, String label) {
        try {
            int value = Integer.parseInt(required(field, label));
            if (value < 0) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " must be a positive number or zero.");
        }
    }

    private void clearForm() {
        nameField.clear();
        barcodeField.clear();
        newCategoryField.clear();
        categoryDescriptionField.clear();
        priceField.clear();
        setQuantityField.clear();
        expiresCheckBox.setSelected(false);
        addToInventoryCheckBox.setSelected(false);
        inventoryQuantityField.clear();
        inventoryDescriptionArea.clear();
        categoryCombo.getSelectionModel().clearSelection();
        supplierCombo.getSelectionModel().clearSelection();
        warehouseCombo.getSelectionModel().clearSelection();
    }
}
