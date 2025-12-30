package com.example.calculator.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import com.example.calculator.database.UserDatabase;
import com.example.calculator.model.UserInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminHistoryController implements Initializable {
    @FXML
    private Button AdminHistoryBackButton;
    @FXML
    private Button AdminHistoryLogoutButton;

    @FXML
    private TableView<UserInfo> historyTable;

    @FXML
    private TableColumn<UserInfo, String> historyUsernameCol;

    @FXML
    protected void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) AdminHistoryLogoutButton.getScene().getWindow();
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
    protected void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/Admin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) AdminHistoryBackButton.getScene().getWindow();
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
            historyUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
            historyTable.setItems(items);

            javafx.util.Callback<TableColumn<UserInfo, String>, javafx.scene.control.TableCell<UserInfo, String>> cellFactory = new javafx.util.Callback<>() {
                @Override
                public javafx.scene.control.TableCell<UserInfo, String> call(final TableColumn<UserInfo, String> param) {
                    final javafx.scene.control.TableCell<UserInfo, String> cell = new javafx.scene.control.TableCell<>() {
                        private final javafx.scene.control.Button btn = new javafx.scene.control.Button();

                        {
                            btn.setOnAction((ActionEvent event) -> {
                                UserInfo data = getTableView().getItems().get(getIndex());
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/UserHistory.fxml"));
                                    Parent root = loader.load();
                                    Object controller = loader.getController();
                                    if (controller instanceof com.example.calculator.controller.UserHistoryController) {
                                        com.example.calculator.controller.UserHistoryController uh = (com.example.calculator.controller.UserHistoryController) controller;
                                        uh.setUsername(data.getUsername());
                                        uh.setOpenedFromAdmin(true);
                                    }
                                    Stage stage = (Stage) btn.getScene().getWindow();
                                    Scene currentScene = stage.getScene();
                                    if (currentScene != null) {
                                        currentScene.setRoot(root);
                                    } else {
                                        Scene scene = new Scene(root);
                                        stage.setScene(scene);
                                    }
                                    stage.setTitle("User History - " + data.getUsername());
                                    stage.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                btn.setText(item == null ? "" : item);
                                setGraphic(btn);
                            }
                        }
                    };
                    return cell;
                }
            };

            historyUsernameCol.setCellFactory(cellFactory);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
