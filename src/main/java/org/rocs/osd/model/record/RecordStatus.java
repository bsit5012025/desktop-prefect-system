package org.rocs.osd.model.record;

/**
 * Represents the status of a student record in the
 * Office of Student Discipline System.
 *
 * Mirrors the lifecycle used by the backend/mobile/web:
 * a record starts PENDING, moves to PROCESSING once an appeal is
 * filed against it, and is closed out as either APPROVED (appeal
 * granted) or RESOLVED (appeal denied, or otherwise closed without
 * an appeal).
 */
public enum RecordStatus {

    /** Record is newly created and awaiting action or review. */
    PENDING,

    /** An appeal has been filed against this record and is under review. */
    PROCESSING,

    /** The appeal against this record was approved. */
    APPROVED,

    /** Record has been addressed and resolved (including a denied appeal). */
    RESOLVED
}
