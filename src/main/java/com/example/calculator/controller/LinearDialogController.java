package com.example.calculator.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.net.URL;
import java.util.ResourceBundle;

public class LinearDialogController implements Initializable {
    @FXML private ChoiceBox<Integer> sizeChoice;
    @FXML private GridPane augGrid;

    private Stage stage;
    private String result = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sizeChoice.getItems().addAll(2,3);
        sizeChoice.setValue(2);
        rebuildGrid(2);
        sizeChoice.getSelectionModel().selectedItemProperty().addListener((obs,oldV,newV)-> rebuildGrid(newV));
    }

    private void rebuildGrid(int n) {
        augGrid.getChildren().clear();
        for (int r=0;r<n;r++) for (int c=0;c<n+1;c++) {
            TextField t = new TextField(); t.setPrefWidth(80);
            augGrid.add(t,c,r);
        }
    }

    public void setStage(Stage s) { this.stage = s; }
    public String getResult() { return result; }

    @FXML
    protected void onOk(ActionEvent e) {
        int n = sizeChoice.getValue();
        StringBuilder sb = new StringBuilder();
        for (int r=0;r<n;r++) {
            for (int c=0;c<n+1;c++) {
                TextField tf = (TextField) getNodeFromGrid(augGrid,c,r);
                sb.append(tf.getText()==null?"0":tf.getText().trim());
                if (c < n) sb.append(',');
            }
            if (r < n-1) sb.append(';');
        }
        try {
            result = com.example.calculator.controller.Calculation.linear.computeFromString(sb.toString());
        } catch (Exception ex) { result = "Syntax Error"; }
        if (stage!=null) stage.close();
    }

    @FXML
    protected void onCancel(ActionEvent e) { result = null; if (stage!=null) stage.close(); }

    private Node getNodeFromGrid(GridPane grid, int col, int row) {
        for (Node n: grid.getChildren()) {
            Integer c = GridPane.getColumnIndex(n); if (c==null) c=0;
            Integer r = GridPane.getRowIndex(n); if (r==null) r=0;
            if (c==col && r==row) return n;
        }
        return null;
    }
}
