package org.rocs.osd.controller.request;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.rocs.osd.data.dao.employee.EmployeeDao;
import org.rocs.osd.data.dao.employee.impl.EmployeeDaoImpl;
import org.rocs.osd.data.dao.request.RequestDao;
import org.rocs.osd.data.dao.request.impl.RequestDaoImpl;
import org.rocs.osd.facade.employee.EmployeeFacade;
import org.rocs.osd.facade.employee.impl.EmployeeFacadeImpl;
import org.rocs.osd.facade.request.RequestFacade;
import org.rocs.osd.facade.request.impl.RequestFacadeImpl;
import org.rocs.osd.model.person.employee.Employee;
import org.rocs.osd.model.request.Request;
import org.rocs.osd.model.request.RequestStatus;

import java.util.List;

/**
 * Main controller for the Request module.
 * Handles the logic for displaying the list of request cards.
 */
public class RequestController {

    /** Container where Request Cards will be added. */
    @FXML
    private VBox listContainer;

    /** Facade for handling request operations. */
    private RequestFacade requestFacade;

    /** Facade for handling employee operations. */
    private EmployeeFacade employeeFacade;

    /** Initializes the request view and populates the list. */
    @FXML
    public void initialize() {
        RequestDao requestDao = new RequestDaoImpl();
        requestFacade = new RequestFacadeImpl(requestDao);

        EmployeeDao employeeDao = new EmployeeDaoImpl();
        employeeFacade = new EmployeeFacadeImpl(employeeDao);

        loadPendingRequestData();
    }


    /** Loads and filters Pending request data to create UI cards. */
    private void loadPendingRequestData() {
        if (listContainer == null) {
            return;
        }

        listContainer.getChildren().clear();
        List<Request> requestList = requestFacade.getAllRequestByStatus(
                RequestStatus.PENDING);

        if (requestList != null) {
            for (Request request : requestList) {
                Employee employee = employeeFacade
                        .getEmployeeByEmployeeID(request.getEmployeeID());
                String dept = String.valueOf(employee.getDepartment());
                String name = employee.getFirstName() + " "
                        + employee.getMiddleName() + ". "
                        + employee.getLastName();
                String type = request.getType();
                String reason = request.getMessage();
                long requestId = request.getRequestID();
                String aiRecommendation = request.getAiRecommendation();
                String aiReasoning = request.getAiReasoning();
                String deliveryMethod = request.getDeliveryMethod();
                String details = request.getDetails();

                addPendingRequestCard(dept, name, type,
                        reason, requestId, aiRecommendation, aiReasoning,
                        deliveryMethod, details);
            }
        }
    }

    /**
     * Loads the RequestCard FXML and adds it to the list.
     * @param dept    the department name
     * @param name      the name of the requester
     * @param type      the type of request
     * @param reason    the reason for the request
     * @param requestId the unique identifier for the request
     * @param aiRecommendation the AI recommendation, or null
     * @param aiReasoning      the short AI-generated reasoning, or null
     * @param deliveryMethod   HARDCOPY or EMAIL, or null
     * @param details          the student IDs the request is about
     */
    private void addPendingRequestCard(String dept, String name,
                                       String type, String reason,
                                       long requestId,
                                       String aiRecommendation,
                                       String aiReasoning,
                                       String deliveryMethod,
                                       String details) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/view/request/RequestCard.fxml"));
            VBox card = loader.load();

            RequestCardController controller = loader.getController();
            if (controller != null) {
                controller.setData(dept, name, type, reason, requestId,
                        aiRecommendation, aiReasoning);
                controller.setDeliveryMethod(deliveryMethod);
                controller.setStudents(details);
                listContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            System.err.println("Error creating request card.");
            e.printStackTrace();
        }
    }

    /**
     * Handles the "Pending" tab action.
     * Loads pending request data and updates the UI.
     * with corresponding request cards.
     * */
    @FXML
    public void handlePendingTab() {
        loadPendingRequestData();
    }

    /** Loads and filters Approve request data to create UI cards. */
    private void loadApproveRequestData() {
        if (listContainer == null) {
            return;
        }

        listContainer.getChildren().clear();
        List<Request> requestList =
                requestFacade.getAllRequestByStatus(RequestStatus.APPROVED);

        if (requestList != null) {
            for (Request request : requestList) {
                Employee employee = employeeFacade
                        .getEmployeeByEmployeeID(request.getEmployeeID());
                String dept = String.valueOf(employee.getDepartment());
                String name = employee.getFirstName() + " "
                        + employee.getMiddleName() + ". "
                        + employee.getLastName();
                String type = request.getType();
                String reason = request.getMessage();
                String remarks = request.getRemarks();
                long requestId = request.getRequestID();
                String aiRecommendation = request.getAiRecommendation();
                String aiReasoning = request.getAiReasoning();
                String deliveryMethod = request.getDeliveryMethod();
                String details = request.getDetails();

                addApproveRequestCard(dept, name, type,
                        reason, remarks, requestId,
                        aiRecommendation, aiReasoning,
                        deliveryMethod, details);
            }
        }
    }

    /**
     * Loads the RequestCard FXML and adds it to the list.
     * @param dept    the department name
     * @param name      the name of the requester
     * @param type      the type of request
     * @param reason    the reason for the request
     * @param remarks    the remarks for the request
     * @param requestId the unique identifier for the request
     * @param aiRecommendation the AI recommendation, or null
     * @param aiReasoning      the short AI-generated reasoning, or null
     */
    private void addApproveRequestCard(String dept, String name,
                                       String type, String reason,
                                       String remarks, long requestId,
                                       String aiRecommendation,
                                       String aiReasoning,
                                       String deliveryMethod,
                                       String details) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/view/request/ApprovedRequestCard.fxml"));
            VBox card = loader.load();

            ApprovedRequestCardController controller =
                    loader.getController();
            if (controller != null) {
                controller.setData(dept, name, type, reason,
                        remarks, requestId, aiRecommendation, aiReasoning);
                controller.setDeliveryMethod(deliveryMethod);
                controller.setStudents(details);
                listContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            System.err.println("Error creating request card.");
            e.printStackTrace();
        }
    }

    /**
     * Handles the "Approve" tab action.
     * Loads approve request data and updates the UI.
     * with corresponding request cards.
     * */
    @FXML
    public void handleApproveTab() {
        loadApproveRequestData();
    }

    /** Loads and filters Denied request data to create UI cards. */
    private void loadDeniedRequestData() {
        if (listContainer == null) {
            return;
        }

        listContainer.getChildren().clear();
        List<Request> requestList =
                requestFacade.getAllRequestByStatus(RequestStatus.DENIED);

        if (requestList != null) {
            for (Request request : requestList) {
                Employee employee = employeeFacade
                        .getEmployeeByEmployeeID(request.getEmployeeID());
                String dept = String.valueOf(employee.getDepartment());
                String name = employee.getFirstName() + " "
                        + employee.getMiddleName() + ". "
                        + employee.getLastName();
                String type = request.getType();
                String reason = request.getMessage();
                String remarks = request.getRemarks();
                long requestId = request.getRequestID();
                String aiRecommendation = request.getAiRecommendation();
                String aiReasoning = request.getAiReasoning();
                String deliveryMethod = request.getDeliveryMethod();
                String details = request.getDetails();

                addDeniedRequestCard(dept, name, type,
                        reason, remarks, requestId,
                        aiRecommendation, aiReasoning,
                        deliveryMethod, details);
            }
        }
    }

    /**
     * Loads the RequestCard FXML and adds it to the list.
     * @param dept    the department name
     * @param name      the name of the requester
     * @param type      the type of request
     * @param reason    the reason for the request
     * @param remarks    the remarks for the request
     * @param requestId the unique identifier for the request
     * @param aiRecommendation the AI recommendation, or null
     * @param aiReasoning      the short AI-generated reasoning, or null
     */
    private void addDeniedRequestCard(String dept, String name,
                                      String type, String reason,
                                      String remarks, long requestId,
                                      String aiRecommendation,
                                      String aiReasoning,
                                      String deliveryMethod,
                                      String details) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/view/request/DeniedRequestCard.fxml"));
            VBox card = loader.load();

            DeniedRequestCardController controller = loader.getController();
            if (controller != null) {
                controller.setData(dept, name, type,
                        reason, remarks, requestId,
                        aiRecommendation, aiReasoning);
                controller.setDeliveryMethod(deliveryMethod);
                controller.setStudents(details);
                listContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            System.err.println("Error creating request card.");
            e.printStackTrace();
        }
    }

    /**
     * Handles the "Denied" tab action.
     * Loads denied request data and updates the UI.
     * with corresponding request cards.
     * */
    @FXML
    public void handleDeniedTab() {
        loadDeniedRequestData();
    }

}
