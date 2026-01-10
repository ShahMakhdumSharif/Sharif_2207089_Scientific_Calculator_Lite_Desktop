package com.example.calculator.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class MatrixDialogController implements Initializable {

    @FXML private ChoiceBox<Integer> sizeChoice;
    @FXML private ChoiceBox<String> opChoice;
    @FXML private GridPane gridA;
    @FXML private GridPane gridB;

    private Stage stage;
    private String result = null;
    private java.util.function.Consumer<String> resultConsumer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sizeChoice.getItems().addAll(2,3);
        sizeChoice.setValue(2);
        opChoice.getItems().addAll("ADD","SUB","MUL");
        opChoice.setValue("ADD");
        rebuildGrids(2);
        sizeChoice.getSelectionModel().selectedItemProperty().addListener((obs,oldV,newV)-> rebuildGrids(newV));
    }

    private void rebuildGrids(int n) {
        gridA.getChildren().clear();
        gridB.getChildren().clear();
        for (int r=0;r<n;r++) for (int c=0;c<n;c++) {
            TextField t1 = new TextField(); t1.setPrefWidth(60);
            TextField t2 = new TextField(); t2.setPrefWidth(60);
            gridA.add(t1,c,r);
            gridB.add(t2,c,r);
        }
    }

    public void setStage(Stage s) { this.stage = s; }

    public String getResult() { return result; }

    public void setResultConsumer(java.util.function.Consumer<String> c) { this.resultConsumer = c; }

    @FXML
    protected void onOk(ActionEvent e) {
        int n = sizeChoice.getValue();
        StringBuilder aSb = new StringBuilder();
        StringBuilder bSb = new StringBuilder();
        for (int r=0;r<n;r++) {
            for (int c=0;c<n;c++) {
                TextField tfA = (TextField) getNodeFromGrid(gridA,c,r);
                TextField tfB = (TextField) getNodeFromGrid(gridB,c,r);
                aSb.append(tfA.getText()==null?"0":tfA.getText().trim());
                bSb.append(tfB.getText()==null?"0":tfB.getText().trim());
                if (c < n-1) { aSb.append(','); bSb.append(','); }
            }
            if (r < n-1) { aSb.append(';'); bSb.append(';'); }
        }
        String expr = aSb.toString() + "|" + bSb.toString();
        String op = opChoice.getValue();
        try {
            result = com.example.calculator.controller.Calculation.matrix.computeFromString(expr, n, op);
        } catch (Exception ex) {
            result = "Syntax Error";
        }
        if (resultConsumer != null) resultConsumer.accept(result);
        if (stage != null) stage.close();
    }

    @FXML
    protected void onCancel(ActionEvent e) {
        result = null;
        if (resultConsumer != null) resultConsumer.accept(result);
        if (stage != null) stage.close();
    }

    private Node getNodeFromGrid(GridPane grid, int col, int row) {
        for (Node n: grid.getChildren()) {
            Integer c = GridPane.getColumnIndex(n); if (c==null) c=0;
            Integer r = GridPane.getRowIndex(n); if (r==null) r=0;
            if (c==col && r==row) return n;
        }
        return null;
    }
}
