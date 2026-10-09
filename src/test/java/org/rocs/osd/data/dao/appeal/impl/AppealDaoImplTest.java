package org.rocs.osd.data.dao.appeal.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rocs.osd.data.connection.ConnectionHelper;
import org.rocs.osd.model.appeal.Appeal;
import org.rocs.osd.model.enrollment.Enrollment;
import org.rocs.osd.model.person.student.Student;
import org.rocs.osd.model.record.Record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppealDaoImplTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement appealStatement;

    @Mock
    private PreparedStatement recordStatement;

    @Mock
    private ResultSet resultSet;

    private MockedStatic<ConnectionHelper> connectionHelper;

    private AppealDaoImpl appealDao;

    @BeforeEach
    void setUp() throws SQLException {
        connectionHelper = mockStatic(ConnectionHelper.class);
        connectionHelper.when(ConnectionHelper::getConnection)
                .thenReturn(connection);

        appealDao = new AppealDaoImpl();
    }

    @AfterEach
    void tearDown() {
        connectionHelper.close();
    }

    @Test
    void testFindAppealsByStatus() throws SQLException {
        when(connection.prepareStatement(anyString()))
                .thenReturn(appealStatement);
        when(appealStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);

        when(resultSet.getLong("appealID")).thenReturn(1L);
        when(resultSet.getLong("recordID")).thenReturn(1L);
        when(resultSet.getLong("enrollmentID")).thenReturn(1L);
        when(resultSet.getLong("documentID")).thenReturn(10L);
        when(resultSet.wasNull()).thenReturn(false);

        when(resultSet.getString("message")).thenReturn("Test appeal");
        when(resultSet.getDate("dateFiled"))
                .thenReturn(new java.sql.Date(System.currentTimeMillis()));
        when(resultSet.getString("status")).thenReturn("PENDING");
        when(resultSet.getDate("dateProcessed"))
                .thenReturn(new java.sql.Date(System.currentTimeMillis()));
        when(resultSet.getString("remarks")).thenReturn("Test Remarks");
        when(resultSet.getString("aiRecommendation"))
                .thenReturn("APPROVE");
        when(resultSet.getString("aiReasoning"))
                .thenReturn("The appeal meets the requirements.");
        when(resultSet.getBoolean("edited")).thenReturn(true);

        when(resultSet.getString("studentID")).thenReturn("S001");
        when(resultSet.getString("firstName")).thenReturn("John");
        when(resultSet.getString("lastName")).thenReturn("Doe");
        when(resultSet.getString("offense")).thenReturn("Late Submission");

        List<Appeal> appeals = appealDao.findAppealsByStatus("PENDING");

        assertNotNull(appeals);
        assertEquals(1, appeals.size());

        Appeal appeal = appeals.get(0);

        assertEquals(1L, appeal.getAppealID());
        assertEquals("Test appeal", appeal.getMessage());
        assertEquals("PENDING", appeal.getStatus());
        assertEquals("Test Remarks", appeal.getRemarks());
        assertEquals("APPROVE", appeal.getAiRecommendation());
        assertEquals(
                "The appeal meets the requirements.",
                appeal.getAiReasoning()
        );
        assertEquals(10L, appeal.getDocumentId());
        assertTrue(appeal.isEdited());

        Record record = appeal.getRecord();
        assertNotNull(record);
        assertEquals(1L, record.getRecordId());
        assertEquals("Late Submission", record.getRemarks());

        Enrollment enrollment = appeal.getEnrollment();
        assertNotNull(enrollment);
        assertEquals(1L, enrollment.getEnrollmentId());

        Student student = enrollment.getStudent();
        assertNotNull(student);
        assertEquals("S001", student.getStudentId());
        assertEquals("John", student.getFirstName());
        assertEquals("Doe", student.getLastName());

        verify(connection).prepareStatement(anyString());
        verify(appealStatement).setString(1, "PENDING");
        verify(appealStatement).executeQuery();
    }

    @Test
    void testProcessAppealApproved() throws SQLException {
        when(connection.prepareStatement(anyString()))
                .thenReturn(appealStatement, recordStatement);
        when(appealStatement.executeUpdate()).thenReturn(1);
        when(recordStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() ->
                appealDao.processAppeal(
                        1L, "APPROVED", "Optional remark"
                )
        );

        verify(connection, times(2)).prepareStatement(anyString());

        verify(appealStatement).setString(1, "APPROVED");
        verify(appealStatement).setString(2, "Optional remark");
        verify(appealStatement).setLong(3, 1L);
        verify(appealStatement).executeUpdate();

        verify(recordStatement).setString(1, "APPROVED");
        verify(recordStatement).setLong(2, 1L);
        verify(recordStatement).executeUpdate();
    }

    @Test
    void testProcessAppealDenied() throws SQLException {
        when(connection.prepareStatement(anyString()))
                .thenReturn(appealStatement, recordStatement);

        assertDoesNotThrow(() ->
                appealDao.processAppeal(1L, "DENIED", "Invalid appeal")
        );

        verify(appealStatement).setString(1, "DENIED");
        verify(appealStatement).setString(2, "Invalid appeal");
        verify(appealStatement).setLong(3, 1L);
        verify(appealStatement).executeUpdate();

        verify(recordStatement).setString(1, "RESOLVED");
        verify(recordStatement).setLong(2, 1L);
        verify(recordStatement).executeUpdate();
    }

    @Test
    void testProcessAppealWithNullRemarks() throws SQLException {
        when(connection.prepareStatement(anyString()))
                .thenReturn(appealStatement, recordStatement);

        assertDoesNotThrow(() ->
                appealDao.processAppeal(1L, "APPROVED", null)
        );

        verify(appealStatement).setString(1, "APPROVED");
        verify(appealStatement).setString(2, null);
        verify(appealStatement).setLong(3, 1L);
        verify(appealStatement).executeUpdate();

        verify(recordStatement).setString(1, "APPROVED");
        verify(recordStatement).setLong(2, 1L);
        verify(recordStatement).executeUpdate();
    }
}