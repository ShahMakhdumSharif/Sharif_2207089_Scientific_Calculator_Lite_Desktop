package com.example.calculator.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class UserInterfaceController {

    @FXML
    private Button UserLogoutButton;

    @FXML
    private TextField UserDisplayButton;

    @FXML
    private Label UserWelcomeLabel;

    @FXML
    private GridPane UserGridPane;

    @FXML
    private Button UserACButton;

    @FXML
    private Button UserDELButton;

    @FXML
    protected void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) UserLogoutButton.getScene().getWindow();
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
    protected void handleDigit(ActionEvent event) {
        Object src = event.getSource();
        if (src instanceof Button) {
            String digit = ((Button) src).getText();
            String current = UserDisplayButton.getText();
            if (current == null)
                current = "";
            UserDisplayButton.setText(current + digit);
        }
    }

    @FXML
    protected void handleOperator(ActionEvent event) {
        Object src = event.getSource();
        if (src instanceof Button) {
            String op = ((Button) src).getText();
            String current = UserDisplayButton.getText();
            if (current == null)
                current = "";
            UserDisplayButton.setText(current + " " + op + " ");
        }
    }

    @FXML
    public void initialize() {
        if (UserDisplayButton != null)
            UserDisplayButton.setText("");
    }

    @FXML
    protected void handleAC(ActionEvent event) {
        if (UserDisplayButton != null)
            UserDisplayButton.setText("");
    }

    @FXML
    protected void handleDel(ActionEvent event) {
        if (UserDisplayButton == null)
            return;
        String t = UserDisplayButton.getText();
        if (t == null || t.isEmpty())
            return;
        UserDisplayButton.setText(t.substring(0, t.length() - 1));
    }

    public void setUsername(String username) {
        if (UserWelcomeLabel != null) {
            UserWelcomeLabel.setText("Welcome " + username);
            UserWelcomeLabel.setAlignment(javafx.geometry.Pos.CENTER);
        }
    }
}
