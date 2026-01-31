module library.lms {
    requires javafx.controls;
    requires javafx.fxml;

    opens library.lms to javafx.fxml;

    exports library.lms;
}