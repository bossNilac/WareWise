package com.warewise.client.controller;

import com.warewise.client.network.DataHandler;
import com.warewise.client.util.AlertUtil;
import com.warewise.common.model.Category;
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
        categoryData.setAll(DataHandler.categoryList);
        categoryTableView.setItems(categoryData);
    }

    /**
     * Refreshes the category table with new data.
     */
    public void refreshTable() {
        categoryData.setAll(DataHandler.categoryList);
    }

    public void addNewCategoryAction(ActionEvent actionEvent){
        if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.CREATE,"Category")) {
            DataHandler.askForWarehouseData("Category");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Category category = new Category(
                    categoryList.get(categoryList.size()-1).getID()+1,
                    nameTextField.getText(),
                    descriptionTextField.getText()
            );
            categoryList.add(category);
            nameTextField.clear();
            descriptionTextField.clear();
            refreshTable();
            String[] params = Arrays.copyOfRange(categoryToParam(category),1,3);
            parseAndSendToServer(Protocol.ADD_CATEGORY,params);
        }
    }

    private void updateCategory(TableColumn.CellEditEvent<Category, String> t){
        if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.MODIFY,"Category")){
            parseAndSendToServer(Protocol.UPDATE_CATEGORY,categoryToParam(( t.getTableView().getItems().get(
                    t.getTablePosition().getRow())
            )));
        }
    }

    private String[] categoryToParam(Category category){
        return new String[]{
                String.valueOf(category.getID()),category.getName(),category.getDescription()
        };
    }

    public void deleteCategoryAction(KeyEvent event){
        final Category selectedItem = categoryTableView.getSelectionModel().getSelectedItem();
        if(event.getCode().equals(KeyCode.DELETE) && selectedItem !=null){
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Category")) {
                parseAndSendToServer(Protocol.DELETE_CATEGORY, categoryToParam(selectedItem));
                categoryList.remove(selectedItem);
                refreshTable();
            }
        }
    }

    public void deleteCategoryActionEvent(ActionEvent actionEvent){
        final Category selectedItem = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if(AlertUtil.showYesNoAlert(AlertUtil.AlertType.DELETE,"Category")) {
                parseAndSendToServer(Protocol.DELETE_CATEGORY, categoryToParam(selectedItem));
                categoryList.remove(selectedItem);
                refreshTable();
            }
        }
    }


}
