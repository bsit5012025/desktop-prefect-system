package org.rocs.osd.controller.appeal;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.rocs.osd.controller.dialog.ConfirmationDialogController;
import org.rocs.osd.facade.appeal.AppealFacade;
import org.rocs.osd.facade.appeal.impl.AppealFacadeImpl;
import org.rocs.osd.facade.document.DocumentFacade;
import org.rocs.osd.facade.document.impl.DocumentFacadeImpl;
import org.rocs.osd.model.appeal.Appeal;
import org.rocs.osd.model.document.Document;
import org.rocs.osd.model.enrollment.Enrollment;
import org.rocs.osd.model.person.student.Student;
import org.rocs.osd.model.record.Record;

/**
 * Controller class for managing the UI behavior
 * of an individual Appeal Card.
 */
public class AppealCardController {

    /**
     * Functional interface for showing confirmation dialogs.
     */
    @FunctionalInterface
    public interface ConfirmationProvider {

        /**
         * Shows a confirmation dialog.
         *
         * @param line1       the first line of message
         * @param line2       the second line of message
         * @param confirmText the confirm button text
         * @param cancelText  the cancel button text
         * @param onConfirm   the confirm action
         * @param onCancel    the cancel action
         */
        void show(String line1, String line2, String confirmText,
                  String cancelText, Runnable onConfirm,
                  Runnable onCancel);
    }

    /** Default provider that shows a real popup dialog. */
    private static final ConfirmationProvider DEFAULT_PROVIDER =
            (l1, l2, confirmTxt, cancelTxt, onConfirm, onCancel) -> {
    try {
        FXMLLoader loader = new FXMLLoader(
              java.util.Objects.requireNonNull(
                   AppealCardController.class
                           .getResource("/view/dialogs/confirmation.fxml"),
                      "Cannot find confirmation.fxml"
                )
         );
                    StackPane rootNode = loader.load();
                    ConfirmationDialogController controller =
                            loader.getController();
                    controller.setMessage(l1, l2);
                    controller.setButtonLabels(confirmTxt, cancelTxt);
                    controller.setOnConfirm(() -> {
                        onConfirm.run();
                        try {
                            Stage stage = (Stage) rootNode
                                    .getScene().getWindow();
                            stage.close();
                        } catch (Exception ignored) {
                        }
                    });
                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    Scene scene = new Scene(rootNode);
                    scene.setFill(null);
                    stage.setScene(scene);
                    stage.sizeToScene();
                    stage.show();
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Failed to load confirmation dialog", e);
                }
            };

    /** The confirmation provider instance. */
    private ConfirmationProvider confirmationProvider =
            DEFAULT_PROVIDER;

    /**
     * Allows tests to inject a mock confirmation provider.
     *
     * @param provider the provider to use, or null for default
     */
    public void setConfirmationProvider(
            ConfirmationProvider provider) {
        this.confirmationProvider = provider != null
                ? provider : DEFAULT_PROVIDER;
    }

    /** The expanded section VBox. */
    @FXML
    private VBox expandedSection;
    /** The action bar HBox. */
    @FXML
    private HBox actionBar;
    /** The popup box VBox. */
    @FXML
    private VBox popupBox;
    /** The student ID label. */
    @FXML
    private Label studentIdLabel;
    /** The student name label. */
    @FXML
    private Label studentNameLabel;
    /** The offense label. */
    @FXML
    private Label offenseLabel;
    /** The reason label. */
    @FXML
    private Label reasonLabel;
    /** Container for the AI suggestion badge. */
    @FXML
    private VBox aiSuggestionBox;
    /** The AI recommendation label (APPROVABLE / DENIABLE / UNCERTAIN). */
    @FXML
    private Label aiRecommendationLabel;
    /** The AI reasoning label. */
    @FXML
    private Label aiReasoningLabel;
    /** Button to view the attached appeal letter, if any. */
    @FXML
    private Button viewLetterButton;
    /** Badge shown when the student edited this appeal after filing it. */
    @FXML
    private Label editedBadge;
    /** The arrow icon ImageView. */
    @FXML
    private ImageView arrowIcon;
    /** The comment area TextArea. */
    @FXML
    private TextArea commentArea;
    /** The popup label. */
    @FXML
    private Label popupLabel;
    /** The error label. */
    @FXML
    private Label inlineErrorText;
    /** Container for the inlineErrorText. */
    @FXML
    private HBox errorBannerContainer;
    /** The arrow button. */
    @FXML
    private Button arrowButton;

    /** The appeal data. */
    private Appeal appeal;
    /** The action complete callback. */
    private Runnable onActionComplete;
    /** Whether the card is expanded. */
    private boolean isExpanded = false;
    /** The appeal facade. */
    private AppealFacade appealFacade;
    /** The document facade, used to fetch attached appeal letters. */
    private DocumentFacade documentFacade;
    /** Timer used to automatically dismiss the error banner. */
    private PauseTransition errorHideDelay;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        if (expandedSection != null) {
            expandedSection.setVisible(false);
            expandedSection.setManaged(false);
        }
        if (actionBar != null) {
            actionBar.setVisible(false);
            actionBar.setManaged(false);
        }
        if (popupBox != null) {
            popupBox.setVisible(false);
            popupBox.setManaged(false);
        }
        if (errorBannerContainer != null) {
            errorBannerContainer.setVisible(false);
            errorBannerContainer.setManaged(false);
        }
        if (inlineErrorText != null) {
            inlineErrorText.setText("");
        }
        if (aiSuggestionBox != null) {
            aiSuggestionBox.setVisible(false);
            aiSuggestionBox.setManaged(false);
        }
        if (viewLetterButton != null) {
            viewLetterButton.setVisible(false);
            viewLetterButton.setManaged(false);
        }
        if (editedBadge != null) {
            editedBadge.setVisible(false);
            editedBadge.setManaged(false);
        }
        if (arrowButton != null) {
            arrowButton.setMinSize(30, 30);
            arrowButton.setPrefSize(30, 30);
        }
        if (commentArea != null) {
            commentArea.textProperty().addListener((
                    observable,
                    oldValue,
                    newValue) -> {

                if (newValue != null && newValue.length() > 500) {
                    commentArea.setText(oldValue);
                }
            });
        }
    }

    /**
     * Sets the appeal facade.
     *
     * @param pAppealFacade the facade to use
     */
    public void setAppealFacade(AppealFacade pAppealFacade) {
        this.appealFacade = pAppealFacade;
    }

    /**
     * Gets the appeal facade, creating default if not set.
     *
     * @return the appeal facade
     */
    private AppealFacade getAppealFacade() {
        if (appealFacade == null) {
            appealFacade = new AppealFacadeImpl();
        }
        return appealFacade;
    }

    /**
     * Gets the document facade, creating default if not set.
     *
     * @return the document facade
     */
    private DocumentFacade getDocumentFacade() {
        if (documentFacade == null) {
            documentFacade = new DocumentFacadeImpl();
        }
        return documentFacade;
    }

    /**
     * Sets the appeal data.
     *
     * @param pAppeal the appeal to display
     */
    public void setAppeal(Appeal pAppeal) {
        this.appeal = pAppeal;
        loadAppealData();
    }

    /**
     * Sets the action complete callback.
     *
     * @param pAction the callback runnable
     */
    public void setOnActionComplete(Runnable pAction) {
        this.onActionComplete = pAction;
    }

    /**
     * Handles the approve appeal action.
     */
    @FXML
    public void handleAppealApprove() {
        if (commentArea != null
                && commentArea.getText().length() > 500) {
            showError("Remarks cannot exceed 500 characters.");
            return;
        }
        showConfirmation(
                "Are you sure you want to",
                "approve this appeal?",
                "Approve",
                "Cancel",
                () -> {
                    String remarks = (commentArea != null
                            && !commentArea.getText()
                            .trim().isEmpty())
                            ? commentArea.getText() : null;
                    getAppealFacade().approveAppeal(
                            appeal.getAppealID(), remarks);
                    showPopupAndRemoveCard("Appeal approved!");
                },
                () -> {
                    /* cancel - do nothing */
                }
        );
    }

    /**
     * Handles the deny appeal action.
     */
    @FXML
    public void handleAppealDeny() {
        if (commentArea == null
                || commentArea.getText().trim().isEmpty()) {
            showError("Please enter remarks before denying.");
            return;
        }
        if (commentArea != null
                && commentArea.getText().length() > 500) {
            showError("Remarks cannot exceed 500 characters.");
            return;
        }
        showConfirmation(
                "Are you sure you want to",
                "deny this appeal?",
                "Deny",
                "Cancel",
                () -> {
                    getAppealFacade().denyAppeal(
                            appeal.getAppealID(),
                            commentArea.getText());
                    showPopupAndRemoveCard("Appeal denied!");
                },
                () -> {
                    /* cancel - do nothing */
                }
        );

    }

    /**
     * Shows a confirmation dialog.
     *
     * @param l1         line 1
     * @param l2         line 2
     * @param confirmTxt confirm text
     * @param cancelTxt  cancel text
     * @param onConfirm  confirm action
     * @param onCancel   cancel action
     */
    private void showConfirmation(String l1, String l2,
                                  String confirmTxt, String cancelTxt,
                                  Runnable onConfirm, Runnable onCancel) {
        confirmationProvider.show(l1, l2, confirmTxt, cancelTxt,
                onConfirm, onCancel);
    }

    /**
     * Shows popup and removes card.
     *
     * @param msg the message to show
     */
    private void showPopupAndRemoveCard(String msg) {
        if (popupLabel != null) {
            popupLabel.setText(msg);
        }
        if (popupBox != null) {
            popupBox.setVisible(true);
            popupBox.setManaged(true);
            popupBox.applyCss();
            popupBox.layout();
        }
        if (onActionComplete != null) {
            onActionComplete.run();
        }
    }

    /**
     * Shows an error message.
     *
     * @param msg the error message
     */
    private void showError(String msg) {
        if (errorBannerContainer == null
                || inlineErrorText == null) {
            return;
        }

        if (errorHideDelay != null) {
            errorHideDelay.stop();
        }

        inlineErrorText.setText(msg);

        errorBannerContainer.setManaged(true);
        errorBannerContainer.setVisible(true);

        errorBannerContainer.requestLayout();

        errorHideDelay = new PauseTransition(Duration.seconds(3));

        errorHideDelay.setOnFinished(e -> {
            errorBannerContainer.setVisible(false);
            errorBannerContainer.setManaged(false);
        });

        errorHideDelay.play();
    }


    /**
     * Loads appeal data into labels.
     */
    private void loadAppealData() {
        if (appeal != null) {
            Enrollment e = appeal.getEnrollment();
            Record r = appeal.getRecord();
            Student s = e.getStudent();
            if (studentIdLabel != null) {
                studentIdLabel.setText(s.getStudentId());
            }
            if (studentNameLabel != null) {
                studentNameLabel.setText(
                        s.getFirstName() + " " + s.getLastName());
            }
            if (offenseLabel != null) {
                offenseLabel.setText(r.getRemarks());
            }
            if (reasonLabel != null) {
                reasonLabel.setText(appeal.getMessage());
            }
            displayAiSuggestion(
                    appeal.getAiRecommendation(),
                    appeal.getAiReasoning());
            displayLetterButton(appeal.getDocumentId());
            displayEditedBadge(appeal.isEdited());
        }
    }

    /**
     * Shows or hides the "Edited" badge depending on whether the
     * student edited this appeal's message after filing it.
     *
     * @param edited whether the appeal was edited
     */
    private void displayEditedBadge(boolean edited) {
        if (editedBadge == null) {
            return;
        }
        editedBadge.setVisible(edited);
        editedBadge.setManaged(edited);
    }

    /**
     * Shows or hides the "View Letter" button depending on whether
     * this appeal has an attached letter document on file.
     *
     * @param documentId the ID of the attached document, or null
     */
    private void displayLetterButton(Long documentId) {
        if (viewLetterButton == null) {
            return;
        }
        boolean hasLetter = documentId != null;
        viewLetterButton.setVisible(hasLetter);
        viewLetterButton.setManaged(hasLetter);
    }

    /**
     * Opens the appeal's attached letter in the user's default
     * viewer (PDF reader, image viewer, Word, etc.), by writing the
     * stored file bytes to a temp file and asking the OS to open it.
     */
    @FXML
    public void onViewLetter() {
        if (appeal == null || appeal.getDocumentId() == null) {
            return;
        }

        try {
            Document document = getDocumentFacade()
                    .getDocumentById(appeal.getDocumentId());

            if (document == null || document.getFileData() == null) {
                showError("The attached letter could not be found.");
                return;
            }

            File tempFile = File.createTempFile(
                    "appeal-letter-" + document.getDocumentId() + "-",
                    fileSuffix(document.getFileName(), document.getContentType()));
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                out.write(document.getFileData());
            }

            Desktop.getDesktop().open(tempFile);

        } catch (Exception e) {
            showError("Could not open the attached letter: " + e.getMessage());
        }
    }

    /**
     * Determines a file suffix (with leading dot) to use for the temp
     * file, so the OS opens it with the correct default application.
     *
     * @param fileName    the original filename, or null
     * @param contentType the MIME content type, or null
     * @return a file suffix such as ".pdf", never null
     */
    private String fileSuffix(String fileName, String contentType) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.'));
        }
        if (contentType != null) {
            return switch (contentType) {
                case "application/pdf" -> ".pdf";
                case "image/jpeg" -> ".jpg";
                case "image/png" -> ".png";
                case "application/msword" -> ".doc";
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx";
                default -> ".bin";
            };
        }
        return ".bin";
    }

    /**
     * Populates the AI suggestion badge, if the AI produced a
     * recommendation for this appeal. Hidden entirely when there's
     * no recommendation on file (e.g. older appeals filed before
     * this feature existed, or the AI call failed at submit time).
     *
     * @param recommendation APPROVABLE / DENIABLE / UNCERTAIN, or null
     * @param reasoning      short AI-generated reasoning, or null
     */
    private void displayAiSuggestion(String recommendation, String reasoning) {
        if (aiSuggestionBox == null) {
            return;
        }
        if (recommendation == null || recommendation.isBlank()) {
            aiSuggestionBox.setVisible(false);
            aiSuggestionBox.setManaged(false);
            return;
        }

        String normalized = recommendation.trim().toUpperCase();
        aiSuggestionBox.getStyleClass().removeAll(
                "aiApprovable", "aiDeniable", "aiUncertain");

        String badgeText;
        switch (normalized) {
            case "APPROVABLE":
                badgeText = "AI Suggestion: Approvable";
                aiSuggestionBox.getStyleClass().add("aiApprovable");
                break;
            case "DENIABLE":
                badgeText = "AI Suggestion: Deniable";
                aiSuggestionBox.getStyleClass().add("aiDeniable");
                break;
            default:
                badgeText = "AI Suggestion: Uncertain";
                aiSuggestionBox.getStyleClass().add("aiUncertain");
                break;
        }

        if (aiRecommendationLabel != null) {
            aiRecommendationLabel.setText(badgeText);
        }
        if (aiReasoningLabel != null) {
            aiReasoningLabel.setText(reasoning != null ? reasoning : "");
        }

        aiSuggestionBox.setVisible(true);
        aiSuggestionBox.setManaged(true);
    }

    /**
     * Toggles the card expansion.
     */
    @FXML
    public void toggleExpansion() {
        isExpanded = !isExpanded;
        if (expandedSection != null) {
            expandedSection.setVisible(isExpanded);
            expandedSection.setManaged(isExpanded);
        }
        if (actionBar != null) {
            boolean shouldShow = isExpanded && appeal != null
                    && "PENDING".equals(appeal.getStatus());
            actionBar.setVisible(shouldShow);
            actionBar.setManaged(shouldShow);
        }
        updateIcon();
        if (expandedSection != null) {
            expandedSection.applyCss();
            expandedSection.layout();
        }
        if (actionBar != null) {
            actionBar.applyCss();
            actionBar.layout();
        }
        javafx.scene.Parent parent = expandedSection != null
                ? expandedSection.getParent() : null;
        while (parent != null) {
            parent.requestLayout();
            parent.applyCss();
            parent.layout();
            if (parent.getScene() != null) {
                parent.getScene().getRoot().applyCss();
                parent.getScene().getRoot().layout();
                break;
            }
            parent = parent.getParent();
        }
    }

    /**
     * Updates the arrow icon.
     */
    private void updateIcon() {
        if (arrowIcon != null) {
            String path = isExpanded
                    ? "/assets/downButton.png"
                    : "/assets/rightButton.png";
            try {
                Image newImage = new Image(
                        getClass().getResourceAsStream(path));
                if (newImage.isError()) {
                    throw new RuntimeException(
                            "Image load error");
                }
                arrowIcon.setImage(newImage);
            } catch (Exception e) {
                arrowIcon.setRotate(isExpanded ? 90 : 0);
            }
        }
    }
}
