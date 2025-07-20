package com.warewise.client.controller;

import com.warewise.client.networking.ApiHandler;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.networking.ParamBuilder;
import com.warewise.client.util.AlertUtil;
import com.warewise.client.util.model.Category;
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

import static com.warewise.client.networking.ApiHandler.*;
import static com.warewise.client.util.AdminUtil.parseResponse;

public class CategoriesController {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TableColumn<Category,Integer> idColumn;
    @FXML
    private TableColumn<Category,String> nameColumn;
    @FXML
    private TableColumn<Category,String> descriptionColumn;
    @FXML
    private TableView<Category> categoryTableView;

    private final ObservableList<Category> categoryData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        categoryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Category, String> t) ->{
                    ( t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setName(t.getNewValue());
                    updateCategory(t);
                }

        );

        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Category, String> t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setDescription(t.getNewValue());
                    updateCategory(t);
                }

        );

        // Bind table columns to Category properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Populate TableView
        categoryData.setAll(DataHandler.parsedCategoriesList);
        categoryTableView.setItems(categoryData);
    }

    /**
     * Refreshes the category table with new data.
     */
    public void refreshTable() {
        DataHandler.initTables("Category");
        categoryData.setAll(DataHandler.parsedCategoriesList);
    }

    public void addNewCategoryAction(ActionEvent actionEvent){
        if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.CREATE,"Category")) {
            DataHandler.initTables("Category");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Category category = new Category(
                    nameTextField.getText(),
                    descriptionTextField.getText()
            );
            nameTextField.clear();
            descriptionTextField.clear();
            String params = ParamBuilder.buildParamsCategory(true,category);
            String unparsedResponse = ApiHandler.sendApiCall(POST,CATEGORIES,"add_category",params);
            parseResponse(unparsedResponse);
            refreshTable();
        }
    }

    private void updateCategory(TableColumn.CellEditEvent<Category, String> t){
        if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.MODIFY,"Category")){
            String params = ParamBuilder.buildParamsCategory(true,t.getTableView().getItems().get(
                    t.getTablePosition().getRow()));
            String unparsedResponse = ApiHandler.sendApiCall(PATCH,CATEGORIES,"update_category",params);
            if(parseResponse(unparsedResponse)){
                refreshTable();
            }
        }
    }

    public void deleteCategoryAction(KeyEvent event){
        final Category selectedItem = categoryTableView.getSelectionModel().getSelectedItem();
        if(event.getCode().equals(KeyCode.DELETE) && selectedItem !=null){
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Category")) {
                ApiHandler.sendDeleteCall("DELETE_CATEGORY",selectedItem.getID());
                refreshTable();
            }
        }
    }

    public void deleteCategoryActionEvent(ActionEvent actionEvent){
        final Category selectedItem = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Category")) {
                ApiHandler.sendDeleteCall("DELETE_CATEGORY",selectedItem.getID());
                refreshTable();
            }
        }
    }


}
