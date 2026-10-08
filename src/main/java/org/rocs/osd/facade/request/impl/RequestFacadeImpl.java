package org.rocs.osd.facade.request.impl;

import org.rocs.osd.data.dao.request.RequestDao;
import org.rocs.osd.facade.request.RequestFacade;
import org.rocs.osd.model.request.Request;
import org.rocs.osd.model.request.RequestStatus;

import java.util.List;

/**
 * Facade implementation for managing requests in the Office of Student
 * Discipline System.
 *
 * Provides methods to add requests, retrieve all
 * requests, and update request status.
 */
public class RequestFacadeImpl implements RequestFacade {

    /**
     * DAO used to interact with request data in the database.
     * */
    private RequestDao requestDao;

    /**
     * Constructor to inject the RequestDao dependency.
     * @param pRequestDao the DAO used to handle request database operations.
     */
    public RequestFacadeImpl(RequestDao pRequestDao) {
        this.requestDao = pRequestDao;
    }

    /**
     * Adds a new request to the system after validating input.
     *
     * @param employeeID the employee submitting the request.
     * @param details brief details about the request.
     * @param message optional message or description.
     * @param type the type of request.
     * @return true if the request was successfully added.
     */
    @Override
    public boolean addRequest(String employeeID,
                              String details,
                              String message,
                              String type) {
        boolean status = validateString(employeeID, 10, false)
                && validateString(details, 100, false)
                && validateString(message, 500, true)
                && validateString(type, 100, false);

        if (status) {
            requestDao.addRequest(employeeID, details, message, type);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Validates a string based on max length and whether null/empty values
     * are allowed.
     *
     * @param s the string to validate.
     * @param maxLength maximum allowed length.
     * @param allowsNull whether null or empty strings are allowed.
     * @return true if valid, false otherwise.
     */
    private boolean validateString(String s,
                                   int maxLength,
                                   boolean allowsNull) {
        if (allowsNull) {
            return !(s != null && s.length() > maxLength);
        } else {
            return !(s == null || s.isBlank() || s.isEmpty()
                    || s.length() > maxLength);
        }
    }

    /**
     * Retrieves all requests by status in the system.
     *
     * @param status used to filter requests.
     * @return a list of all Request objects.
     */
    @Override
    public List<Request> getAllRequestByStatus(RequestStatus status) {
        return requestDao.findAllRequestsByStatus(status);
    }

    /**
     * Updates the status of an existing request.
     *
     * @param requestID the ID of the request to update.
     * @param remarks the remarks of the request to update.
     * @param status the new status of the request.
     * @return true if the status was updated successfully.
     */
    @Override
    public boolean updateRequestStatus(long requestID, String remarks,
                                       RequestStatus status) {
        if (status == null) {
            return false;
        }

        if (remarks != null && remarks.length() > 500) {
            return false;
        }

        return requestDao.updateRequestStatus(requestID, remarks, status);
    }
}
