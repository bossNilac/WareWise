package com.warewise.client.util.form;

import com.warewise.client.App;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.util.model.Category;
import com.warewise.client.util.model.Supplier;
import com.warewise.client.util.model.GeneralItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneralItemForm {

    private GeneralItem createdItem;

    public GeneralItem showAndWait() {
        // 1) Load lookup data
        DataHandler.initTables("Category");
        DataHandler.initTables("Suppliers");
        List<Category>   categories = DataHandler.parsedCategoriesList;
        List<Supplier>   suppliers  = DataHandler.parsedSuppliersList;

        // 2) Build name→ID maps
        Map<String,Integer> nameToCatId  = categories.stream()
                .collect(Collectors.toMap(Category::getName, Category::getID));
        Map<String,Integer> nameToSuppId = suppliers.stream()
                .collect(Collectors.toMap(Supplier::getName, Supplier::getID));

        // 3) Populate ComboBoxes with the human-friendly names
        ObservableList<String> categoryNames = FXCollections.observableArrayList(nameToCatId.keySet());
        ObservableList<String> supplierNames = FXCollections.observableArrayList(nameToSuppId.keySet());

        // 4) Build the form
        Stage stage = new Stage();
        stage.setTitle("New Item");
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField   nameField      = new TextField();
        TextField   barcodeField   = new TextField();
        ComboBox<String> categoryCombo = new ComboBox<>(categoryNames);
        TextField   priceField     = new TextField();
        TextField   setQuantityField     = new TextField();
        ComboBox<String> supplierCombo = new ComboBox<>(supplierNames);
        CheckBox   expiresField   = new CheckBox();

        Button submitBtn = new Button("Create");
        Button cancelBtn = new Button("Cancel");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Name:"),       0, 0);
        grid.add(nameField,                1, 0);
        grid.add(new Label("Barcode:"),    0, 1);
        grid.add(barcodeField,             1, 1);
        grid.add(new Label("Category:"),   0, 2);
        grid.add(categoryCombo,            1, 2);
        grid.add(new Label("Price:"),      0, 3);
        grid.add(priceField,               1, 3);
        grid.add(new Label("Set Quantity:"),      0, 4);
        grid.add(setQuantityField,               1, 4);
        grid.add(new Label("Supplier:"),   0, 5);
        grid.add(supplierCombo,            1, 5);
        grid.add(new Label("Expires:"),    0, 6);
        grid.add(expiresField,             1, 6);
        grid.add(submitBtn,                0, 7);
        grid.add(cancelBtn,                1, 7);

        Scene scene = new Scene(grid);
        String theme = App.darkMode
                ? "/stylesheets/stylesheet-dark.css"
                : "/stylesheets/stylesheet-light.css";
        scene.getStylesheets().add(getClass().getResource(App.resourceDir + theme).toExternalForm());
        stage.setScene(scene);

        // 5) Submit logic: pull out the IDs from the maps
        submitBtn.setOnAction(e -> {
            String name     = nameField.getText();
            String barcode  = barcodeField.getText();
            String catName  = categoryCombo.getValue();
            String setQuantity    = setQuantityField.getText();
            String price    = priceField.getText();
            String suppName = supplierCombo.getValue();
            boolean expires  = expiresField.isSelected();

            if (name.isEmpty() || barcode.isEmpty()
                    || catName == null || price.isEmpty()
                    || suppName == null) {
                new Alert(Alert.AlertType.ERROR, "All fields are required.", ButtonType.OK)
                        .showAndWait();
                return;
            }

            int catId  = nameToCatId.get(catName);
            int suppId = nameToSuppId.get(suppName);

            // now pass IDs instead of names into your GeneralItem
            createdItem = new GeneralItem(
                    name,
                    Integer.parseInt(setQuantity),
                    barcode,
                    catId,
                    suppId,
                    Integer.parseInt(price),
                    expires
            );

            stage.close();
        });

        // 6) Cancel logic
        cancelBtn.setOnAction(e -> {
            createdItem = null;
            stage.close();
        });

        stage.showAndWait();
        return createdItem;
    }
}
