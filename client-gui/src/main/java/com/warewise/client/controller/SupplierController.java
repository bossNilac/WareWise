package com.warewise.client.controller;

import com.warewise.client.network.DataHandler;
import com.warewise.client.util.AlertUtil;
import com.warewise.client.util.SupplierForm;
import com.warewise.common.model.Supplier;
import com.warewise.common.util.protocol.Protocol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Arrays;

import static com.warewise.client.network.DataHandler.*;

public class SupplierController {

    @FXML
    private TableColumn<Supplier,Integer> idColumn;
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
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        contactPhoneNoColumn.setCellValueFactory(new PropertyValueFactory<>("contactPhoneNo"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Populate TableView
        supplierObservableList.setAll(DataHandler.supplierList);
        supplierTableView.setItems(supplierObservableList);
    }

    /**
     * Refreshes the supplier table with new data.
     */
    public void refreshTable() {
        supplierObservableList.setAll(supplierList);
    }

//    public void addNewSupplierAction(ActionEvent actionEvent){
//        if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.CREATE,"Supplier")) {
//            DataHandler.askForWarehouseData("Supplier");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//            supplierList.add(supplier);
//            nameTextField.clear();
//            descriptionTextField.clear();
//            refreshTable();
//            String[] params = Arrays.copyOfRange(supplierToParam(supplier),1,3);
//            parseAndSendToServer(Protocol.ADD_SUPPLIER,params);
//        }
//    }

    private void updateSupplier(TableColumn.CellEditEvent<Supplier, String> t){
        if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.MODIFY,"Supplier")){
            String[] params = Arrays.copyOfRange(supplierToParam(( t.getTableView().getItems().get(
                    t.getTablePosition().getRow()))),0,5);
            parseAndSendToServer(Protocol.UPDATE_SUPPLIER,params);
        }
    }

    private String[] supplierToParam(Supplier supplier){
        return new String[]{
                String.valueOf(supplier.getID()),supplier.getName(),supplier.getContactEmail(), supplier.getContactPhoneNo(),supplier.getAddress(), supplier.getCreatedAt()
        };
    }

    public void deleteSupplierAction(KeyEvent event){
        final Supplier selectedItem = supplierTableView.getSelectionModel().getSelectedItem();
        if(event.getCode().equals(KeyCode.DELETE) && selectedItem !=null){
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Supplier")) {
                parseAndSendToServer(Protocol.DELETE_SUPPLIER, supplierToParam(selectedItem));
                supplierList.remove(selectedItem);
                refreshTable();
            }
        }
    }

    public void deleteSupplierActionEvent(ActionEvent actionEvent){
        final Supplier selectedItem = supplierTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Supplier")) {
                parseAndSendToServer(Protocol.DELETE_SUPPLIER, supplierToParam(selectedItem));
                supplierList.remove(selectedItem);
                refreshTable();
            }
        }
    }


    public void addSupplierActionEvent(ActionEvent actionEvent) {
        SupplierForm form = new SupplierForm();
        Supplier newSupplier = form.showAndWait();

        if (newSupplier != null) {
            String[] params = Arrays.copyOfRange(supplierToParam(newSupplier),1,6);
            if (AlertUtil.showYesNoAlert(AlertUtil.AlertType.CREATE,"Supplier")) {
                parseAndSendToServer(Protocol.ADD_SUPPLIER, params);
                supplierList.add(newSupplier);
                refreshTable();
            }
        }

    }
}
