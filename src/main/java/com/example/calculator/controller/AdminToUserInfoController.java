package com.example.calculator.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import com.example.calculator.database.UserDatabase;
import com.example.calculator.model.UserInfo;

public class AdminToUserInfoController implements Initializable {

    @FXML
    private Button AdminUserInfoBackButton;

    @FXML
    private Button AdminUserInfoLogoutButton;

    @FXML
    private TableView<UserInfo> userTable;

    @FXML
    private TableColumn<UserInfo, String> usernameCol;

    @FXML
    private TableColumn<UserInfo, String> passwordCol;

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
            List<UserInfo> users = UserDatabase.getAllNonAdminUsers();
            if (userTable != null) {
                ObservableList<UserInfo> items = FXCollections.observableArrayList(users);
                usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
                passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
                userTable.setItems(items);
                System.out.println("AdminToUserInfo: loaded " + items.size() + " users");
                if (items.isEmpty()) {
                    userTable.setPlaceholder(new Label("No users found"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Database Error");
            a.setHeaderText(null);
            a.setContentText("Failed to load users: " + e.getMessage());
            a.showAndWait();
        }
    }
}
