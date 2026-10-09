package org.rocs.osd.data.dao.document.impl;

import org.rocs.osd.data.connection.ConnectionHelper;
import org.rocs.osd.data.dao.document.DocumentDao;
import org.rocs.osd.model.document.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of DocumentDao that handles database operations for
 * uploaded documents (such as appeal letters) in the Office of Student
 * Discipline System.
 */
public class DocumentDaoImpl implements DocumentDao {

    @Override
    public Document findById(long documentId) {
        String sql = "SELECT documentID, fileName, contentType, fileData "
                + "FROM document WHERE documentID = ?";

        try (Connection conn = ConnectionHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, documentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Document document = new Document();
                document.setDocumentId(rs.getLong("documentID"));
                document.setFileName(rs.getString("fileName"));
                document.setContentType(rs.getString("contentType"));
                document.setFileData(rs.getBytes("fileData"));

                return document;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching document by ID", e);
        }
    }
}
