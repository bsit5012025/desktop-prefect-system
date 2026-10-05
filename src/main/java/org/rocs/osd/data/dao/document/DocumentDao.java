package org.rocs.osd.data.dao.document;

import org.rocs.osd.model.document.Document;

/**
 * DAO interface for retrieving uploaded documents
 * (such as appeal letters) from the database.
 */
public interface DocumentDao {

    /**
     * Finds a document by its unique ID.
     *
     * @param documentId the ID of the document to retrieve
     * @return the Document, or null if not found
     */
    Document findById(long documentId);
}
