package org.rocs.osd.controller.appeal;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.rocs.osd.facade.document.DocumentFacade;
import org.rocs.osd.facade.document.impl.DocumentFacadeImpl;
import org.rocs.osd.model.appeal.Appeal;
import org.rocs.osd.model.document.Document;
import org.rocs.osd.model.enrollment.Enrollment;
import org.rocs.osd.model.person.student.Student;
import org.rocs.osd.model.record.Record;

/**
 * Controller for handling approved appeal card behavior.
 */
public class ApprovedAppealCardController {

    /**
     * The expandable section of the card.
     */
    @FXML
    private VBox expandedSection;

    /**
     * The arrow icon used to indicate expansion state.
     */
    @FXML
    private ImageView arrowIcon;

    /**
     * Tracks whether the card is expanded or collapsed.
     */
    private boolean isExpanded = false;

    /**
     * Toggles the expansion state of the card.
     */
    @FXML
    void toggleExpansion() {
        isExpanded = !isExpanded;

        if (expandedSection != null) {
            expandedSection.setVisible(isExpanded);
            expandedSection.setManaged(isExpanded);
        }

        String imgPath = isExpanded
                ? "/assets/downButton.png"
                : "/assets/rightButton.png";

        try {
            if (arrowIcon != null) {
                arrowIcon.setImage(
                        new Image(getClass().getResourceAsStream(imgPath))
                );
            }
        } catch (Exception e) {
            if (arrowIcon != null) {
                arrowIcon.setRotate(isExpanded ? 90 : 0);
            }
        }
    }

    /**
     * Student ID label.
     */
    @FXML
    private Label studentIdLabel;

    /**
     * Student name label.
     */
    @FXML
    private Label studentNameLabel;

    /**
     * Offense label.
     */
    @FXML
    private Label offenseLabel;

    /**
     * Reason label.
     */
    @FXML
    private Label reasonLabel;

    /**
     * Container for the AI suggestion badge.
     */
    @FXML
    private VBox aiSuggestionBox;

    /**
     * The AI recommendation label (APPROVABLE / DENIABLE / UNCERTAIN).
     */
    @FXML
    private Label aiRecommendationLabel;

    /**
     * The AI reasoning label.
     */
    @FXML
    private Label aiReasoningLabel;

    /**
     * Remarks display area.
     */
    @FXML
    private TextArea commentArea;

    /**
     * Button to view the attached appeal letter, if any.
     */
    @FXML
    private Button viewLetterButton;

    /**
     * Badge shown when the student edited this appeal after filing it.
     */
    @FXML
    private Label editedBadge;

    /**
     * The appeal currently displayed by this card.
     */
    private Appeal appeal;

    /**
     * The document facade, used to fetch attached appeal letters.
     */
    private DocumentFacade documentFacade;

    /**
     * Gets the document facade, creating default if not set.
     * @return the document facade
     */
    private DocumentFacade getDocumentFacade() {
        if (documentFacade == null) {
            documentFacade = new DocumentFacadeImpl();
        }
        return documentFacade;
    }

    /**
     * Sets appeal data into UI components.
     * @param appeal set appeal data.
     */
    public void setAppeal(Appeal appeal) {
        if (appeal == null) {
            return;
        }
        this.appeal = appeal;

        Enrollment e = appeal.getEnrollment();
        Record r = appeal.getRecord();
        Student s = e.getStudent();

        if (studentIdLabel != null) {
            studentIdLabel.setText(s.getStudentId());
        }
        if (studentNameLabel != null) {
            studentNameLabel.setText(
                    s.getFirstName() + " " + s.getLastName()
            );
        }
        if (offenseLabel != null) {
            offenseLabel.setText(r.getRemarks());
        }
        if (reasonLabel != null) {
            reasonLabel.setText(appeal.getMessage());
        }
        if (commentArea != null) {
            commentArea.setText(appeal.getRemarks());
        }
        displayAiSuggestion(
                appeal.getAiRecommendation(),
                appeal.getAiReasoning());
        displayLetterButton(appeal.getDocumentId());
        displayEditedBadge(appeal.isEdited());
    }

    /**
     * Shows or hides the "View Letter" button depending on whether
     * this appeal has an attached letter document on file.
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
     * Shows or hides the "Edited" badge depending on whether the
     * student edited this appeal's message after filing it.
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
     * Opens the appeal's attached letter in the user's default viewer.
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
        } catch (Exception ignored) {
            // Best-effort viewer; nothing else to fall back to here.
        }
    }

    /**
     * Determines a file suffix (with leading dot) to use for the temp
     * file, so the OS opens it with the correct default application.
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
     * no recommendation on file.
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
}
