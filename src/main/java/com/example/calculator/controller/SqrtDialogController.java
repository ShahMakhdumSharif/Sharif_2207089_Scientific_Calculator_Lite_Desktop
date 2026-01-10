package com.example.calculator.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class SqrtDialogController implements Initializable {
    @FXML private TextField valueField;
    private Stage stage;
    private String result = null;
    @Override public void initialize(URL location, ResourceBundle resources) {}
    public void setStage(Stage s) { this.stage = s; }
    public String getResult() { return result; }
    @FXML protected void onOk(ActionEvent e) {
        String expr = (valueField.getText()==null?"0":valueField.getText().trim());
        try { result = com.example.calculator.controller.Calculation.squareroot.computeFromString(expr); } catch (Exception ex) { result = "Syntax Error"; }
        if (stage!=null) stage.close();
    }
    @FXML protected void onCancel(ActionEvent e) { result=null; if (stage!=null) stage.close(); }
}
