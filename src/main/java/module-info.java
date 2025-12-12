module com.example.calculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports com.example.calculator;
    opens com.example.calculator to javafx.fxml;
}