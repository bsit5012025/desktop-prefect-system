package org.rocs.osd.data.dao.request.impl;

import org.rocs.osd.data.connection.ConnectionHelper;
import org.rocs.osd.data.dao.request.RequestDao;
import org.rocs.osd.model.request.Request;
import org.rocs.osd.model.request.RequestStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the RequestDao interface.
 * This class handles database operations for Request objects including:
 * adding a new request, fetching all requests, and updating request status.
 */
public class RequestDaoImpl implements RequestDao {

    /**
     * Adds a new request to the database with status PENDING.
     *
     * @param employeeID the ID of the employee creating the request.
     * @param details the details of the request.
     * @param message the message or reason for the request.
     * @param type the type/category of the request.
     * @return true if the request was successfully added, false otherwise.
     */
    @Override
    public boolean addRequest(String employeeID, String details, String message,
                              String type) {
        String sql =
                "INSERT INTO request (employeeID, "
                        + "details, "
                        + "type, "
                        + "message, "
                        + "status) "
                        + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectionHelper.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, employeeID);
            stmt.setString(2, details);
            stmt.setString(3, type);
            stmt.setString(4, message);
            stmt.setString(5, RequestStatus.PENDING.toString());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("An SQL Exception occurred. " + e.getMessage());
        }

        return false;
    }

    /**
     * Retrieves all requests with the specified status from the database.
     * Each record from the REQUEST table is mapped to a Request object.
     *
     * @param status used to filter requests.
     * @return a List of Request objects representing all requests
     */
    @Override
    public List<Request> findAllRequestsByStatus(RequestStatus status) {
        List<Request> requestList = new ArrayList<>();

        String sql = "SELECT * FROM REQUEST WHERE status = ?";

        try (Connection con = ConnectionHelper.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, String.valueOf(status));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Request r = new Request();
                    r.setRequestID(rs.getLong("requestID"));
                    r.setEmployeeID(rs.getString("employeeID"));
                    r.setDetails(rs.getString("details"));
                    r.setType(rs.getString("type"));
                    r.setMessage(rs.getString("message"));
                    r.setDateProcessed(rs.getDate("dateProcessed"));
                    r.setRemarks(rs.getString("remarks"));
                    r.setStatus(RequestStatus.valueOf(rs.getString("status")));
                    r.setAiResponse(rs.getString("aiResponse"));
                    r.setAiRecommendation(rs.getString("aiRecommendation"));
                    r.setAiReasoning(rs.getString("aiReasoning"));
                    r.setDeliveryMethod(rs.getString("deliveryMethod"));

                    requestList.add(r);
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception (getAllRequests): "
            + e.getMessage());
        }

        return requestList;
    }

    /**
     * Updates the status of a request in the database.
     *
     * @param requestID the ID of the request to update.
     * @param remarks the remarks of user approving or denying the request.
     * @param status the new status to assign to the request.
     * @return true if the update was successful, false otherwise.
     */
    @Override
    public boolean updateRequestStatus(long requestID,
                                       String remarks,
                                       RequestStatus status) {
        String sql = "UPDATE REQUEST SET "
                + "status = ?, "
                + "remarks = ?, "
                + "dateProcessed = SYSDATE "
                + "WHERE requestID = ?";

        try (Connection con = ConnectionHelper.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, status.toString());
            stmt.setString(2, remarks);
            stmt.setLong(3, requestID);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("SQL Exception ("
                    + "updateRequestStatus): " + e.getMessage());
        }
        return false;
    }
}
