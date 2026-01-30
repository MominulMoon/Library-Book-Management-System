package mehrin.loginpage;


import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView imageView;

    @FXML
    public void initialize() {
        // Load image
        Image image = new Image(getClass().getResourceAsStream("RUET.png"));
        imageView.setImage(image);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill all fields");
        } else {
            System.out.println("Login clicked!");
        }
    }
}

