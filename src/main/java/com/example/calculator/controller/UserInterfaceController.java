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
import java.util.Locale;
 

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
    private Label UserDirectionLabel;

    @FXML
    private Button UserACButton;

    @FXML
    private Button UserDELButton;

    @FXML
    private Button UserHistoryButton;

    private String currentUsername = null;
    private int currentUserId = -1;

    // advanced operation modes
    private enum Mode { NORMAL, MATRIX, POLYNOMIAL, LINEAR, POWER, SQRT }
    private Mode currentMode = Mode.NORMAL;

    // matrix state
    private int matrixSize = 2; // 2 or 3
    private String matrixOp = "ADD"; // ADD, SUB, MUL

    // polynomial state
    private int polyDegree = 1; // 1..3

    // linear system state
    private int linearN = 2; // 2 or 3

    // power/sqrt handled simply

    // navigator buttons (present in FXML)
    @FXML private javafx.scene.control.Button UserLeftShiftButton;
    @FXML private javafx.scene.control.Button UserRightShiftButton;
    @FXML private javafx.scene.control.Button UserUpShiftButton;
    @FXML private javafx.scene.control.Button UserDownShiftButton;
    @FXML private javafx.scene.control.Button UserMODEButton;
    @FXML private javafx.scene.control.Button UserMatrixButton;
    @FXML private javafx.scene.control.Button UserPolynomialButton;
    @FXML private javafx.scene.control.Button UserLinearButton;
    @FXML private javafx.scene.control.Button UserPowerButton;
    @FXML private javafx.scene.control.Button UserSqrtButton;

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
    protected void handleMode(ActionEvent event) {
        switch (currentMode) {
            case NORMAL: currentMode = Mode.MATRIX; break;
            case MATRIX: currentMode = Mode.POLYNOMIAL; break;
            case POLYNOMIAL: currentMode = Mode.LINEAR; break;
            case LINEAR: currentMode = Mode.POWER; break;
            case POWER: currentMode = Mode.SQRT; break;
            default: currentMode = Mode.NORMAL; break;
        }
        updateModeDisplay();
    }

    @FXML
    protected void handleUpShift(ActionEvent event) {
        switch (currentMode) {
            case MATRIX:
                matrixSize = (matrixSize == 2) ? 3 : 2;
                break;
            case POLYNOMIAL:
                polyDegree = Math.min(3, polyDegree + 1);
                break;
            case LINEAR:
                linearN = Math.min(3, linearN + 1);
                break;
            default:
        }
        updateModeDisplay();
    }

    @FXML
    protected void handleDownShift(ActionEvent event) {
        switch (currentMode) {
            case MATRIX:
                matrixSize = (matrixSize == 3) ? 2 : 3;
                break;
            case POLYNOMIAL:
                polyDegree = Math.max(1, polyDegree - 1);
                break;
            case LINEAR:
                linearN = Math.max(2, linearN - 1);
                break;
            default:
        }
        updateModeDisplay();
    }

    @FXML
    protected void handleLeftShift(ActionEvent event) {
        switch (currentMode) {
            case MATRIX:
                matrixOp = matrixOp.equals("ADD") ? "MUL" : matrixOp.equals("SUB") ? "ADD" : "SUB";
                break;
            default:
        }
        updateModeDisplay();
    }

    @FXML
    protected void handleRightShift(ActionEvent event) {
        switch (currentMode) {
            case MATRIX:
                matrixOp = matrixOp.equals("ADD") ? "SUB" : matrixOp.equals("SUB") ? "MUL" : "ADD";
                break;
            default:
        }
        updateModeDisplay();
    }

    @FXML
    protected void handleMatrixMode(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/calculator/dialogs/matrix_dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            MatrixDialogController c = loader.getController();
            Stage dialog = new Stage();
            c.setStage(dialog);
            dialog.setTitle("Matrix");
            dialog.initOwner(UserDisplayButton.getScene().getWindow());
            dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialog.setScene(new javafx.scene.Scene(root));
            dialog.showAndWait();
            String res = c.getResult();
            if (res != null) UserDisplayButton.setText(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handlePolynomialMode(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/calculator/dialogs/polynomial_dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            PolynomialDialogController c = loader.getController();
            Stage dialog = new Stage();
            c.setStage(dialog);
            dialog.setTitle("Polynomial");
            dialog.initOwner(UserDisplayButton.getScene().getWindow());
            dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialog.setScene(new javafx.scene.Scene(root));
            dialog.showAndWait();
            String res = c.getResult(); if (res!=null) UserDisplayButton.setText(res);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    protected void handleLinearMode(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/calculator/dialogs/linear_dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            LinearDialogController c = loader.getController();
            Stage dialog = new Stage();
            c.setStage(dialog);
            dialog.setTitle("Linear System");
            dialog.initOwner(UserDisplayButton.getScene().getWindow());
            dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialog.setScene(new javafx.scene.Scene(root));
            dialog.showAndWait();
            String res = c.getResult(); if (res!=null) UserDisplayButton.setText(res);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    protected void handlePowerMode(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/calculator/dialogs/power_dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            PowerDialogController c = loader.getController();
            Stage dialog = new Stage();
            c.setStage(dialog);
            dialog.setTitle("Power");
            dialog.initOwner(UserDisplayButton.getScene().getWindow());
            dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialog.setScene(new javafx.scene.Scene(root));
            dialog.showAndWait();
            String res = c.getResult(); if (res!=null) UserDisplayButton.setText(res);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    protected void handleSqrtMode(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/calculator/dialogs/sqrt_dialog.fxml"));
            javafx.scene.Parent root = loader.load();
            SqrtDialogController c = loader.getController();
            Stage dialog = new Stage();
            c.setStage(dialog);
            dialog.setTitle("Sqrt");
            dialog.initOwner(UserDisplayButton.getScene().getWindow());
            dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialog.setScene(new javafx.scene.Scene(root));
            dialog.showAndWait();
            String res = c.getResult(); if (res!=null) UserDisplayButton.setText(res);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateModeDisplay() {
        if (UserDirectionLabel == null) return;
        switch (currentMode) {
            case NORMAL:
                UserDirectionLabel.setText("Normal calculator mode");
                break;
            case MATRIX:
                UserDirectionLabel.setText(String.format(Locale.US, "MATRIX mode: %dx%d  OP=%s\nEnter two matrices separated by '|' . Rows as comma-separated. Example 2x2: 1,2;3,4|5,6;7,8", matrixSize, matrixSize, matrixOp));
                break;
            case POLYNOMIAL:
                UserDirectionLabel.setText(String.format(Locale.US, "POLYNOMIAL mode: degree=%d\nEnter coefficients c0,c1,...,cN@x (evaluate at x). Example: 1,2,3@2", polyDegree));
                break;
            case LINEAR:
                UserDirectionLabel.setText(String.format(Locale.US, "LINEAR mode: n=%d\nEnter augmented matrix rows separated by ';' and values by commas. Example 2x2: a,b,c;d,e,f", linearN));
                break;
            case POWER:
                UserDirectionLabel.setText("POWER mode: Enter base,exponent (comma-separated) e.g. 2,3");
                break;
            case SQRT:
                UserDirectionLabel.setText("SQRT mode: Enter value and press = to compute sqrt(x)");
                break;
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
            UserDisplayButton.setText(current+ op);
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

        try {
            switch (currentMode) {
                case MATRIX:
                    try {
                        String res = com.example.calculator.controller.Calculation.matrix.computeFromString(expr, matrixSize, matrixOp);
                        UserDisplayButton.setText(res);
                        if (currentUserId != -1) com.example.calculator.database.UserDatabase.logHistory(currentUserId, "MATRIX:"+expr, res);
                    } catch (Exception e) {
                        UserDisplayButton.setText("Syntax Error");
                    }
                    return;
                case POLYNOMIAL:
                    try {
                        String res = com.example.calculator.controller.Calculation.polynomial.computeFromString(expr, polyDegree);
                        UserDisplayButton.setText(res);
                        if (currentUserId != -1) com.example.calculator.database.UserDatabase.logHistory(currentUserId, "POLY:"+expr, res);
                    } catch (Exception e) {
                        UserDisplayButton.setText("Syntax Error");
                    }
                    return;
                case LINEAR:
                    try {
                        String res = com.example.calculator.controller.Calculation.linear.computeFromString(expr);
                        UserDisplayButton.setText(res);
                        if (currentUserId != -1) com.example.calculator.database.UserDatabase.logHistory(currentUserId, "LINEAR:"+expr, res);
                    } catch (Exception e) {
                        UserDisplayButton.setText("Syntax Error");
                    }
                    return;
                case POWER:
                    try {
                        String res = com.example.calculator.controller.Calculation.power.computeFromString(expr);
                        UserDisplayButton.setText(res);
                        if (currentUserId != -1) com.example.calculator.database.UserDatabase.logHistory(currentUserId, "POWER:"+expr, res);
                    } catch (Exception e) {
                        UserDisplayButton.setText("Syntax Error");
                    }
                    return;
                case SQRT:
                    try {
                        String out = com.example.calculator.controller.Calculation.squareroot.computeFromString(expr);
                        UserDisplayButton.setText(out);
                        if (currentUserId != -1) com.example.calculator.database.UserDatabase.logHistory(currentUserId, "sqrt("+expr+")", out);
                    } catch (NumberFormatException ne) {
                        UserDisplayButton.setText("Syntax Error");
                    }
                    return;
                default:
                    break;
            }
        } catch (Exception ex) {
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

            if (currentUserId != -1) {
                try {
                    String exprForLog = expr;
                    com.example.calculator.database.UserDatabase.logHistory(currentUserId, exprForLog, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
        this.currentUsername = username;
        try {
            com.example.calculator.model.UserInfo ui = com.example.calculator.database.UserDatabase.getUserByUsername(username);
            if (ui != null) currentUserId = ui.getId();
        } catch (Exception e) {
            e.printStackTrace();
            currentUserId = -1;
        }
        try {
            if (currentUserId != -1) {
                String op = com.example.calculator.database.UserDatabase.getAssignedOperation(currentUserId);
                if (op != null && !op.isBlank()) {
                    if (UserDirectionLabel != null) UserDirectionLabel.setText("You are asked to do the operation: '" + op + "'");
                } else {
                    if (UserDirectionLabel != null) UserDirectionLabel.setText("You are asked to do the operation: ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/calculator/UserHistory.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof com.example.calculator.controller.UserHistoryController) {
                com.example.calculator.controller.UserHistoryController uh = (com.example.calculator.controller.UserHistoryController) controller;
                uh.setUsername(currentUsername);
                uh.setOpenedFromAdmin(false);
            }
            Stage stage = (Stage) UserHistoryButton.getScene().getWindow();
            Scene currentScene = stage.getScene();
            if (currentScene != null) {
                currentScene.setRoot(root);
            } else {
                Scene scene = new Scene(root);
                stage.setScene(scene);
            }
            stage.setTitle("User History - Scientific Calculator Lite");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
