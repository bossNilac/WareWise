package com.warewise.client.controller;

import com.warewise.client.networking.ApiHandler;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.networking.ParamBuilder;
import com.warewise.client.util.AlertUtil;
import com.warewise.client.util.form.SupplierForm;
import com.warewise.client.util.model.Category;
import com.warewise.client.util.model.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Arrays;

import static com.warewise.client.networking.ApiHandler.*;
import static com.warewise.client.util.AdminUtil.parseResponse;

public class SupplierController {

    @FXML
    private TableColumn<Supplier,String> nameColumn;
    @FXML
    private TableColumn<Supplier,String> emailColumn;
    @FXML
    private TableColumn<Supplier,String> contactPhoneNoColumn;
    @FXML
    private TableColumn<Supplier,String> addressColumn;
    @FXML
    private TableColumn<Supplier,String> createdAtColumn;

    @FXML
    private TableView<Supplier> supplierTableView;

    private final ObservableList<Supplier> supplierObservableList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        supplierTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Supplier, String> t) ->{
                    ( t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setName(t.getNewValue());
                    updateSupplier(t);
                }

        );

        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Supplier, String> t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setContactEmail(t.getNewValue());
                    updateSupplier(t);
                }

        );

        contactPhoneNoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        contactPhoneNoColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Supplier, String> t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setContactPhoneNo(t.getNewValue());
                    updateSupplier(t);
                }

        );

        addressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        addressColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Supplier, String> t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setAddress(t.getNewValue());
                    updateSupplier(t);
                }

        );

        createdAtColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        createdAtColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Supplier, String> t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setCreatedAt(t.getNewValue());
                    updateSupplier(t);
                }

        );


        // Bind table columns to Supplier properties
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        contactPhoneNoColumn.setCellValueFactory(new PropertyValueFactory<>("contactPhoneNo"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Populate TableView
        supplierObservableList.setAll(DataHandler.parsedSuppliersList);
        supplierTableView.setItems(supplierObservableList);
    }

    /**
     * Refreshes the supplier table with new data.
     */
    public void refreshTable() {
        DataHandler.initTables("Supplier");
        supplierObservableList.setAll(DataHandler.parsedSuppliersList);
    }

    public void addNewSupplierAction(ActionEvent actionEvent){
        Supplier supplier = new SupplierForm().showAndWait();
        if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.CREATE,"Supplier")) {
            String params = ParamBuilder.buildParamsSupplier(true,supplier);
            String unparsedResponse = ApiHandler.sendApiCall(POST,CATEGORIES,"add_category",params);
            parseResponse(unparsedResponse);
            refreshTable();
        }
    }

    private void updateSupplier(TableColumn.CellEditEvent<Supplier, String> t) {
        if (AlertUtil.showYesNoAlert(AlertUtil.AlertType.MODIFY, "Supplier")) {
            String params = ParamBuilder.buildParamsSupplier(true, t.getTableView().getItems().get(
                    t.getTablePosition().getRow()));
            String unparsedResponse = ApiHandler.sendApiCall(PATCH, SUPPLIERS, "update_supplier", params);
            if (parseResponse(unparsedResponse)) {
                refreshTable();
            }
        }
    }

    public void deleteSupplierAction(KeyEvent event){
        final Supplier selectedItem = supplierTableView.getSelectionModel().getSelectedItem();
        if(event.getCode().equals(KeyCode.DELETE) && selectedItem !=null){
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Supplier")) {
                ApiHandler.sendDeleteCall("DELETE_SUPPLIER",selectedItem.getID());
                refreshTable();
            }
        }
    }

    public void deleteSupplierActionEvent(ActionEvent actionEvent){
        final Supplier selectedItem = supplierTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Supplier")) {
                ApiHandler.sendDeleteCall("DELETE_SUPPLIER",selectedItem.getID());
                refreshTable();
            }
        }
    }
}
