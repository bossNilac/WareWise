module com.warewise.gui.controller {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;


    opens com.warewise.gui.controller to javafx.fxml;
    exports com.warewise.gui.controller;
    exports com.warewise.gui;
    opens com.warewise.gui to javafx.fxml;
}