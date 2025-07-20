package com.warewise.client.util.form;

import com.warewise.client.App;
import com.warewise.client.networking.DataHandler;
import com.warewise.client.util.model.Supplier;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.warewise.client.networking.DataHandler.parsedSuppliersList;


public class SupplierForm {

    private Supplier createdSupplier;

    public Supplier showAndWait() {
        // Create window
        Stage stage = new Stage();
        stage.setTitle("New Supplier");
        stage.initModality(Modality.APPLICATION_MODAL);

        // Form fields
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();

        Button submitBtn = new Button("Create");
        Button cancelBtn = new Button("Cancel");

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);

        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);

        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);

        grid.add(submitBtn, 0, 4);
        grid.add(cancelBtn, 1, 4);

        Scene scene = new Scene(grid);
        String themeFile = App.darkMode ? App.resourceDir+"/stylesheets/stylesheet-dark.css" : App.resourceDir+"/stylesheets/stylesheet-light.css";
        scene.getStylesheets().add(getClass().getResource(themeFile).toExternalForm());
        stage.setScene(scene);

        // Submit button logic
        submitBtn.setOnAction(e -> {
            DataHandler.initTables("Supplier");
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();
            String formattedDate = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

            if (name.isEmpty() || email.isEmpty() ||  phone.isEmpty() || address.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "All fields are required.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            createdSupplier = new Supplier(name, email, phone, address);
            createdSupplier.setCreatedAt(formattedDate);
            stage.close();
        });

        // Cancel logic
        cancelBtn.setOnAction(e -> {
            createdSupplier = null;
            stage.close();
        });
        stage.showAndWait();
        return createdSupplier;
    }
}
