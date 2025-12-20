package com.example.calculator.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
 

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

    @FXML
    protected void handleEquals(ActionEvent event) {
        if (UserDisplayButton == null) return;
        String expr = UserDisplayButton.getText();
        if (expr == null || expr.isBlank()) return;

        expr = expr.trim();
        while (!expr.isEmpty() && "+-*/".indexOf(expr.charAt(expr.length() - 1)) >= 0) {
            expr = expr.substring(0, expr.length() - 1).trim();
        }

        if (expr.isEmpty()) {
            UserDisplayButton.setText("Syntax Error");
            return;
        }

        List<String> tokens;
        try {
            tokens = preprocessTokens(tokenize(expr));
        } catch (IllegalArgumentException ex) {
            UserDisplayButton.setText("Syntax Error");
            return;
        }

        try {
            List<String> rpn = toRPN(tokens);
            double result = evalRPN(rpn);
            String out;
            if (Math.abs(result - Math.round(result)) < 1e-10) {
                out = String.valueOf((long) Math.round(result));
            } else {
                DecimalFormat df = new DecimalFormat("0.##########");
                out = df.format(result);
            }
            UserDisplayButton.setText(out);
        } catch (Exception e) {
            UserDisplayButton.setText("Syntax Error");
        }
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        int idx = 0;
        while (idx < expr.length()) {
            char c = expr.charAt(idx);
            if (Character.isWhitespace(c)) { idx++; continue; }
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')') {
                tokens.add(String.valueOf(c));
                idx++;
            } else {
                int j = idx;
                while (j < expr.length() && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) j++;
                tokens.add(expr.substring(idx, j));
                idx = j;
            }
        }
        return tokens;
    }

   
    private List<String> preprocessTokens(List<String> tokens) {
        List<String> merged = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String tk = tokens.get(i);
            if ("-".equals(tk) || "+".equals(tk)) {
                boolean isUnary = (i == 0) || tokens.get(i - 1).matches("[+\\-*/(]");
                if (isUnary) {
                    if (i + 1 < tokens.size() && tokens.get(i + 1).matches("^[0-9]*\\.?[0-9]+$")) {
                        String num = tokens.get(i + 1);
                        if ("-".equals(tk)) merged.add("-" + num);
                        else merged.add(num);
                        i++; 
                        continue;
                    } else {
                        throw new IllegalArgumentException("Invalid unary operator");
                    }
                }
            }
            merged.add(tk);
        }

        boolean expectNumber = true;
        int paren = 0;
        for (String tk : merged) {
            if (expectNumber) {
                if (tk.matches("^[+-]?[0-9]*\\.?[0-9]+$")) {
                    expectNumber = false;
                } else if ("(".equals(tk)) {
                    paren++;
                    expectNumber = true;
                } else {
                    throw new IllegalArgumentException("Unexpected token: " + tk);
                }
            } else {
                if (tk.matches("[+\\-*/]")) {
                    expectNumber = true;
                } else if (")".equals(tk)) {
                    paren--;
                    if (paren < 0) throw new IllegalArgumentException("Mismatched parentheses");
                    expectNumber = false;
                } else {
                    throw new IllegalArgumentException("Unexpected token: " + tk);
                }
            }
        }
        if (paren != 0) throw new IllegalArgumentException("Mismatched parentheses");
        if (expectNumber) throw new IllegalArgumentException("Expression ends with operator");
        return merged;
    }

    private int precedence(String op) {
        switch (op) {
            case "+":
            case "-": return 1;
            case "*":
            case "/": return 2;
        }
        return 0;
    }

    private List<String> toRPN(List<String> tokens) {
        List<String> out = new ArrayList<>();
        Stack<String> ops = new Stack<>();
        for (String t : tokens) {
            if (t.matches("^[+-]?[0-9]*\\.?[0-9]+$")) {
                out.add(t);
            } else if (t.equals("(")) {
                ops.push(t);
            } else if (t.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) out.add(ops.pop());
                if (!ops.isEmpty() && ops.peek().equals("(")) ops.pop();
            } else if (t.matches("[+\\-*/]")) {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(t)) {
                    out.add(ops.pop());
                }
                ops.push(t);
            }
        }
        while (!ops.isEmpty()) out.add(ops.pop());
        return out;
    }

    private double evalRPN(List<String> rpn) {
        Stack<Double> st = new Stack<>();
        for (String t : rpn) {
            if (t.matches("^[+-]?[0-9]*\\.?[0-9]+$")) {
                st.push(Double.parseDouble(t));
            } else {
                double b = st.pop();
                double a = st.isEmpty() ? 0 : st.pop();
                switch (t) {
                    case "+": st.push(a + b); break;
                    case "-": st.push(a - b); break;
                    case "*": st.push(a * b); break;
                    case "/": st.push(a / b); break;
                }
            }
        }
        return st.isEmpty() ? 0 : st.pop();
    }

    public void setUsername(String username) {
        if (UserWelcomeLabel != null) {
            UserWelcomeLabel.setText("Welcome " + username);
            UserWelcomeLabel.setAlignment(javafx.geometry.Pos.CENTER);
        }
    }
}
