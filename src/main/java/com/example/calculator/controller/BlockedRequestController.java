package com.example.calculator.controller;

import com.example.calculator.database.UserDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;

public class BlockedRequestController {

    @FXML
    private Label blockedMessageLabel;

    @FXML
    private TextArea requestMessageArea;

    @FXML
    private Button sendRequestButton;

    @FXML
    private Button logoutButton;

    private String username;
    private int userId = -1;

    public void setUsername(String username) {
        this.username = username;
        if (blockedMessageLabel != null) {
            blockedMessageLabel.setText("Your account '" + username + "' has been blocked. You can request the admin to unblock your account.");
        }
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    @FXML
    protected void handleSendRequest(ActionEvent event) {
        String msg = requestMessageArea.getText();
        if (msg == null || msg.isBlank()) {
            msg = "Please review my account; I believe it was blocked in error.";
        }
        try {
            boolean ok = UserDatabase.createUnblockRequest(userId, msg);
            if (ok) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Request Sent");
                a.setHeaderText(null);
                a.setContentText("Your unblock request was sent to the admin.");
                a.showAndWait();
                sendRequestButton.setDisable(true);
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Request Failed");
                a.setHeaderText(null);
                a.setContentText("Failed to send unblock request.");
                a.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Request Error");
            a.setHeaderText(null);
            a.setContentText("Error sending request: " + e.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    protected void handleLogout(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/calculator/login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) logoutButton.getScene().getWindow();
            javafx.scene.Scene currentScene = stage.getScene();
            if (currentScene != null) {
                currentScene.setRoot(root);
            } else {
                javafx.scene.Scene scene = new javafx.scene.Scene(root);
                stage.setScene(scene);
            }
            stage.setTitle("Login - Scientific Calculator Lite");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
