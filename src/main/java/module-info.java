module com.example.calculator {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.calculator;
    opens com.example.calculator to javafx.fxml, javafx.graphics;
}
