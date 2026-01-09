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
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;

public class AdminBlocklistController implements Initializable {
    @FXML
    private Button AdminBlocklistBackButton;
    @FXML
    private Button AdminBlocklistLogoutButton;

    @FXML
    private TableView<UserInfo> blocklistTable;

    @FXML
    private TableColumn<UserInfo, String> blocklistUsernameCol;

    @FXML
    private TableColumn<UserInfo, Void> blocklistUnblockCol;
    @FXML
    protected void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) AdminBlocklistLogoutButton.getScene().getWindow();
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
            Stage stage = (Stage) AdminBlocklistBackButton.getScene().getWindow();
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
            java.util.List<UserInfo> users = UserDatabase.getBlockedUsers();
            ObservableList<UserInfo> items = FXCollections.observableArrayList(users);
            blocklistUsernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
            blocklistTable.setItems(items);

            Callback<TableColumn<UserInfo, Void>, TableCell<UserInfo, Void>> cellFactory = new Callback<>() {
                @Override
                public TableCell<UserInfo, Void> call(final TableColumn<UserInfo, Void> param) {
                    final TableCell<UserInfo, Void> cell = new TableCell<>() {
                        private final javafx.scene.control.Button btn = new javafx.scene.control.Button("Unblock");

                        {
                            btn.setOnAction((ActionEvent event) -> {
                                UserInfo data = getTableView().getItems().get(getIndex());
                                Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Unblock user '" + data.getUsername() + "'?", ButtonType.YES, ButtonType.NO);
                                a.setHeaderText(null);
                                a.showAndWait().ifPresent(bt -> {
                                    if (bt == ButtonType.YES) {
                                        try {
                                                    boolean ok = UserDatabase.unblockUserByUsername(data.getUsername());
                                                    if (ok) {
                                                        // mark any pending unblock requests for this user as processed so admin won't see them again
                                                        try {
                                                            UserDatabase.markUnblockRequestsProcessedForUser(data.getId());
                                                        } catch (Exception ex) {
                                                            ex.printStackTrace();
                                                        }
                                                        getTableView().getItems().remove(data);
                                                    } else {
                                                        Alert err = new Alert(Alert.AlertType.ERROR, "Failed to unblock user.");
                                                        err.setHeaderText(null);
                                                        err.showAndWait();
                                                    }
                                        } catch (SQLException ex) {
                                            ex.printStackTrace();
                                            Alert err = new Alert(Alert.AlertType.ERROR, "DB error while unblocking user.");
                                            err.setHeaderText(null);
                                            err.showAndWait();
                                        }
                                    }
                                });
                            });
                        }

                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btn);
                            }
                        }
                    };
                    return cell;
                }
            };
            blocklistUnblockCol.setCellFactory(cellFactory);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
