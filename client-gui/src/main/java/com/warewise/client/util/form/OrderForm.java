package com.warewise.client.util.form;

import com.warewise.client.App;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.util.AdminUtil;
import com.warewise.client.util.enums.OrderStatus;
import com.warewise.client.util.model.Category;
import com.warewise.client.util.model.GeneralItem;
import com.warewise.client.util.model.Order;
import com.warewise.client.util.model.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



public class OrderForm {

    private Order createdOrder;

    public Order showAndWait() {

        DataHandler.initTables("GeneralItem");

        // 1) Load lookup data
        Map<String,Integer> nameToItemId  = DataHandler.parsedItemsList.stream()
                .collect(Collectors.toMap(GeneralItem::getName, GeneralItem::getId));

        ObservableList<String> itemsNames = FXCollections.observableArrayList(nameToItemId.keySet());

        Stage stage = new Stage();
        stage.setTitle("New Order");
        stage.initModality(Modality.APPLICATION_MODAL);

        TextField   quantityField     = new TextField();
        ComboBox<String> itemsCombo = new ComboBox<>(itemsNames);

        Button submitBtn = new Button("Create");
        Button cancelBtn = new Button("Cancel");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(new Label("Item:"),       0, 0);
        grid.add(itemsCombo,                1, 0);
        grid.add(new Label("Quantity:"),       0, 1);
        grid.add(quantityField,                1, 1);
        grid.add(submitBtn,                0, 2);
        grid.add(cancelBtn,                1, 2);

        Scene scene = new Scene(grid);
        String theme = App.darkMode
                ? "/stylesheets/stylesheet-dark.css"
                : "/stylesheets/stylesheet-light.css";
        scene.getStylesheets().add(getClass().getResource(App.resourceDir + theme).toExternalForm());
        stage.setScene(scene);

        // 5) Submit logic: pull out the IDs from the maps
        submitBtn.setOnAction(e -> {
            String quantity     = quantityField.getText();
            String itemsComboValue  = itemsCombo.getValue();

            if (quantity.isEmpty() || itemsComboValue.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "All fields are required.", ButtonType.OK)
                        .showAndWait();
                return;
            }

            int itemId  = nameToItemId.get(itemsComboValue);
            // now pass IDs instead of names into your GeneralItem
            createdOrder = new Order(
                   itemId,
                   Integer.parseInt(quantity),
                   OrderStatus.PENDING,
                   LocalDateTime.now().format(
                           DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                   LocalDateTime.now().format(
                           DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                    AdminUtil.userId
            );

            stage.close();
        });

        // 6) Cancel logic
        cancelBtn.setOnAction(e -> {
            createdOrder = null;
            stage.close();
        });

        stage.showAndWait();
        return createdOrder;
    }
}
