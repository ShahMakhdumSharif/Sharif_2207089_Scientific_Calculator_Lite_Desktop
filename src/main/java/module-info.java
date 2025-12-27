module com.example.calculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports com.example.calculator;
    opens com.example.calculator to javafx.fxml;
    opens com.example.calculator.controller to javafx.fxml;
    opens com.example.calculator.database to javafx.fxml;
    opens com.example.calculator.model to javafx.base, javafx.fxml;
}