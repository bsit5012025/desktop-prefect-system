package org.rocs.osd.model.request;

import java.sql.Date;

/**
 * Represents a request made by an employee in
 * the Office of Student Discipline System.
 * Stores details such as the employee making the request,
 * request type, message, and current status.
 */
public class Request {

    /** Unique identifier for the request. */
    private long requestID;

    /** ID of the employee who made the request. */
    private String employeeID;

    /** Short details about the request. */
    private String details;

    /** Message or explanation for the request. */
    private String message;

    /** Type or category of the request. */
    private String type;

    /** Current status of the request. */
    private RequestStatus status;

    /** Current Date where request was Processed of the request. */
    private Date dateProcessed;

    /** Remarks of the person approving or denying the request. */
    private String remarks;

    /** Informational AI-generated summary of the matching records (not a recommendation). */
    private String aiResponse;

    /** AI suggestion for this request: APPROVABLE, DENIABLE, or UNCERTAIN. */
    private String aiRecommendation;

    /** Short AI-generated reasoning behind the recommendation. */
    private String aiReasoning;

    /** How the department head wants the result: HARDCOPY or EMAIL. */
    private String deliveryMethod;

    /** Default constructor initializing an empty Request object. */
    public Request() {
        // Default constructor
    }

    /**
     * Constructor to create a Request with all values.
     *
     * @param pRequestID unique identifier of the request.
     * @param pEmployeeID ID of the employee making the request.
     * @param pDetails short pDetails about the request.
     * @param pMessage pMessage or explanation for the request.
     * @param pType pType or category of the request.
     * @param pStatus current pStatus of the request.
     * @param pDateProcessed current pDateProcessed of the request.
     * @param pRemarks current pRemarks of the request.
     */
    public Request(long pRequestID, String pEmployeeID, String pDetails,
                   String pMessage, String pType, RequestStatus pStatus,
                   Date pDateProcessed, String pRemarks) {
        this.requestID = pRequestID;
        this.employeeID = pEmployeeID;
        this.details = pDetails;
        this.message = pMessage;
        this.type = pType;
        this.status = pStatus;
        this.dateProcessed = pDateProcessed;
        this.remarks = pRemarks;
    }

    /** @return unique identifier of the request */
    public long getRequestID() {
        return requestID;
    }

    /** @param pRequestID sets the unique identifier of the request */
    public void setRequestID(long pRequestID) {
        this.requestID = pRequestID;
    }

    /** @return ID of the employee making the request */
    public String getEmployeeID() {
        return employeeID;
    }

    /** @param pEmployeeID sets the ID of the employee making the request */
    public void setEmployeeID(String pEmployeeID) {
        this.employeeID = pEmployeeID;
    }

    /** @return short details about the request */
    public String getDetails() {
        return details;
    }

    /** @param pDetails sets short details about the request */
    public void setDetails(String pDetails) {
        this.details = pDetails;
    }

    /** @return message or explanation of the request */
    public String getMessage() {
        return message;
    }

    /** @param pMessage sets the message or explanation of the request */
    public void setMessage(String pMessage) {
        this.message = pMessage;
    }

    /** @return type or category of the request */
    public String getType() {
        return type;
    }

    /** @param pType sets the type or category of the request */
    public void setType(String pType) {
        this.type = pType;
    }

    /** @return current status of the request */
    public RequestStatus getStatus() {
        return status;
    }

    /** @param pStatus sets the current status of the request */
    public void setStatus(RequestStatus pStatus) {
        this.status = pStatus;
    }

    /** @return The date when the request was processed. */
    public Date getDateProcessed() {
        return dateProcessed;
    }

    /** @param pDateProcessed Sets the date when the request was processed. */
    public void setDateProcessed(Date pDateProcessed) {
        this.dateProcessed = pDateProcessed;
    }

    /**
     *  Getting the remarks of the
     *  request why its approved or denied.
     *
     *  @return current remarks of a request
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Person approving or denying the request.
     *
     * @param pRemarks Sets the remarks of the Request
     */
    public void setRemarks(String pRemarks) {
        this.remarks = pRemarks;
    }

    /** @return the informational AI-generated summary of the matching records. */
    public String getAiResponse() {
        return aiResponse;
    }

    /** @param pAiResponse sets the informational AI-generated summary of the matching records. */
    public void setAiResponse(String pAiResponse) {
        this.aiResponse = pAiResponse;
    }

    /** @return the AI recommendation for this request (APPROVABLE / DENIABLE / UNCERTAIN), or null. */
    public String getAiRecommendation() {
        return aiRecommendation;
    }

    /** @param pAiRecommendation sets the AI recommendation for this request. */
    public void setAiRecommendation(String pAiRecommendation) {
        this.aiRecommendation = pAiRecommendation;
    }

    /** @return how the requester wants the result (HARDCOPY or EMAIL), or null. */
    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    /** @param pDeliveryMethod sets how the requester wants the result. */
    public void setDeliveryMethod(String pDeliveryMethod) {
        this.deliveryMethod = pDeliveryMethod;
    }

    /** @return the short AI-generated reasoning behind the recommendation, or null. */
    public String getAiReasoning() {
        return aiReasoning;
    }

    /** @param pAiReasoning sets the AI-generated reasoning behind the recommendation. */
    public void setAiReasoning(String pAiReasoning) {
        this.aiReasoning = pAiReasoning;
    }

}
