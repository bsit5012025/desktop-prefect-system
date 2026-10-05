package org.rocs.osd.controller.request;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controller for handling approved request card behavior.
 */
public class ApprovedRequestCardController {

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
     * Label displaying the name of the requester.
     */
    @FXML
    private Label deptLabel;
    /**
     * Label displaying the name of the requester.
     */
    @FXML
    private Label studentNameLabel;
    /**
     * Label displaying the type of the request.
     */
    @FXML
    private Label typeLabel;
    /**
     * Label displaying the reason for the request.
     */
    @FXML
    private Label studentsLabel;

    /** Label displaying how the requester wants the result. */
    @FXML
    private Label deliveryLabel;

    @FXML
    private Label reasonLabel;
    /**
     * Label for displaying the reason
     * why it is approve or denied.
     */
    @FXML
    private TextArea commentArea;

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
     * Tracks whether the card is expanded or collapsed.
     */
    private boolean isExpanded = false;

    /** ID of the current request. */
    private long cardId;

    /**
     * Getter for cardId.
     * @return cardId.
     * */
    public long getCardId() {
        return cardId;
    }

    /**
     * Sets the data for the request card.
     * @param pDept the department name.
     * @param pName the requester name.
     * @param pType the request type.
     * @param pReason the reason for the request.
     * @param remarks the remarks of the user approving the request.
     * @param requestId the number for the specific cards
     */
    public void setData(String pDept, String pName,
                        String pType, String pReason,
                        String remarks, long requestId) {
        setData(pDept, pName, pType, pReason, remarks, requestId, null, null);
    }

    /**
     * Sets the data for the request card, including the AI recommendation.
     * @param pDept the department name.
     * @param pName the requester name.
     * @param pType the request type.
     * @param pReason the reason for the request.
     * @param remarks the remarks of the user approving the request.
     * @param requestId the number for the specific cards
     * @param pAiRecommendation AI recommendation (APPROVABLE / DENIABLE / UNCERTAIN), or null
     * @param pAiReasoning short AI-generated reasoning, or null
     */
    public void setData(String pDept, String pName,
                        String pType, String pReason,
                        String remarks, long requestId,
                        String pAiRecommendation, String pAiReasoning) {
        if (deptLabel != null) {
            deptLabel.setText(pDept);
        }
        if (studentNameLabel != null) {
            studentNameLabel.setText(pName);
        }
        if (typeLabel != null) {
            typeLabel.setText(pType);
        }
        if (reasonLabel != null) {
            reasonLabel.setText(pReason);
        }
        if (commentArea != null) {
            commentArea.setText(remarks);
        }
        cardId = requestId;
        displayAiSuggestion(pAiRecommendation, pAiReasoning);
    }

    /**
     * Populates the AI suggestion badge, if the AI produced a
     * recommendation for this request. Hidden entirely when there's
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

    /**
     * Toggles the expansion state of the card.
     */
    @FXML
    void toggleExpansion() {
        isExpanded = !isExpanded;
        expandedSection.setVisible(isExpanded);
        expandedSection.setManaged(isExpanded);

        String imgPath = isExpanded
                ? "/assets/downButton.png"
                : "/assets/rightButton.png";

        try {
            arrowIcon.setImage(new Image(
                    getClass().getResourceAsStream(imgPath)
            ));
        } catch (Exception e) {
            arrowIcon.setRotate(isExpanded ? 90 : 0);
        }
    }

    /**
     * Shows which students the request is about.
     *
     * @param details comma-separated student IDs, or null
     */
    public void setStudents(String details) {
        if (studentsLabel != null) {
            studentsLabel.setText(details == null ? "" : details);
        }
    }

    /**
     * Shows whether the requester wants a physical copy or an email.
     *
     * @param deliveryMethod HARDCOPY or EMAIL, or null
     */
    public void setDeliveryMethod(String deliveryMethod) {
        if (deliveryLabel == null) {
            return;
        }
        if ("EMAIL".equalsIgnoreCase(deliveryMethod)) {
            deliveryLabel.setText("Email (Gmail)");
        } else {
            deliveryLabel.setText("Hardcopy (physical)");
        }
    }
}
