package com.example.calculator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;


public class LoginController {

    // User login
    @FXML
    private TextField userUsernameField;

    @FXML
    private PasswordField userPasswordField;

    // Admin login
    @FXML
    private PasswordField adminPasswordField;

    @FXML
    private TabPane tabPane;

    // Register User
    @FXML
    private TextField registerUsernameField;

    @FXML
    private TextField registerPasswordField;

    @FXML
    private TextField registerVerifyPasswordField;

    private static final String ADMIN_PASS = "admin123";


    @FXML
    public void handleUserLogin() {
        String u = userUsernameField.getText();
        String p = userPasswordField.getText();

    }

    @FXML
    public void handleAdminLogin() {
        String p = adminPasswordField.getText();
        if (ADMIN_PASS.equals(p)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Admin login successful.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid admin credentials.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
