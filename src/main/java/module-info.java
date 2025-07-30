module se233.chapter3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens se233.chapter3 to javafx.fxml;
    exports se233.chapter3;
}