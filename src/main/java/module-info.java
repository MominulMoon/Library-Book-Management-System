module mehrin.loginpage {
    requires javafx.controls;
    requires javafx.fxml;


    opens mehrin.loginpage to javafx.fxml;
    exports mehrin.loginpage;
}