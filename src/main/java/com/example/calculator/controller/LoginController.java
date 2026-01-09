package com.example.calculator.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.calculator.database.UserDatabase;

public class LoginController implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            UserDatabase.init();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to initialize user database: " + e.getMessage());
        }
    }

    @FXML
    public void handleUserLogin() {
        String u = userUsernameField.getText();
        String p = userPasswordField.getText();
        if (u == null || u.isBlank() || p == null || p.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Enter username and password.");
            return;
        }
        try {
            com.example.calculator.model.UserInfo ui = UserDatabase.authenticateUser(u, p);
            if (ui == null) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid user information.");
                return;
            }
            boolean blocked = UserDatabase.isUserBlocked(u);
            if (blocked) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/BlockedRequest.fxml"));
                    Parent root = loader.load();
                    Object controller = loader.getController();
                    if (controller instanceof com.example.calculator.controller.BlockedRequestController) {
                        ((com.example.calculator.controller.BlockedRequestController) controller).setUsername(u);
                        ((com.example.calculator.controller.BlockedRequestController) controller).setUserId(ui.getId());
                    }
                    Stage stage = (Stage) userUsernameField.getScene().getWindow();
                    Scene currentScene = stage.getScene();
                    if (currentScene != null) {
                        currentScene.setRoot(root);
                    } else {
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                    }
                    stage.setTitle("Account Blocked - Scientific Calculator Lite");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load blocked request page: " + e.getMessage());
                }
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/UserInterface.fxml"));
                Parent root = loader.load();
                Object controller = loader.getController();
                if (controller instanceof com.example.calculator.controller.UserInterfaceController) {
                    ((com.example.calculator.controller.UserInterfaceController) controller).setUsername(u);
                }
                Stage stage = (Stage) userUsernameField.getScene().getWindow();
                Scene currentScene = stage.getScene();
                if (currentScene != null) {
                    currentScene.setRoot(root);
                } else {
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                }
                stage.setTitle("User - Scientific Calculator Lite");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load user interface: " + e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error checking user: " + e.getMessage());
        }
    }

    @FXML
    public void handleAdminLogin() {
        String p = adminPasswordField.getText();
        if (p == null) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Enter admin password.");
            return;
        }
        try {
            if (UserDatabase.validateAdminByPassword(p)) {
                // load admin page
                try {
                    java.util.List<com.example.calculator.model.UnblockRequest> requests = UserDatabase.getPendingUnblockRequests();
                    if (!requests.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (com.example.calculator.model.UnblockRequest r : requests) {
                            sb.append(r.getUsername()).append(": ");
                            String msg = r.getMessage();
                            if (msg == null || msg.isBlank()) msg = "(no message)";
                            sb.append(msg).append("\n\n");
                        }
                        javafx.scene.control.Alert info = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                        info.setTitle("Unblock Requests");
                        info.setHeaderText("Pending unblock requests");
                        javafx.scene.control.TextArea ta = new javafx.scene.control.TextArea(sb.toString());
                        ta.setEditable(false);
                        ta.setWrapText(true);
                        ta.setMaxWidth(Double.MAX_VALUE);
                        ta.setMaxHeight(Double.MAX_VALUE);
                        info.getDialogPane().setExpandableContent(ta);
                        info.getDialogPane().setExpanded(true);
                        info.showAndWait();
                    }

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/Admin.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) adminPasswordField.getScene().getWindow();
                    Scene currentScene = stage.getScene();
                    if (currentScene != null) {
                        currentScene.setRoot(root);
                    } else {
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                    }
                    stage.setTitle("Admin - Scientific Calculator Lite");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load admin page: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid admin information.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error checking admin: " + e.getMessage());
        }
    }

    @FXML
    public void handleRegister() {
        String username = registerUsernameField.getText();
        String password = registerPasswordField.getText();
        String ReEnterredPass = registerVerifyPasswordField.getText();

        if (username == null || username.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username cannot be empty.");
            return;
        }
        if (password == null || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Password cannot be empty.");
            return;
        }
        if (!password.equals(ReEnterredPass)) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Passwords do not match.");
            return;
        }

        try {
            boolean ok = UserDatabase.registerUser(username, password);
            if (ok) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User registered successfully.");
                registerUsernameField.clear();
                registerPasswordField.clear();
                registerVerifyPasswordField.clear();
                if (tabPane != null) tabPane.getSelectionModel().select(0);
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Could not register user.");
            }
        } catch (SQLException e) {
            //ei user ageo register krse
            String msg = e.getMessage();
            if (msg != null && msg.toLowerCase().contains("constraint")) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists.");
            } else {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error registering user: " + e.getMessage());
            }
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
