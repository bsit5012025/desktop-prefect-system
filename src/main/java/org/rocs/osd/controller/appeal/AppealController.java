package org.rocs.osd.controller.appeal;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;
import org.rocs.osd.facade.appeal.AppealFacade;
import org.rocs.osd.facade.appeal.impl.AppealFacadeImpl;
import org.rocs.osd.model.appeal.Appeal;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for managing and displaying appeal records
 * in the Office of Student Discipline System.
 * It loads appeals from the database and displays them
 * in the UI.
 */
public class AppealController {

    /**
     * Container that holds all appeal cards in the UI.
     */
    @FXML
    private VBox listContainer;

    /**
     * Facade used to retrieve appeal data from backend.
     */
    private AppealFacade appealFacade;

    /**
     * Static controller factory for dependency injection in nested FXML loads.
     * Set by tests to ensure mock facades are injected into card controllers.
     */
    private static Callback<Class<?>, Object> controllerFactory;

    /**
     * Sets the static controller factory for nested FXML loading.
     * @param factory the controller factory callback
     */
    public static void setControllerFactory(
            Callback<Class<?>, Object> factory) {
        controllerFactory = factory;
    }

    /**
     * Clears the static controller factory.
     */
    public static void clearControllerFactory() {
        controllerFactory = null;
    }

    /**
     * Initializes the controller.
     * Clears the list and loads appeals from the database.
     */
    @FXML
    public void initialize() {
        loadAppealsByStatus("PENDING");
        startAutoRefresh();
    }

    /** Appeal IDs currently shown on the Pending tab. */
    private final Set<Long> shownPendingIds = new HashSet<>();

    /** Status of the tab currently displayed. */
    private String currentStatus = "PENDING";

    /** Seconds between background checks for new pending appeals. */
    private static final int REFRESH_SECONDS = 15;

    /**
     * Checks for new pending appeals in the background and adds
     * them to the Pending tab without touching cards already shown.
     */
    private void startAutoRefresh() {
        if (listContainer == null) {
            return;
        }

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(REFRESH_SECONDS),
                event -> refreshPending()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        listContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                timeline.stop();
            }
        });
    }

    /** Fetches pending appeals off the UI thread and appends new ones. */
    private void refreshPending() {
        if (!"PENDING".equals(currentStatus)) {
            return;
        }

        CompletableFuture
                .supplyAsync(() ->
                        getAppealFacade().getAppealsByStatus("PENDING"))
                .thenAccept(appeals -> Platform.runLater(() -> {
                    if (appeals == null || !"PENDING".equals(currentStatus)) {
                        return;
                    }
                    for (Appeal appeal : appeals) {
                        if (shownPendingIds.add(appeal.getAppealID())) {
                            addCard(appeal, "PENDING");
                        }
                    }
                }))
                .exceptionally(error -> null);
    }

    /**
     * Sets the appeal facade for dependency injection.
     * Used for testing to inject mock facades.
     *
     * @param pAppealFacade the facade to use
     */
    public void setAppealFacade(AppealFacade pAppealFacade) {
        this.appealFacade = pAppealFacade;
    }

    /**
     * Gets the appeal facade, creating default implementation if not set.
     *
     * @return the appeal facade
     */
    public AppealFacade getAppealFacade() {
        if (appealFacade == null) {
            appealFacade = new AppealFacadeImpl();
        }
        return appealFacade;
    }

    /**
     * Fetches pending appeals from the database
     * and injects them into the listContainer.
     * @param status load appeal status.
     */
    private void loadAppealsByStatus(String status) {

        if (listContainer == null) {
            return;
        }

        listContainer.getChildren().clear();
        currentStatus = status;
        shownPendingIds.clear();

        List<Appeal> appeals = getAppealFacade().getAppealsByStatus(status);

        if (appeals == null) {
            return;
        }

        for (Appeal appeal : appeals) {
            if ("PENDING".equals(status)) {
                shownPendingIds.add(appeal.getAppealID());
            }
            addCard(appeal, status);
        }
    }

    /**
     * Builds one appeal card and adds it to the list.
     * @param appeal the appeal to show
     * @param status the tab the card belongs to
     */
    private void addCard(Appeal appeal, String status) {
        try {

            FXMLLoader loader;

            if ("APPROVED".equals(status)) {
                loader = new FXMLLoader(getClass().getResource(
                        "/view/appeal/approvedAppealCard.fxml"));
            } else if ("DENIED".equals(status)) {
                loader = new FXMLLoader(getClass().getResource(
                        "/view/appeal/deniedAppealCard.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource(
                        "/view/appeal/appealCard.fxml"));
            }

            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory);
            }

            VBox card = loader.load();

            if ("PENDING".equals(status)) {
                AppealCardController controller =
                        loader.getController();
                controller.setAppeal(appeal);
                controller.setAppealFacade(getAppealFacade());

                controller.setOnActionComplete(() ->
                        loadAppealsByStatus("PENDING"));

            } else if ("APPROVED".equals(status)) {
                ApprovedAppealCardController controller =
                        loader.getController();
                controller.setAppeal(appeal);

            } else {
                DeniedAppealCardController controller =
                        loader.getController();
                controller.setAppeal(appeal);
            }

            listContainer.getChildren().add(card);

        } catch (IOException e) {
            System.err.println("Error loading Appeal Card: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles pending tab click.
     */
    @FXML
    void handlePendingTab() {
        loadAppealsByStatus("PENDING");
    }

    /**
     * Handles approved tab click.
     */
    @FXML
    void handleApprovedTab() {
        loadAppealsByStatus("APPROVED");
    }

    /**
     * Handles denied tab click.
     */
    @FXML
    void handleDeniedTab() {
        loadAppealsByStatus("DENIED");
    }
}
