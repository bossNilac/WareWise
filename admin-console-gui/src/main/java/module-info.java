module com.warewise.gui.controller {
    requires javafx.fxml;
    requires org.jfxtras.styles.jmetro;
    requires org.controlsfx.controls;
    requires java.desktop;


    opens com.warewise.gui.controller to javafx.fxml;
    exports com.warewise.gui.controller;
    exports com.warewise.gui;
    opens com.warewise.gui to javafx.fxml;
}