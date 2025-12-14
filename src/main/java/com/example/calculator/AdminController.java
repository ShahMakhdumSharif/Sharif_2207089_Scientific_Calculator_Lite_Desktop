package com.example.calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    @FXML
    private Button AdminLogoutButton;

    @FXML
    private Button AdminAllUserButton;

    @FXML
    protected void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) AdminLogoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Login - Scientific Calculator Lite");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleAllUsers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/AdminToUserInfo.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) AdminAllUserButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Admin - All Users");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
