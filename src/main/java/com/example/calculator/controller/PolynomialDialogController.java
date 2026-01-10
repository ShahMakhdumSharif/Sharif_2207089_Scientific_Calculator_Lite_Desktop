package com.example.calculator.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class PolynomialDialogController implements Initializable {

    @FXML private ChoiceBox<Integer> degreeChoice;
    @FXML private TextField c0, c1, c2, c3, xField;

    private Stage stage;
    private String result = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        degreeChoice.getItems().addAll(1,2,3);
        degreeChoice.setValue(1);
    }

    public void setStage(Stage s) { this.stage = s; }
    public String getResult() { return result; }

    @FXML
    protected void onOk(ActionEvent e) {
        int deg = degreeChoice.getValue();
        StringBuilder coeffs = new StringBuilder();
        coeffs.append((c0.getText()==null||c0.getText().isBlank())?"0":c0.getText().trim());
        if (deg>=1) coeffs.append(',').append((c1.getText()==null||c1.getText().isBlank())?"0":c1.getText().trim());
        if (deg>=2) coeffs.append(',').append((c2.getText()==null||c2.getText().isBlank())?"0":c2.getText().trim());
        if (deg>=3) coeffs.append(',').append((c3.getText()==null||c3.getText().isBlank())?"0":c3.getText().trim());
        String expr = coeffs.toString() + "@" + (xField.getText()==null?"0":xField.getText().trim());
        try {
            result = com.example.calculator.controller.Calculation.polynomial.computeFromString(expr, deg);
        } catch (Exception ex) { result = "Syntax Error"; }
        if (stage != null) stage.close();
    }

    @FXML
    protected void onCancel(ActionEvent e) { result = null; if (stage!=null) stage.close(); }
}
