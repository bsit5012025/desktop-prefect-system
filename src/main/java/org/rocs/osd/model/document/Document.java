package org.rocs.osd.model.document;

/**
 * Represents an uploaded document (such as a scanned or typed appeal
 * letter) stored in the Office of Student Discipline system.
 */
public class Document {

    /** Unique identifier for the document. */
    private long documentId;

    /** Original filename of the uploaded document. */
    private String fileName;

    /** MIME content type of the uploaded document, e.g. application/pdf. */
    private String contentType;

    /** Raw file bytes of the uploaded document. */
    private byte[] fileData;

    /** Default constructor initializing an empty Document object. */
    public Document() {
        // Default constructor
    }

    /** @return the unique identifier of this document. */
    public long getDocumentId() {
        return documentId;
    }

    /** @param pDocumentId sets the unique identifier of this document. */
    public void setDocumentId(long pDocumentId) {
        this.documentId = pDocumentId;
    }

    /** @return the original filename of the uploaded document. */
    public String getFileName() {
        return fileName;
    }

    /** @param pFileName sets the original filename of the uploaded document. */
    public void setFileName(String pFileName) {
        this.fileName = pFileName;
    }

    /** @return the MIME content type of the uploaded document. */
    public String getContentType() {
        return contentType;
    }

    /** @param pContentType sets the MIME content type of the uploaded document. */
    public void setContentType(String pContentType) {
        this.contentType = pContentType;
    }

    /** @return the raw file bytes of the uploaded document. */
    public byte[] getFileData() {
        return fileData;
    }

    /** @param pFileData sets the raw file bytes of the uploaded document. */
    public void setFileData(byte[] pFileData) {
        this.fileData = pFileData;
    }
}
