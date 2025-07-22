package com.warewise.client.controller;

import com.warewise.client.networking.ApiHandler;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.networking.ParamBuilder;
import com.warewise.client.util.AlertUtil;
import com.warewise.client.util.form.GeneralItemForm;
import com.warewise.client.util.model.Category;
import com.warewise.client.util.model.GeneralItem;
import com.warewise.client.util.model.Supplier;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.warewise.client.networking.ApiHandler.*;
import static com.warewise.client.util.AdminUtil.parseResponse;

public class GeneralItemController {
    @FXML private TableView<GeneralItem>               itemTableView;
    @FXML private TableColumn<GeneralItem,String>      nameColumn;
    @FXML private TableColumn<GeneralItem,String>      barcodeColumn;
    @FXML private TableColumn<GeneralItem,String>      categoryNameColumn;
    @FXML private TableColumn<GeneralItem,Integer>     priceColumn;
    @FXML private TableColumn<GeneralItem,Integer>     setQuantityColumn;
    @FXML private TableColumn<GeneralItem,String>      supplierColumn;
    @FXML private TableColumn<GeneralItem,Boolean>     expiresColumn;

    private final ObservableList<GeneralItem> generalItemData = FXCollections.observableArrayList();

    private Map<Integer, Category>  idToCat;
    private Map<String,  Category>  nameToCat;
    private ObservableList<String>  catNames;

    private Map<Integer, Supplier>  idToSupplier;
    private Map<String,  Supplier>  nameToSupplier;
    private ObservableList<String>  supplierNames;

    @FXML
    private void initialize() {
        DataHandler.initTables("Category");
        DataHandler.initTables("Suppliers");
        DataHandler.initTables("GeneralItem");

        List<Category> parsedCategoriesList  = DataHandler.parsedCategoriesList;
        List<Supplier> parsedSuppliersList   = DataHandler.parsedSuppliersList;

        idToCat      = parsedCategoriesList.stream()
                .collect(Collectors.toMap(Category::getID, c -> c));
        nameToCat    = parsedCategoriesList.stream()
                .collect(Collectors.toMap(Category::getName, c -> c));
        catNames     = FXCollections.observableArrayList(nameToCat.keySet());

        idToSupplier   = parsedSuppliersList.stream()
                .collect(Collectors.toMap(Supplier::getID, s -> s));
        nameToSupplier = parsedSuppliersList.stream()
                .collect(Collectors.toMap(Supplier::getName, s -> s));
        supplierNames  = FXCollections.observableArrayList(nameToSupplier.keySet());

        itemTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemTableView.setEditable(true);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(t -> {
            t.getRowValue().setName(t.getNewValue());
            updateItem(t);
        });

        barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        barcodeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        barcodeColumn.setOnEditCommit(t -> {
            t.getRowValue().setBarcode(t.getNewValue());
            updateItem(t);
        });

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new IntegerStringConverter())
        );
        priceColumn.setOnEditCommit(t -> {
            t.getRowValue().setPrice(t.getNewValue());
            updateItemInteger(t);
        });

        setQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("setQuantity"));
        setQuantityColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new IntegerStringConverter())
        );
        setQuantityColumn.setOnEditCommit(t -> {
            t.getRowValue().setSetQuantity(t.getNewValue());
            updateItemInteger(t);
        });

        expiresColumn.setCellValueFactory(cellData -> {
            GeneralItem item = cellData.getValue();
            SimpleBooleanProperty prop = new SimpleBooleanProperty(item.getExpires());
            prop.addListener((obs, oldVal, newVal) -> {
                item.setExpires(newVal);
                // you can call your boolean update helper directly here
                updateItemBoolean(new TableColumn.CellEditEvent<>(
                        itemTableView,
                        new TablePosition<>(itemTableView,
                                itemTableView.getItems().indexOf(item),
                                expiresColumn),
                        TableColumn.editCommitEvent(),
                        newVal
                ));
            });
            return prop;
        });
        expiresColumn.setCellFactory(CheckBoxTableCell.forTableColumn(expiresColumn));

        categoryNameColumn.setCellValueFactory(cellData -> {
            int catId = cellData.getValue().getCategoryId();
            String name = idToCat.containsKey(catId)
                    ? idToCat.get(catId).getName()
                    : "";
            return new SimpleStringProperty(name);
        });
        categoryNameColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                new DefaultStringConverter(), catNames
        ));
        categoryNameColumn.setOnEditCommit(evt -> {
            GeneralItem item      = evt.getRowValue();
            Category chosenCat    = nameToCat.get(evt.getNewValue());
            if (chosenCat != null) {
                item.setCategoryId(chosenCat.getID());
                updateItem(evt);
                itemTableView.refresh();
            }
        });

        supplierColumn.setCellValueFactory(cellData -> {
            int suppId = cellData.getValue().getSupplierId();
            String name = idToSupplier.containsKey(suppId)
                    ? idToSupplier.get(suppId).getName()
                    : "";
            return new SimpleStringProperty(name);
        });
        supplierColumn.setCellFactory(ComboBoxTableCell.forTableColumn(
                new DefaultStringConverter(), supplierNames
        ));
        supplierColumn.setOnEditCommit(evt -> {
            GeneralItem item       = evt.getRowValue();
            Supplier chosenSupp    = nameToSupplier.get(evt.getNewValue());
            if (chosenSupp != null) {
                item.setSupplierId(chosenSupp.getID());
                updateItem(evt);
                itemTableView.refresh();
            }
        });

        generalItemData.setAll(DataHandler.parsedItemsList);
        itemTableView.setItems(generalItemData);
    }


    public void deleteItemAction(KeyEvent keyEvent) {
        final GeneralItem selectedItem = itemTableView.getSelectionModel().getSelectedItem();
        if(keyEvent.getCode().equals(KeyCode.DELETE) && selectedItem !=null){
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Item")) {
                ApiHandler.sendDeleteCall("DELETE_GENERAL_ITEM",selectedItem.getId());
                refreshTable();
            }
        }
    }

    public void addNewItemAction(ActionEvent actionEvent) {
        GeneralItem selectedItem = new GeneralItemForm().showAndWait();
        if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.CREATE,"Item")) {
            String params = ParamBuilder.buildParamsItem(true,selectedItem);
            String unparsedResponse = ApiHandler.sendApiCall(POST,GENERAL_ITEMS,"add_item",params);
            parseResponse(unparsedResponse);
            refreshTable();
        }
    }

    public void deleteItemActionEvent(ActionEvent actionEvent) {
        final GeneralItem selectedItem = itemTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Item")) {
                ApiHandler.sendDeleteCall("DELETE_GENERAL_ITEM",selectedItem.getId());
                refreshTable();
            }
        }
    }


    private void updateItem(TableColumn.CellEditEvent<GeneralItem, String> t) {
        if (AlertUtil.showYesNoAlert(AlertUtil.AlertType.MODIFY, "Item")) {
            String params = ParamBuilder.buildParamsItem(false, t.getTableView().getItems().get(
                    t.getTablePosition().getRow()));
            System.out.println(params);
            String unparsedResponse = ApiHandler.sendApiCall(PATCH, GENERAL_ITEMS, "update_item", params);
            if (parseResponse(unparsedResponse)) {
                refreshTable();
            }
        }
    }

    private void updateItemInteger(TableColumn.CellEditEvent<GeneralItem, Integer> t) {
        if (AlertUtil.showYesNoAlert(AlertUtil.AlertType.MODIFY, "Item")) {
            String params = ParamBuilder.buildParamsItem(false, t.getTableView().getItems().get(
                    t.getTablePosition().getRow()));
            String unparsedResponse = ApiHandler.sendApiCall(PATCH, GENERAL_ITEMS, "update_item", params);
            if (parseResponse(unparsedResponse)) {
                refreshTable();
            }
        }
    }

    private void updateItemBoolean(TableColumn.CellEditEvent<GeneralItem, Boolean> t) {
        if (AlertUtil.showYesNoAlert(AlertUtil.AlertType.MODIFY, "Item")) {
            String params = ParamBuilder.buildParamsItem(false, t.getTableView().getItems().get(
                    t.getTablePosition().getRow()));
            String unparsedResponse = ApiHandler.sendApiCall(PATCH, GENERAL_ITEMS, "update_item", params);
            if (parseResponse(unparsedResponse)) {
                refreshTable();
            }
        }
    }

    public void refreshTable() { 
        DataHandler.initTables("GeneralItem");
        generalItemData.setAll(DataHandler.parsedItemsList);
    }

}
