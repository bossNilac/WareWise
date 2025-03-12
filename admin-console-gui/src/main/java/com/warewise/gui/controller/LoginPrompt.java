package com.warewise.gui.controller;

import com.warewise.gui.util.AdminUtil;
import com.warewise.gui.util.UtilityCommands;
import javax.swing.*;

public class LoginPrompt {
    /**
     * Utility method to retrieve login credentials using Swing (No JavaFX Thread Issues)
     */
    public static String[] promptLogin() {
        JPanel panel = new JPanel();
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Setup Login Credentials",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return new String[]{usernameField.getText(), new String(passwordField.getPassword())};
        } else {
            // Handle Cancel button
            UtilityCommands.displayNotificationPanel(3, "Login canceled, closing server.");
            AdminUtil.closeServer(ServerApplication.getNetworkingObject());
            return null;
        }
    }
}
