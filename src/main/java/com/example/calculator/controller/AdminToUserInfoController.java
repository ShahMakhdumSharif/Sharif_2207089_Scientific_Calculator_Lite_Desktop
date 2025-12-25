package com.example.calculator.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import com.example.calculator.database.UserDatabase;

public class AdminToUserInfoController implements Initializable {

    @FXML
    private Button AdminUserInfoBackButton;

    @FXML
    private Button AdminUserInfoLogoutButton;

    @FXML
    private ListView<String> userListView;

    @FXML
    protected void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/Admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) AdminUserInfoBackButton.getScene().getWindow();
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
        }
    }

    @FXML
    protected void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) AdminUserInfoLogoutButton.getScene().getWindow();
            Scene currentScene = stage.getScene();
            if (currentScene != null) {
                currentScene.setRoot(root);
            } else {
                Scene scene = new Scene(root);
                stage.setScene(scene);
            }
            stage.setTitle("Login - Scientific Calculator Lite");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<String> users = UserDatabase.getAllNonAdminUsernames();
            if (userListView != null) {
                userListView.setItems(FXCollections.observableArrayList(users));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
