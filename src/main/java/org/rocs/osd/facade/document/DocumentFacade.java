package org.rocs.osd.facade.document;

import org.rocs.osd.model.document.Document;

/**
 * Facade interface for retrieving uploaded documents
 * (such as appeal letters) in the Office of Student Discipline system.
 */
public interface DocumentFacade {

    /**
     * Retrieves a document by its unique ID.
     *
     * @param documentId the ID of the document to retrieve
     * @return the Document, or null if not found
     */
    Document getDocumentById(long documentId);
}
