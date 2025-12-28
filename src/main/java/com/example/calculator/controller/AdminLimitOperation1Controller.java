package com.example.calculator.controller;

import com.example.calculator.database.UserDatabase;
import com.example.calculator.model.UserInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminLimitOperation1Controller implements Initializable{
    @FXML
    private Button AdminHistory1BackButton;
    @FXML
    private Button AdminHistory1LogoutButton;

    @FXML
    private TableView<UserInfo> limitTable;

    @FXML
    private TableColumn<UserInfo, String> limitUsernameCol;

    @FXML
    protected void handleLogout1(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) AdminHistory1LogoutButton.getScene().getWindow();
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

    @FXML
    protected void handleBack1(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/Admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) AdminHistory1BackButton.getScene().getWindow();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            java.util.List<UserInfo> users = UserDatabase.getAllNonAdminUsers();
            ObservableList<UserInfo> items = FXCollections.observableArrayList(users);
            limitUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
            limitTable.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
