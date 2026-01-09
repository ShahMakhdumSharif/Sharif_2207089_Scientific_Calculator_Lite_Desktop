package com.example.calculator.controller;

import com.example.calculator.database.UserDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class LimitOperationsController {

    @FXML
    private Label targetUserLabel;

    @FXML
    private Button opAdd;
    @FXML
    private Button opSub;
    @FXML
    private Button opMul;
    @FXML
    private Button opDiv;
    @FXML
    private Button opSqrt;
    @FXML
    private Button opPower;
    @FXML
    private Button opMatrix;
    @FXML
    private Button opPolynomial;
    @FXML
    private Button opLinear;
    @FXML
    private Button opX10;
    @FXML
    private Button opClear;

    @FXML
    private Button backButton;

    private String username;
    private int userId = -1;

    public void setUser(String username, int userId) {
        this.username = username;
        this.userId = userId;
        if (targetUserLabel != null) targetUserLabel.setText("Limit operations for: " + username);
    }

    @FXML
    protected void initialize() {
        // wire buttons
        opAdd.setOnAction(e -> assignOp("+"));
        opSub.setOnAction(e -> assignOp("-"));
        opMul.setOnAction(e -> assignOp("*"));
        opDiv.setOnAction(e -> assignOp("/"));
        opSqrt.setOnAction(e -> assignOp("sqrt(x)"));
        opPower.setOnAction(e -> assignOp("x^"));
        opMatrix.setOnAction(e -> assignOp("MATRIX"));
        opPolynomial.setOnAction(e -> assignOp("POLYNOMIAL"));
        opLinear.setOnAction(e -> assignOp("LINEAR"));
    opX10.setOnAction(e -> assignOp("*10^x"));
        opClear.setOnAction(e -> clearAssigned());
        backButton.setOnAction(this::goBack);
    }

    private void assignOp(String op) {
        if (userId == -1) return;
        try {
            boolean ok = UserDatabase.setAssignedOperation(userId, op);
            if (ok) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Assigned");
                a.setHeaderText(null);
                a.setContentText("Assigned operation '" + op + "' to " + username);
                a.showAndWait();
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Failed");
                a.setHeaderText(null);
                a.setContentText("Failed to assign operation.");
                a.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearAssigned() {
        if (userId == -1) return;
        try {
            boolean ok = UserDatabase.clearAssignedOperation(userId);
            Alert a = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            a.setTitle(ok ? "Cleared" : "Failed");
            a.setHeaderText(null);
            a.setContentText(ok ? "Cleared assigned operation for " + username : "Failed to clear assigned operation.");
            a.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goBack(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/AdminLimitOperation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene currentScene = stage.getScene();
            if (currentScene != null) {
                currentScene.setRoot(root);
            } else {
                stage.setScene(new Scene(root));
            }
            stage.setTitle("Admin - Limit Operation");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
