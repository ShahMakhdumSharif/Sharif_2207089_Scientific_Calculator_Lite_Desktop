package com.example.calculator.controller;

import com.example.calculator.database.UserDatabase;
import com.example.calculator.model.CalculationHistory;
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
import java.util.List;
import java.util.ResourceBundle;

public class UserHistoryController implements Initializable {

    @FXML
    private Button UserHistoryBackButton;

    @FXML
    private Button UserHistoryLogoutButton;

    @FXML
    private TableView<CalculationHistory> historyTable;

    @FXML
    private TableColumn<CalculationHistory, String> expressionCol;

    @FXML
    private TableColumn<CalculationHistory, String> resultCol;

    private String username;
    private boolean openedFromAdmin = false;

    @FXML
    protected void handleBack(ActionEvent event) {
        try {
            String fxml = openedFromAdmin ? "/com/example/calculator/AdminHistory.fxml" : "/com/example/calculator/UserInterface.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            if (!openedFromAdmin) {
                Object controller = loader.getController();
                if (controller instanceof com.example.calculator.controller.UserInterfaceController) {
                    ((com.example.calculator.controller.UserInterfaceController) controller).setUsername(username);
                }
            }
            Stage stage = (Stage) UserHistoryBackButton.getScene().getWindow();
            Scene currentScene = stage.getScene();
            if (currentScene != null) {
                currentScene.setRoot(root);
            } else {
                Scene scene = new Scene(root);
                stage.setScene(scene);
            }
            stage.setTitle(openedFromAdmin ? "Admin - Scientific Calculator Lite" : "User - Scientific Calculator Lite");
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
            Stage stage = (Stage) UserHistoryLogoutButton.getScene().getWindow();
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
        // default
    }

    public void setUsername(String username) {
        this.username = username;
        if (historyTable != null) loadHistory();
    }

    public void setOpenedFromAdmin(boolean v) {
        this.openedFromAdmin = v;
    }

    private void loadHistory() {
        try {
            UserInfo ui = UserDatabase.getUserByUsername(username);
            if (ui == null) return;
            List<CalculationHistory> rows = UserDatabase.getHistoryForUser(ui.getId());
            ObservableList<CalculationHistory> items = FXCollections.observableArrayList(rows);
            expressionCol.setCellValueFactory(new PropertyValueFactory<>("expression"));
            resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));
            historyTable.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
