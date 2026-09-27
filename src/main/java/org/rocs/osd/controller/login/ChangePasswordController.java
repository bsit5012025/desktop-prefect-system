package org.rocs.osd.controller.login;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.rocs.osd.controller.dialog.ErrorDialogController;
import org.rocs.osd.data.dao.login.LoginDao;
import org.rocs.osd.data.dao.login.impl.LoginDaoImpl;
import org.rocs.osd.facade.login.LoginFacade;
import org.rocs.osd.facade.login.impl.LoginFacadeImpl;

import java.io.IOException;

public class ChangePasswordController {

    /**
     * Stage used to display login error popups.
     */
    private Stage errorStage;

    /**
     * Password field used to enter the user's new password.
     */
    @FXML
    private PasswordField oldPasswordField;

    /**
     * Text field used to display the old password
     * when password visibility is enabled.
     */
    @FXML
    private TextField oldpasswordTextField;

    /**
     * Password field used to securely enter the new password.
     */
    @FXML
    private PasswordField newPasswordField;

    /**
     * Text field used to display the password when visibility is enabled.
     */
    @FXML
    private TextField newPasswordTextField;

    /**
     * Password field used to enter the confirmation password.
     */
    @FXML
    private PasswordField confirmPasswordField;

    /**
     * Text field used to display the confirmation password when
     * visibility is enabled.
     */
    @FXML
    private TextField confirmPasswordTextField;

    /**
     * Button used to toggle password visibility.
     */
    @FXML
    private Button toggleOldPasswordButton;

    /**
     * Button used to toggle the visibility of the new password field.
     */
    @FXML
    private Button toggleNewPasswordButton;

    /**
     * Button used to toggle confirmation password visibility.
     */
    @FXML
    private Button toggleConfirmPasswordButton;

    /**
     * Facade used to handle login-related operations.
     */
    private LoginFacade loginFacade;

    /**
     * Initializes the controller and its required dependencies.
     * note: suppress warning because of false positive pmd error.
     */
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void initialize() {
        LoginDao loginDao = new LoginDaoImpl();
        loginFacade = new LoginFacadeImpl(loginDao);
    }

    /**
     * Toggles the visibility of the old password
     * between a masked password field and a plain text field.
     * note: suppress warning because of false positive pmd error.
     */
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void toggleOldPasswordVisibility() {

        if (oldPasswordField == null || oldpasswordTextField == null
                || toggleOldPasswordButton == null) {
            return;
        }

        if (oldPasswordField.isVisible()) {

            oldpasswordTextField.setText(oldPasswordField.getText());

            oldPasswordField.setManaged(false);
            oldPasswordField.setVisible(false);

            oldpasswordTextField.setManaged(true);
            oldpasswordTextField.setVisible(true);
            toggleOldPasswordButton.getStyleClass().add("show-icon");

        } else {

            oldPasswordField.setText(oldpasswordTextField.getText());

            oldpasswordTextField.setManaged(false);
            oldpasswordTextField.setVisible(false);

            oldPasswordField.setManaged(true);
            oldPasswordField.setVisible(true);
            toggleOldPasswordButton.getStyleClass().remove("show-icon");
        }
    }

    /**
     * Toggles the visibility of the new password
     * between a masked password field and a plain text field.
     * note: suppress warning because of false positive pmd error.
     */
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void togglePasswordVisibility() {

        if (newPasswordField == null || newPasswordTextField == null
                || toggleNewPasswordButton == null) {
            return;
        }

        if (newPasswordField.isVisible()) {

            newPasswordTextField.setText(newPasswordField.getText());

            newPasswordField.setManaged(false);
            newPasswordField.setVisible(false);

            newPasswordTextField.setManaged(true);
            newPasswordTextField.setVisible(true);
            toggleNewPasswordButton.getStyleClass().add("show-icon");

        } else {

            newPasswordField.setText(newPasswordTextField.getText());

            newPasswordTextField.setManaged(false);
            newPasswordTextField.setVisible(false);

            newPasswordField.setManaged(true);
            newPasswordField.setVisible(true);
            toggleNewPasswordButton.getStyleClass().remove("show-icon");
        }
    }

    /**
     * Toggles the visibility of the confirmation password field.
     * note: suppress warning because of false positive pmd error.
     */
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void toggleConfirmPasswordVisibility() {

        if (confirmPasswordField == null || confirmPasswordTextField == null
                || toggleConfirmPasswordButton == null) {
            return;
        }

        if (confirmPasswordField.isVisible()) {

            confirmPasswordTextField.setText(
                    confirmPasswordField.getText()
            );

            confirmPasswordField.setManaged(false);
            confirmPasswordField.setVisible(false);

            confirmPasswordTextField.setManaged(true);
            confirmPasswordTextField.setVisible(true);
            toggleConfirmPasswordButton.getStyleClass().add("show-icon");

        } else {

            confirmPasswordField.setText(
                    confirmPasswordTextField.getText()
            );

            confirmPasswordTextField.setManaged(false);
            confirmPasswordTextField.setVisible(false);

            confirmPasswordField.setManaged(true);
            confirmPasswordField.setVisible(true);
            toggleConfirmPasswordButton.getStyleClass().remove("show-icon");
        }
    }

    /**
     * Cancels the password change process and returns to the login page.
     * note: suppress warning because of false positive pmd error.
     *
     * @param event action event triggered by the cancel button
     */
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void cancelChangePassword(ActionEvent event) {
        toLogin(event);
    }

    /**
     * Validates the password fields and attempts to change the user's password.
     *
     * @param event the action event triggered by the Change Password button
     */
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void changePassword(ActionEvent event) {
        String oldPassword;
        String newPassword;
        String confirmedPassword;

        if (oldPasswordField.isVisible()) {
            oldPassword = oldPasswordField.getText();
        } else {
            oldPassword = oldpasswordTextField.getText();
        }

        if (newPasswordField.isVisible()) {
            newPassword = newPasswordField.getText();
        } else {
            newPassword = newPasswordTextField.getText();
        }

        if (confirmPasswordField.isVisible()) {
            confirmedPassword = confirmPasswordField.getText();
        } else {
            confirmedPassword = confirmPasswordTextField.getText();
        }

        if (newPassword.isBlank() || confirmedPassword.isBlank()) {
            showErrorPopup("New Password and confirmation should have value");
            return;
        }

        if (!newPassword.equals(confirmedPassword)) {
            showErrorPopup("Password and confirmation password do not match.");
            return;
        }

        boolean changePasswordStatus = loginFacade.changePassword(
                oldPassword,
                confirmedPassword
        );

        if (!changePasswordStatus) {
            showErrorPopup("Old Password is not the same");
            return;
        }

        toLogin(event);
    }

    /**
     * Navigates the current window back to the login page.
     *
     * @param event action event used to access the current stage
     */
    private void toLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/login/login.fxml")
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
     * Displays a temporary error popup at the bottom of the screen.
     *
     * @param message the message to display in the error dialog
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

            Stage mainStage =
                    (Stage) confirmPasswordField.getScene().getWindow();

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
