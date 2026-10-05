package org.rocs.osd.controller.login;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.rocs.osd.controller.dialog.ErrorDialogController;
import org.rocs.osd.data.dao.login.LoginDao;
import org.rocs.osd.data.dao.login.impl.LoginDaoImpl;
import org.rocs.osd.facade.login.LoginFacade;
import org.rocs.osd.facade.login.impl.LoginFacadeImpl;
import org.rocs.osd.model.login.Login;
import org.rocs.osd.session.Session;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller responsible for handling user interactions in
 * the Login screen of the Office of Student Discipline.
 *
 * It validates user credentials and manages
 * navigation to the Dashboard view.
 */
public class LoginController {

    /**
     * Stage used to display login error popups.
     */
    private Stage errorStage;

    /**
     * Text field for entering the username.
     */
    @FXML
    private TextField usernameTextField;

    /**
     * Password field for entering the password.
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Text field to display password in plain text when toggled.
     */
    @FXML
    private TextField passwordTextField;

    /**
     * Button used to toggle password visibility.
     */
    @FXML
    private javafx.scene.control.Button togglePasswordButton;
    /**
     * Facade used to retrieve login data from backend.
     */
    private LoginFacade loginFacade;

    /**
     * Toggles the visibility of the password input.
     * Shows the password in plain text if currently hidden,
     * and hides it if currently visible.
     */
    @FXML
    public void togglePasswordVisibility() {
        if (passwordField == null || passwordTextField == null
                || togglePasswordButton == null) {
            return;
        }
        if (passwordField.isVisible()) {
            passwordTextField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            togglePasswordButton.getStyleClass().add("show-icon");
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            togglePasswordButton.getStyleClass().remove("show-icon");
        }
    }

    /**
     * Handles the mock process of loginControllerTest.
     * @param pLoginFacade set loginFacade on loginControllerTest to mock db.
     */
    public void setLoginFacade(LoginFacade pLoginFacade) {
        this.loginFacade = pLoginFacade;
    }

    /**
     * Handles the login process when the Login button is clicked.
     * This method validates the entered username and
     * password and loads the Dashboard if successful.
     * @param event the action event triggered by clicking the login button.
     */
    public void onLogin(ActionEvent event) {
        if (loginFacade == null) {
            LoginDao loginDao = new LoginDaoImpl();
            loginFacade = new LoginFacadeImpl(loginDao);
        }

        String user = usernameTextField.getText();
        // Read from whichever password field is actually visible --
        // togglePasswordVisibility() only syncs the two fields at the
        // moment the eye icon is clicked, not while typing, so if the
        // user typed after toggling to "show password", passwordField
        // itself would still hold stale (often blank) text.
        String pass = (passwordField != null && passwordField.isVisible())
                ? passwordField.getText()
                : passwordTextField.getText();

        if (user == null || user.isBlank() || pass == null || pass.isBlank()) {
            showErrorPopup("Enter both username and password!");
            return;
        }

        try {
            boolean loginCheck = loginFacade.login(user, pass);

            if (loginCheck) {
                Login login = loginFacade.getByUsername(user);
                Session.setEmployee(login.getEmployee());
                loadDashboard(event);
            } else {
                showErrorPopup("Invalid username or password!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorPopup("Login failed: " + e.getMessage());
        }
    }

    /**
     * Opens the change password view and replaces the current scene
     * while preserving the current window dimensions.
     * note: suppress warning because of false positive pmd error.
     *
     * @param event action event triggered by the change password action
     */
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void onLoadChangePasswordModal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/login/changePassword.fxml")
            );

            Parent root = loader.load();

            Stage currentStage = (Stage)
                    ((Node) event.getSource()).getScene().getWindow();

            double currentWidth = currentStage.getWidth();
            double currentHeight = currentStage.getHeight();

            Scene scene = new Scene(root, currentWidth, currentHeight);

            currentStage.setScene(scene);

            currentStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the Dashboard screen from the FXML file.
     * Get User available screen Height and width, then apply
     * those sizes to the UI.
     * @param event the action event used to identify the current stage.
     */
    void loadDashboard(ActionEvent event) {
        try {
            if (errorStage != null) {
                errorStage.close();
                errorStage = null;
            }

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass()
                    .getResource("/view/dashboard/dashboard.fxml")));

            Stage stage = (Stage) (
                    (Node) event.getSource()).getScene().getWindow();

            Rectangle2D availableScreen = Screen.getPrimary().getVisualBounds();
            stage.setWidth(availableScreen.getWidth());
            stage.setHeight(availableScreen.getHeight());

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();

        } catch (LoadException e) {
            System.err.println("Error loading Dashboard");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("A UI component has not been initialized");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Displays a temporary error popup at the bottom of the screen.
     * @param message The message to display in the error dialog.
     */
    private void showErrorPopup(String message) {
        try {
            if (errorStage != null && errorStage.isShowing()) {
                errorStage.close();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/view/dialogs/error.fxml"));
            Parent root = loader.load();

            ErrorDialogController controller = loader.getController();
            controller.setMessage(message);

            errorStage = new Stage();
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            errorStage.setScene(scene);

            errorStage.initStyle(StageStyle.TRANSPARENT);
            errorStage.initModality(Modality.NONE);

            Stage mainStage = (Stage) usernameTextField.getScene().getWindow();

            double popupWidth = 400;
            double popupHeight = 70;
            double centerX = mainStage.getX()
                    + (mainStage.getWidth() - popupWidth) / 2;
            double bottomY = mainStage.getY()
                    + mainStage.getHeight() - popupHeight;

            errorStage.setX(centerX);
            errorStage.setY(bottomY);

            errorStage.show();

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(finishEvent -> errorStage.close());
            delay.play();

        } catch (IOException e) {
            System.err.println("Could not load Error Popup: " + e.getMessage());
        }
    }
}
