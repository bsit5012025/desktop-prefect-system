package org.rocs.osd.facade.document.impl;

import org.rocs.osd.data.dao.document.DocumentDao;
import org.rocs.osd.data.dao.document.impl.DocumentDaoImpl;
import org.rocs.osd.facade.document.DocumentFacade;
import org.rocs.osd.model.document.Document;

/**
 * Facade implementation for retrieving uploaded documents
 * (such as appeal letters) in the Office of Student Discipline system.
 */
public class DocumentFacadeImpl implements DocumentFacade {

    /** DAO for handling document data operations. */
    private final DocumentDao documentDao;

    /**
     * Constructs a facade with a custom DocumentDao implementation.
     * @param pDocumentDao the DocumentDao to use for database operations
     */
    public DocumentFacadeImpl(DocumentDao pDocumentDao) {
        this.documentDao = pDocumentDao;
    }

    /**
     * Constructs a facade with the default DocumentDaoImpl.
     */
    public DocumentFacadeImpl() {
        this.documentDao = new DocumentDaoImpl();
    }

    @Override
    public Document getDocumentById(long documentId) {
        return documentDao.findById(documentId);
    }
}
