module mehrin.libraryloginpage {
    requires javafx.controls;
    requires javafx.fxml;


    opens mehrin.libraryloginpage to javafx.fxml;
    exports mehrin.libraryloginpage;
}